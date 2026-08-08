package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG 子图节点 — 网络搜索。
 * 调用 Tavily API，返回搜索结果作为文档供 generate 节点消费。
 */
@Service
@Slf4j
public class WebSearchAgent implements NodeAction<State> {

    private final String tavilyApiKey;

    public WebSearchAgent(@Value("${tavily.api-key}") String tavilyApiKey) {
        this.tavilyApiKey = tavilyApiKey;
    }

    @Override
    public Map<String, Object> apply(State state) {
        log.debug("---WEB SEARCH---");
        var question = state.ragQuestion();

        var webSearchEngine = TavilyWebSearchEngine.builder()
                .apiKey(tavilyApiKey)
                .build();

        var webRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(3)
                .build();

        var results = webRetriever.retrieve(new Query(question));
        var webResult = results.stream()
                .map(c -> c.textSegment().text())
                .collect(Collectors.joining("\n"));

        log.debug("网络搜索返回 {} 条结果", results.size());
        return Map.of("ragDocuments", List.of(webResult));
    }
}
