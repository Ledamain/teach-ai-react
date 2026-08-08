package cn.iocoder.teach-ai.module.clientChat.api.digitalvideo;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoFileDTO;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoReqDTO;
import cn.iocoder.teach-ai.module.clientChat.api.exercises.dto.ExamPaperDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 数字人视频相关")
public interface DigitalVideoApi {

    String PREFIX = ApiConstants.PREFIX + "/digital-human-video";

    @PostMapping(value = PREFIX + "/upload-pptx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传pptx")
    @Parameter(name = "query", description = "试卷要求", example = "1024", required = true)
    CommonResult<String> uploadPptx(@ModelAttribute DigitalVideoFileDTO dto);

    @GetMapping(PREFIX + "/get-result")
    @ResponseBody
    CommonResult<String> getDigitalVideoResult(@RequestParam String taskId);

    @PostMapping(PREFIX + "/generate-subtitles")
    @ResponseBody
    CommonResult<String> GenerateSubtitles(@RequestBody DigitalVideoReqDTO digitalVideoReqDTO);

}
