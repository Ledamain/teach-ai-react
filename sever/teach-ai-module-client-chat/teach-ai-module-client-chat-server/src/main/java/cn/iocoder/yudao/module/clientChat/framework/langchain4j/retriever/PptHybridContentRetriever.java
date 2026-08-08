package cn.iocoder.teach-ai.module.clientChat.framework.langchain4j.retriever;

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
import java.util.stream.Collectors;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Slf4j
public class PptHybridContentRetriever implements ContentRetriever {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public PptHybridContentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Content> retrieve(Query query) {
        String pptMemoryId = ClientUserContext.getCurrentMemoryId();
        log.info("当前文件上传pptMemoryId：{}", pptMemoryId);

        // 🌟 新增逻辑：如果 kbId 和 memoryId 都为空，直接返回空列表
        // 这样 Langchain4j 就不会带着上下文去请求大模型，而是直接让大模型自己回答
        if (pptMemoryId == null) {
            return Collections.emptyList();
        }

        // 1. 获取查询向量 (放在判空之后，可以节省不必要的 Embedding 调用开销)
        dev.langchain4j.data.embedding.Embedding queryEmbedding =
                embeddingModel.embed(truncateQueryForEmbedding(query.text())).content();

        // 2. 动态构建组合过滤器
        Filter fileFilter = metadataKey("memory_id").isEqualTo(pptMemoryId);

        // 3. 执行搜索请求
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .filter(fileFilter)
                .maxResults(5)
                .minScore(0.5)
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(searchRequest);

        // 如果向量数据库查不到内容，这里也会正常返回空列表，大模型同样会直接回答
        if (result.matches() == null || result.matches().isEmpty()) {
            log.info("向量数据库没有匹配结果，直接返回空列表");
            return Collections.emptyList();
        }

        return result.matches().stream()
                .map(match -> Content.from(match.embedded().text()))
                .collect(Collectors.toList());
    }

    /** 截断 query 文本，确保不超过 embedding 模型的 2048 token 限制 */
    private static String truncateQueryForEmbedding(String text) {
        if (text == null || text.isBlank()) return "";
        // 保守估计中文约 1 字符/token，1024 字符远低于 2048 token 上限
        final int MAX_CHARS = 1024;
        if (text.length() <= MAX_CHARS) return text;
        return text.substring(0, MAX_CHARS);
    }
}
