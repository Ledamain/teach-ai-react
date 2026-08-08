package cn.iocoder.teach-ai.module.clientChat.api.hotquery;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 热点气泡相关")
public interface HotQueryApi {

    String PREFIX = ApiConstants.PREFIX + "/hot-query";

    @GetMapping(PREFIX + "/get-hot-query")
    @Operation(summary = "获得热点气泡")
    @Parameter(name = "query", description = "热点气泡", example = "1024", required = true)
    CommonResult<String> getHotQuery();
}
