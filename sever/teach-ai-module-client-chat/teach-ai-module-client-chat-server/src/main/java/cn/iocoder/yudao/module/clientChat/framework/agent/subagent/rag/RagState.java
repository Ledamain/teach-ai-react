package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptive RAG 子图的 State。
 * 父图用 MessagesState（对话流），子图用业务字段（检索流），两者独立。
 */
public class RagState extends AgentState {

    static Map<String, Channel<?>> SCHEMA = Map.of(
            "documents", Channels.appender(ArrayList::new)
    );

    public RagState(Map<String, Object> initData) {
        super(initData);
    }

    public String question() {
        return this.<String>value("question")
                .orElseThrow(() -> new IllegalStateException("question is not set"));
    }

    @SuppressWarnings("unchecked")
    public List<String> documents() {
        return this.<List<String>>value("documents").orElse(List.of());
    }

    public Optional<String> generation() {
        return value("generation");
    }
}
