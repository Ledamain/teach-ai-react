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
 * RAG 子图节点 — 改写问题以优化检索效果。
 */
@Service
@Slf4j
public class TransformQueryAgent implements NodeAction<State> {

    private final ChatModel chatModel;

    public TransformQueryAgent(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Map<String, Object> apply(State state) {
        log.debug("---TRANSFORM QUERY---");
        var question = state.ragQuestion();

        var messages = List.<ChatMessage>of(
                SystemMessage.from("""
                    你是一个问题改写器。将输入的问题改写为更适合向量检索的版本。
                    分析原始问题的语义意图，生成一个更精准的查询。
                    只输出改写后的问题，不要加任何解释。
                    """),
                UserMessage.from("原始问题: " + question + "\n\n改写后的问题:")
        );
        var response = chatModel.chat(messages);
        var rewritten = response.aiMessage().text().trim();

        log.debug("改写: {} -> {}", question, rewritten);
        return Map.of("ragQuestion", rewritten, "ragRetryCount", state.ragRetryCount() + 1);
    }
}
