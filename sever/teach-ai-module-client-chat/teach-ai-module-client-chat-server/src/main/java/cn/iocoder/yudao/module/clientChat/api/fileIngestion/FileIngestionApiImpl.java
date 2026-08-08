package cn.iocoder.teach-ai.module.clientChat.api.fileIngestion;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.MemoryFileSummaryRespDTO;
import cn.iocoder.teach-ai.module.clientChat.service.fileIngestion.FileIngestionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Valid
@Slf4j
public class FileIngestionApiImpl implements FileIngestionApi {

    @Resource
    private FileIngestionService fileIngestionService;

    @Override
    public CommonResult<Boolean> fileIngest(FileIngestionDTO fileIngestionDTO) {
        try {
            fileIngestionService.ingestFile(fileIngestionDTO);
            return CommonResult.success(true);
        } catch (Exception e) {
            return CommonResult.error(500, "解析失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Boolean> deleteEmbeddingById(String id) {
        fileIngestionService.deleteEmbeddingById(id);
        return CommonResult.success(true);
    }

    // 新增：批量按ID删除
    @Override
    public CommonResult<Boolean> deleteEmbeddingByIds(List<String> ids) {
        fileIngestionService.deleteEmbeddingByIds(ids);
        return CommonResult.success(true);
    }

    // 按kb_id删除
    @Override
    public CommonResult<Boolean> deleteByKbId(String kbId, String fileName) {
        log.info("第一步骤：知识库id：{},知识库名字：{}", kbId,fileName);
        fileIngestionService.deleteByKbIdAndFileName(kbId,fileName);
        return CommonResult.success(true);
    }

    // 按memory_id删除
    @Override
    public CommonResult<Boolean> deleteByMemoryId(String memoryId, String fileName) {
        fileIngestionService.deleteByMemoryIdAndFileName(memoryId,fileName);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<MemoryFileSummaryRespDTO> getFileSummary(String memoryId) {
        return CommonResult.success(fileIngestionService.getSummaryByMemoryId(memoryId));
    }
}
