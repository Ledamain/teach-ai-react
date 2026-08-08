package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * RAG 子图 — 知识库优先，无结果时 web 兜底。
 *
 * 流程:
 *   START → init → retrieve → gradeDocuments
 *                      ↓ 文档不足
 *                    web_search → generate → rag_output → END
 *                      ↓ 文档充足
 *                    generate → rag_output → END
 */
@Service
@Slf4j
public class RagAgent {

    /** 无有效结果的固定标记，Supervisor 据此路由 coder */
    public static final String NO_RESULT_MARKER = "[RAG_NO_RESULT]";

    /** 答案过短的最小字符数，低于此阈值视为无有效结果 */
    private static final int MIN_ANSWER_LENGTH = 80;

    /** rag 产出无法回答文本的匹配模式 */
    private static final String[] NO_RESULT_PATTERNS = {
        "未找到", "无法", "无相关", "没有找到", "未能找到",
        "无法直接", "缺少", "缺乏", "没有足够", "无法提供",
        "建议您提供", "不包含", "无具体", "无法直接帮助",
        "缺乏具体的", "没有找到相关信息", "未找到相关信息",
        "无法帮助", "不能帮助", "无法为您", "无法直接回答",
        "无法回答", "不清楚", "不确定",
        "不太明确", "请补充", "不太清楚", "能否提供"
    };

    @Resource private WebSearchAgent          webSearchAgent;
    @Resource private RetrieveAgent           retrieveAgent;
    @Resource private GradeDocumentsAgent     gradeDocumentsAgent;
    @Resource private GenerateAgent           generateAgent;

    private StateGraph<State> ragGraph;

    @PostConstruct
    public void init() throws Exception {
        ragGraph = buildRagGraph();
        log.info("RAG 子图 (KB 优先 + web 兜底) 构建完成");
    }

    public StateGraph<State> getStateGraph() {
        return ragGraph;
    }

    private StateGraph<State> buildRagGraph() throws Exception {
        return new StateGraph<>(State.SCHEMA, State::new)

                .addNode("init", node_async(state -> {
                    var question = state.messages().stream()
                            .filter(m -> m.type() == ChatMessageType.USER)
                            .map(m -> ((UserMessage) m).singleText())
                            .reduce((first, second) -> second)
                            .orElse("");
                    log.info("RAG 收到: {}", question.length() > 50 ? question.substring(0, 50) + "..." : question);
                    return Map.of(
                        "ragQuestion", question,
                        "ragRetryCount", 0
                    );
                }))

                .addNode("retrieve",        node_async(retrieveAgent))
                .addNode("gradeDocuments",  node_async(gradeDocumentsAgent))
                .addNode("web_search",      node_async(webSearchAgent))
                .addNode("generate",        node_async(generateAgent))

                .addNode("rag_output", node_async(state -> {
                    var answer = state.ragGeneration().orElse("未找到相关信息");
                    log.info("RAG 输出, 答案长度: {}", answer.length());
                    if (isNoResult(answer)) {
                        log.info("RAG 无有效结果 → 输出固定标记 [RAG_NO_RESULT]");
                        answer = NO_RESULT_MARKER;
                    }
                    return Map.of(
                        "messages", AiMessage.from(answer),
                        "retrievedDocuments", state.ragDocuments()
                    );
                }))

                .addEdge(START, "init")
                .addEdge("init", "retrieve")
                .addEdge("retrieve", "gradeDocuments")

                .addConditionalEdges("gradeDocuments",
                        edge_async(state -> {
                            var docs = state.ragDocuments();
                            var retry = state.ragRetryCount();
                            if (docs != null && !docs.isEmpty() && !docs.get(0).contains("未找到相关文档")) {
                                log.info("RAG: 知识库命中 {} 条文档 → generate", docs.size());
                                return "generate";
                            }
                            if (retry >= 1) {
                                log.info("RAG: 知识库无结果且已重试{}次 → generate(降级兜底)", retry);
                                return "generate";
                            }
                            log.info("RAG: 知识库无结果 → web_search (第{}次)", retry + 1);
                            return "web_search";
                        }),
                        Map.of("generate",   "generate",
                               "web_search", "web_search"))

                .addEdge("web_search", "generate")
                .addEdge("generate", "rag_output")
                .addEdge("rag_output", END);
    }

    /** 判断生成结果是否为"无法回答"类型 */
    static boolean isNoResult(String text) {
        if (text == null || text.isBlank()) return true;
        // 长度过短 → 必然无有效教学内容
        if (text.length() < MIN_ANSWER_LENGTH) {
            log.debug("RAG isNoResult: 答案过短({}<{}) → 无有效结果", text.length(), MIN_ANSWER_LENGTH);
            return true;
        }
        for (var pat : NO_RESULT_PATTERNS) {
            if (text.contains(pat)) return true;
        }
        return false;
    }
}
