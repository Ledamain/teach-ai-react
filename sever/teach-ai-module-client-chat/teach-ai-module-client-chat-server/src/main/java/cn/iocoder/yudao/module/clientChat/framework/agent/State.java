package cn.iocoder.teach-ai.module.clientChat.framework.agent;

import dev.langchain4j.data.message.ChatMessage;
import org.bsc.langgraph4j.langchain4j.serializer.std.LC4jStateSerializer;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.serializer.StateSerializer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class State extends MessagesState<ChatMessage> {

    // ── RAG 子图字段 ──
    public String ragQuestion() {
        return this.<String>value("ragQuestion").orElse("");
    }

    @SuppressWarnings("unchecked")
    public List<String> ragDocuments() {
        return this.<List<String>>value("ragDocuments").orElse(List.of());
    }

    public Optional<String> ragGeneration() {
        return value("ragGeneration");
    }

    /** RAG 子图检索重试次数，防死循环 */
    public int ragRetryCount() {
        return this.<Integer>value("ragRetryCount").orElse(0);
    }

    public Optional<String> next() {
        return this.value("next");
    }

    /** reviewer 退回 coder 的次数，防死循环 */
    public int reviewRetryCount() {
        return this.<Integer>value("reviewRetryCount").orElse(0);
    }

    /** reviewer 的审查意见，coder 据此修正 */
    public Optional<String> reviewFeedback() {
        return value("reviewFeedback");
    }

    public boolean isClarification() {
        return this.<Boolean>value("isClarification").orElse(false);
    }

    /** rag 检索到的文档，供调试/可视化 */
    @SuppressWarnings("unchecked")
    public List<String> retrievedDocuments() {
        return this.<List<String>>value("retrievedDocuments").orElse(List.of());
    }

    public State(Map<String, Object> initData) {
        super(initData);
    }

    public static StateSerializer<State> serializer() {
        return new LC4jStateSerializer<>(State::new);
    }

    public int loopCount() {
        return this.<Integer>value("loopCount").orElse(0);
    }

    /** 代码生成模式: coder → reviewer → FINISH，不回 supervisor */
    public boolean codeGenMode() {
        return this.<Boolean>value("codeGenMode").orElse(false);
    }

    public Optional<String> summary() {
        return value("summary");
    }
}
