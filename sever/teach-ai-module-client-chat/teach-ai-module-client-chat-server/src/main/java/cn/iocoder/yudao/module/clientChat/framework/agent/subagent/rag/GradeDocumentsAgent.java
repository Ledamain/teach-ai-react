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
 * RAG 子图节点 — LLM 逐条打分，过滤不相关文档。
 */
@Service
@Slf4j
public class GradeDocumentsAgent implements NodeAction<State> {

    private final ChatModel chatModel;

    public GradeDocumentsAgent(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Map<String, Object> apply(State state) {
        log.debug("---GRADE DOCUMENTS---");
        var question = state.ragQuestion();
        var docs = state.ragDocuments();

        var filtered = docs.stream()
                .filter(doc -> isRelevant(question, doc))
                .toList();

        log.debug("过滤后剩余 {} 篇 (原始 {})", filtered.size(), docs.size());
        return Map.of("ragDocuments", filtered);
    }

    private boolean isRelevant(String question, String doc) {
        var messages = List.<ChatMessage>of(
                SystemMessage.from("""
                    你是一个文档相关性评估器。判断检索到的文档是否与用户问题相关。
                    不需要严格匹配，只要文档包含与问题相关的关键词或语义即可。
                    只回答 "yes" 或 "no"。
                    """),
                UserMessage.from("用户问题: " + question + "\n\n文档内容: " + doc)
        );
        var response = chatModel.chat(messages);
        return response.aiMessage().text().trim().toLowerCase().contains("yes");
    }
}
