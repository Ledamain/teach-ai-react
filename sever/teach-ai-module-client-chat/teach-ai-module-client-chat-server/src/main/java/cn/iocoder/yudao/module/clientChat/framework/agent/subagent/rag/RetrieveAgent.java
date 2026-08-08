package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import dev.langchain4j.rag.content.Content;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RAG 子图节点 — 向量库检索。
 */
@Service
@Slf4j
public class RetrieveAgent implements NodeAction<State> {

    @Resource
    private ContentRetriever contentRetriever;

    @Override
    public Map<String, Object> apply(State state) {
        var question = state.ragQuestion();
        log.debug("---RETRIEVE---");

        var results = contentRetriever.retrieve(Query.from(question));
        var docs = results.stream()
                .map(Content::textSegment)
                .map(ts -> ts.text())
                .toList();

        log.debug("检索到 {} 篇文档", docs.size());
        return Map.of("ragDocuments", docs);
    }
}
