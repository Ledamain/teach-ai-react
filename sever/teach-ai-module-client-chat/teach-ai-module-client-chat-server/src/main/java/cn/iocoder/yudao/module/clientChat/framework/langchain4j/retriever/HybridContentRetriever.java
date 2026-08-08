package cn.iocoder.teach-ai.module.clientChat.framework.langchain4j.retriever;

import cn.iocoder.teach-ai.module.clientChat.service.aiService.ChatStreamEvent;
import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Slf4j
public class HybridContentRetriever implements ContentRetriever {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public HybridContentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Content> retrieve(Query query) {
        List<String> kbids = ClientUserContext.getCurrentKids();
        String memoryId = ClientUserContext.getCurrentMemoryId();
        log.info("当前文件上传memoryId：{}",memoryId);
        log.info("当前知识库kbId：{}", kbids);

        //  新增逻辑：如果 kbId 和 memoryId 都为空，直接返回空列表
        // 这样 Langchain4j 就不会带着上下文去请求大模型，而是直接让大模型自己回答
        if (CollectionUtils.isEmpty(kbids) && !StringUtils.hasText(memoryId)) {
            return Collections.emptyList();
        }

        // 1. 获取查询向量 (放在判空之后，可以节省不必要的 Embedding 调用开销)
        dev.langchain4j.data.embedding.Embedding queryEmbedding =
                embeddingModel.embed(query.text()).content();

        // 2. 动态构建组合过滤器 (核心：OR 逻辑)
        Filter filter = null;
        List<Filter> filters = new ArrayList<>();

        if (!CollectionUtils.isEmpty(kbids)) {
            filters.add(metadataKey("kb_id").isIn(kbids));
        }
        if (StringUtils.hasText(memoryId)) {
            filters.add(metadataKey("memory_id").isEqualTo(memoryId));
        }

        // 组合 Filter
        if (filters.size() == 2) {
            filter = Filter.or(filters.get(0), filters.get(1));
        } else if (filters.size() == 1) {
            filter = filters.get(0);
        }

        // 3. 执行搜索请求
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .filter(filter)
                .maxResults(10) // 增加结果数以覆盖两个来源
                .minScore(0.5)
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(searchRequest);

        // 如果向量数据库查不到内容，这里也会正常返回空列表，大模型同样会直接回答
        if (result.matches() == null || result.matches().isEmpty()) {
            log.info("向量数据库没有匹配结果，直接返回空列表");
            return Collections.emptyList();
        }

        List<ChatStreamEvent.RagChunk> ragChunks = result.matches().stream()
                .map(match -> {
                    TextSegment seg = match.embedded();
                    return ChatStreamEvent.RagChunk.builder()
                            .fileId(seg.metadata().getString("file_id"))
                            .fileName(seg.metadata().getString("file_name"))
                            .text(seg.text())
                            .score(match.score())
                            .build();
                })
                .collect(Collectors.toList());
        ClientUserContext.setRagChunks(memoryId, ragChunks);
        log.info("RAG 检索完成，暂存 {} 个文本块", ragChunks.size());

        return result.matches().stream()
                .map(match -> Content.from(match.embedded().text()))
                .collect(Collectors.toList());
    }
}
