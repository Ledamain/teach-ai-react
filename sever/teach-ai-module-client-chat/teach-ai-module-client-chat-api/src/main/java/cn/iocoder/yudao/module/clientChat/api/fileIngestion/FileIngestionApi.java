package cn.iocoder.teach-ai.module.clientChat.api.fileIngestion;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.MemoryFileSummaryRespDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 对话历史")
public interface FileIngestionApi {

    String PREFIX = ApiConstants.PREFIX + "/knowledge";

    @PostMapping(value = PREFIX + "/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件解析成知识")
    CommonResult<Boolean> fileIngest(@ModelAttribute FileIngestionDTO fileIngestionDTO);

    @DeleteMapping(value =PREFIX + "/delete")
    @Operation(summary = "按ID删除指定向量数据")
    CommonResult<Boolean> deleteEmbeddingById(@RequestParam String id);

    @DeleteMapping(value =PREFIX + "/delete/batch")
    @Operation(summary = "批量按ID删除向量数据")
    CommonResult<Boolean> deleteEmbeddingByIds(@RequestBody List<String> ids);

    // 按kb_id删除
    @DeleteMapping(value = PREFIX + "/delete/kb")
    @Operation(summary = "按知识库ID删除所有向量数据")
    CommonResult<Boolean> deleteByKbId(
            @RequestParam("kbId") String kbId,
            @RequestParam(value = "fileName", required = false) String fileName);


    // 按memory_id删除
    @DeleteMapping(value =PREFIX + "/delete/memory")
    @Operation(summary = "按会话ID删除所有向量数据")
    CommonResult<Boolean> deleteByMemoryId(@RequestParam String memoryId, String fileName);

    @GetMapping(value = PREFIX +"/file-summary")
    CommonResult<MemoryFileSummaryRespDTO> getFileSummary(@RequestParam String memoryId);
}
