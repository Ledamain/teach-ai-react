package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import dev.langchain4j.data.message.ChatMessage;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * RAG 子图节点 — 基于文档生成答案。
 */
@Service
@Slf4j
public class GenerateAgent implements NodeAction<State> {

    private final ChatModel chatModel;

    public GenerateAgent(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Map<String, Object> apply(State state) {
        log.debug("---GENERATE---");
        var question = state.ragQuestion();
        var docs = state.ragDocuments();

        // 无文档时直接返回，不浪费 LLM 调用
        if (docs.isEmpty() || docs.stream().allMatch(String::isBlank)) {
            log.debug("无有效文档，直接返回未找到");
            return Map.of("ragGeneration", "未找到相关信息");
        }

        var context = String.join("\n\n", docs);
        var messages = List.<ChatMessage>of(
                SystemMessage.from("""
                    你是一个问答助手。使用以下检索到的上下文来回答用户问题。
                    如果上下文不足以回答问题，请明确说明。
                    保持回答简洁，最多三句话。
                    """),
                UserMessage.from("问题: " + question + "\n\n上下文:\n" + context)
        );
        var response = chatModel.chat(messages);
        var generation = response.aiMessage().text();

        log.debug("生成答案: {}", generation.substring(0, Math.min(80, generation.length())));
        return Map.of("ragGeneration", generation);
    }
}
