package cn.iocoder.teach-ai.module.clientChat.api.wordCloud;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 词云")
public interface WordCloudApi {

    String PREFIX = ApiConstants.PREFIX + "/word-cloud";

    @GetMapping(PREFIX + "/gen")
    @Operation(summary = "生成热门词列表")
    @Parameter(name = "prompt", description = "标题列表", example = "abc", required = true)
    CommonResult<String> wordCloudGen(@RequestParam("prompt") String prompt);

}
