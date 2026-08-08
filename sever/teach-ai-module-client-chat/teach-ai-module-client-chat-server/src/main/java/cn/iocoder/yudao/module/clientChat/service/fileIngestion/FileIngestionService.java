package cn.iocoder.teach-ai.module.clientChat.service.fileIngestion;

import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.MemoryFileSummaryRespDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileIngestionService {

    void ingestFile(FileIngestionDTO fileIngestionDTO) throws IOException;

    // 新增：按ID删除指定向量数据
    void deleteEmbeddingById(String id);

    // 新增：批量按ID删除
    void deleteEmbeddingByIds(List<String> ids);

    // 按kb_id删除整个知识库的所有向量
    void deleteByKbIdAndFileName(String kbId, String fileName);

    // 按memory_id删除整个会话的所有向量
    void deleteByMemoryIdAndFileName(String memoryId, String fileName);

    // 只按文件名删除
    void deleteByFileName(String fileName);

    MemoryFileSummaryRespDTO getSummaryByMemoryId(String memoryId);
}
