package cn.iocoder.teach-ai.module.clientSystem.api.conversion;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME, primary = false) // ① @FeignClient 注解，primary = false 避免与本地实现冲突
@Tag(name = "RPC 服务 - 管理员用户") // ② Swagger 接口文档
public interface ConversionApi {

    String PREFIX = ApiConstants.PREFIX + "/conversion";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建会话历史")
    CommonResult<Long> createConversion(@Valid @RequestBody ConversionDTO conversionDTO);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得会话历史")
    CommonResult<ConversionDTO> getConversionByConversionId(@RequestParam("conversionId") Long conversionId);

    @PostMapping(PREFIX + "/update")
    @Operation(summary = "更新会话历史")
    CommonResult<Boolean> updateConversion(@Valid @RequestBody ConversionDTO conversionDTO);


}
