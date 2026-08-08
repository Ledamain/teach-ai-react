package cn.iocoder.teach-ai.module.clientChat.api.exercises;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.exercises.dto.ExamPaperDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 试卷相关")
public interface ExercisesApi {

    String PREFIX = ApiConstants.PREFIX + "/exercise";

    @GetMapping(PREFIX + "/getExamPaper")
    @Operation(summary = "获得试卷")
    @Parameter(name = "query", description = "试卷要求", example = "1024", required = true)
    CommonResult<ExamPaperDTO> getExamPaper(@RequestParam("query") String query);

    @GetMapping(PREFIX + "/getExamPaperJSON")
    @Operation(summary = "获得试卷JSON")
    @Parameter(name = "query", description = "试卷要求", example = "1024", required = true)
    CommonResult<String> getExamPaperJSON(@RequestParam("query") String query);

}
