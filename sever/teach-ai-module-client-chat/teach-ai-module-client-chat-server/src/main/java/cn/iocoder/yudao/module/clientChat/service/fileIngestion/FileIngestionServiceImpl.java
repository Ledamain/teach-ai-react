package cn.iocoder.teach-ai.module.clientChat.service.fileIngestion;

import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestSummaryDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.MemoryFileSummaryRespDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.milvus.grpc.QueryResults;
import io.milvus.param.dml.QueryParam;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.google.protobuf.ByteString;
import io.milvus.grpc.FieldData;
import io.milvus.grpc.QueryResults;
import io.milvus.param.dml.QueryParam;
import java.util.LinkedHashMap;
import java.util.Map;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.fastjson.JSON;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.MutationResult;
import io.milvus.param.R;
import io.milvus.param.dml.DeleteParam;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.KNOWLEDGE_SEGMENTATION_FAILURE;

@Slf4j
@Service
public class FileIngestionServiceImpl implements FileIngestionService {

    @Value("${langchain4j.community.milvus.collection-name}")
    private String collectionName;

    @Value("${dashscope.api-key}") // 请在配置文件中添加此配置
    private String dashscopeApiKey;

    @Value("${dashscope.multimodal-name}")
    private String multimodalName;

    @Resource
    private MilvusEmbeddingStore milvusEmbeddingStore;
    @Resource
    private EmbeddingModel embeddingModel;
    @Resource
    private DocumentSplitter documentSplitter;
    @Resource
    private MilvusServiceClient milvusClient;

    @PostConstruct
    public void init() {
        // 初始化阿里云 SDK
        Constants.apiKey = dashscopeApiKey;
    }

    @Override
    public void ingestFile(FileIngestionDTO fileIngestionDTO) {

        log.info("开始处理文件：{}", fileIngestionDTO.getFile().getOriginalFilename());

        MultipartFile file = fileIngestionDTO.getFile();
        String memoryId = fileIngestionDTO.getMemoryId();
        String kbId = fileIngestionDTO.getKId();

        // 获取文件名并转小写，防止空指针
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename != null ? originalFilename.toLowerCase() : "";

        try {
            Document document;

            // ====================== 新增：判断并处理图片/视频 ======================
            if (isMultimediaFile(fileName)) {
                log.info("检测到多模态文件，开始调用 Qwen-Omni 解析：{}", fileName);
                document = processMultimediaToDocument(fileIngestionDTO, fileName);
            } else {
                // ====================== 原有：文本/文档处理逻辑 ======================
                DocumentParser parser;

                if (fileName.endsWith(".pdf")) {
                    parser = new ApachePdfBoxDocumentParser();
                } else if (fileName.endsWith(".docx") || fileName.endsWith(".doc") ||
                        fileName.endsWith(".xlsx") || fileName.endsWith(".xls") ||
                        fileName.endsWith(".pptx") || fileName.endsWith(".ppt")) {
                    parser = new ApachePoiDocumentParser();
                } else if (fileName.endsWith(".txt") || fileName.endsWith(".md") || fileName.endsWith(".csv")) {
                    parser = new TextDocumentParser();
                } else {
                    throw new IllegalArgumentException("当前系统暂不支持解析该文件格式: " + fileName);
                }

                document = parser.parse(file.getInputStream());
            }

            // ====================== 保持一致：注入元数据 ======================
            document.metadata().put("file_name", fileName);
            if (memoryId != null) {
                document.metadata().put("memory_id", memoryId);
            }
            if (kbId != null) {
                document.metadata().put("kb_id", kbId);
            }

            // ====================== 保持一致：执行切割、向量化、存储 ======================
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(documentSplitter)
                    .embeddingModel(embeddingModel)
                    .embeddingStore(milvusEmbeddingStore)
                    .build();

            ingestor.ingest(document);

        } catch (Exception e) {
            log.info("知识库切分失败，原因：{}", e.getMessage(), e);
            throw exception(KNOWLEDGE_SEGMENTATION_FAILURE);
        }
    }

    // ====================== 新增私有方法区域 ======================

    /**
     * 调用 Qwen-Omni 将图片/视频转换为文本 Document
     */
    private Document processMultimediaToDocument(FileIngestionDTO fileIngestionDTO, String fileName){
        try {

            log.info("切分的文件url： {}",fileIngestionDTO.getFileUrl());
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap(isVideoOrImage(fileName), fileIngestionDTO.getFileUrl()),
                            Collections.singletonMap("text", "图中包含哪些教学知识?"))).build();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(System.getenv(dashscopeApiKey))
                    .model(multimodalName)
                    .messages(Arrays.asList(userMessage))
                    .build();
            MultiModalConversationResult result = conv.call(param);
            String text = result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
            return Document.from(text);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是否为支持的图片或视频格式
     */
    private boolean isMultimediaFile(String fileName) {
        return fileName.endsWith(".mp4")
                || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png") || fileName.endsWith(".gif")
                || fileName.endsWith(".bmp") || fileName.endsWith(".webp");
    }

    private String isVideoOrImage(String fileName){
        return fileName.endsWith(".mp4") ? "video" : "image";
    }

    // ====================== 以下原有删除逻辑保持完全不变 ======================

    @Override
    public void deleteEmbeddingById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("删除ID不能为空");
        }
        milvusEmbeddingStore.remove(id);
    }

    @Override
    public void deleteEmbeddingByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("删除ID列表不能为空");
        }
        milvusEmbeddingStore.removeAll(ids);
    }

    @Override
    public void deleteByKbIdAndFileName(String kbId, String fileName) {
        log.info("获取到的知识库id：{},文件名：{}", kbId, fileName);
        String expr = String.format("metadata[\"kb_id\"] == \"%s\" && metadata[\"file_name\"] == \"%s\"", kbId, fileName);
        System.out.println("准备执行删除的 Expr: " + expr);
        R<MutationResult> response = milvusClient.delete(DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build());
        checkAndDeleteLog(response);
    }

    @Override
    public void deleteByMemoryIdAndFileName(String memoryId, String fileName) {
        String expr = String.format("metadata[\"memory_id\"] == \"%s\" && metadata[\"file_name\"] == \"%s\"", memoryId, fileName);
        R<MutationResult> response = milvusClient.delete(DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build());
        checkAndDeleteLog(response);
    }

    @Override
    public void deleteByFileName(String fileName) {
        String expr = String.format("metadata[\"file_name\"] == \"%s\"", fileName);
        R<MutationResult> response = milvusClient.delete(DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build());
        checkAndDeleteLog(response);
    }

    private void checkAndDeleteLog(R<MutationResult> response) {
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.err.println("Milvus 删除请求执行失败: " + response.getMessage());
        } else {
            long deleteCount = response.getData().getDeleteCnt();
            if (deleteCount > 0) {
                System.out.println("✅ 成功删除了 " + deleteCount + " 条向量数据！");
            } else {
                System.out.println("⚠️ 删除了 0 条数据。说明 Expr 没有在库里匹配到任何记录，请检查表达式或参数！");
            }
        }
    }

    @Override
    public MemoryFileSummaryRespDTO getSummaryByMemoryId(String memoryId) {
        if (memoryId == null || memoryId.isBlank()) {
            return MemoryFileSummaryRespDTO.builder()
                    .memoryId(memoryId).files(List.of()).totalFiles(0).totalChunks(0).build();
        }

        String expr = String.format("metadata[\"memory_id\"] == \"%s\"", memoryId);
        R<QueryResults> response = milvusClient.query(QueryParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .withOutFields(List.of("text", "metadata"))
                .withLimit(500L)
                .build());

        if (response.getStatus() != R.Status.Success.getCode()) {
            log.warn("Milvus 查询失败: {}", response.getMessage());
            return MemoryFileSummaryRespDTO.builder()
                    .memoryId(memoryId).files(List.of()).totalFiles(0).totalChunks(0).build();
        }

        QueryResults results = response.getData();
        List<FieldData> columns = results.getFieldsDataList();
        if (columns.isEmpty()) {
            return MemoryFileSummaryRespDTO.builder()
                    .memoryId(memoryId).files(List.of()).totalFiles(0).totalChunks(0).build();
        }

        // 构建字段名 → FieldData 的映射（列式结构）
        Map<String, FieldData> colMap = new LinkedHashMap<>();
        for (FieldData col : columns) {
            colMap.put(col.getFieldName(), col);
        }
        FieldData textCol = colMap.get("text");
        FieldData metaCol = colMap.get("metadata");
        if (textCol == null) {
            return MemoryFileSummaryRespDTO.builder()
                    .memoryId(memoryId).files(List.of()).totalFiles(0).totalChunks(0).build();
        }

        // 获取 text 列的行数
        int rowCount = textCol.getScalars().getStringData().getDataCount();

        // 按 file_name 分组
        Map<String, List<String>> fileChunkMap = new LinkedHashMap<>();
        for (int i = 0; i < rowCount; i++) {
            String text = textCol.getScalars().getStringData().getData(i);
            String fileName = "未知文件";

            if (metaCol != null) {
                try {
                    ByteString jsonBytes = metaCol.getScalars().getJsonData().getData(i);
                    JSONObject metaJson = JSON.parseObject(jsonBytes.toStringUtf8());
                    String fn = metaJson.getString("file_name");
                    if (fn != null && !fn.isBlank()) fileName = fn;
                } catch (Exception e) {
                    log.warn("解析第{}行 metadata 失败: {}", i, e.getMessage());
                }
            }

            fileChunkMap.computeIfAbsent(fileName, k -> new ArrayList<>()).add(text);
        }

        // 汇总
        List<FileIngestSummaryDTO> files = new ArrayList<>();
        int totalChunks = 0;
        for (var entry : fileChunkMap.entrySet()) {
            List<String> chunks = entry.getValue();
            totalChunks += chunks.size();
            String allText = String.join(" ", chunks);
            String summary = allText.length() > 500 ? allText.substring(0, 500) + "..." : allText;
            String fileType = entry.getKey().contains(".")
                    ? entry.getKey().substring(entry.getKey().lastIndexOf('.') + 1)
                    : "unknown";

            files.add(FileIngestSummaryDTO.builder()
                    .fileName(entry.getKey())
                    .fileType(fileType)
                    .chunkCount(chunks.size())
                    .summary(summary)
                    .keyPoints(extractSimpleKeyPoints(allText))
                    .build());
        }

        return MemoryFileSummaryRespDTO.builder()
                .memoryId(memoryId)
                .files(files)
                .totalFiles(files.size())
                .totalChunks(totalChunks)
                .build();
    }

    /** 简单关键词提取：按中文标点分句后取长度适中的短句 */
    private List<String> extractSimpleKeyPoints(String text) {
        if (text == null || text.isBlank()) return List.of();
        String[] sentences = text.split("[。；\\n]");
        return java.util.Arrays.stream(sentences)
                .map(String::trim)
                .filter(s -> s.length() > 6 && s.length() < 60)
                .limit(5)
                .collect(Collectors.toList());
    }
}
