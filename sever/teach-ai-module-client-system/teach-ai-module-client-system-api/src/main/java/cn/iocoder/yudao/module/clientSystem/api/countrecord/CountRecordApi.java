package cn.iocoder.teach-ai.module.clientSystem.api.countrecord;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.dto.CountRecordDTO;
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

import java.util.List;

@FeignClient(name = ApiConstants.NAME, primary = false) // ① @FeignClient 注解，primary = false 避免与本地实现冲突
@Tag(name = "RPC 服务 - 管理员用户") // ② Swagger 接口文档
public interface CountRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/countrecord";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建会话历史")
    CommonResult<Long> createCountRecord(@Valid @RequestBody CountRecordDTO countRecordDTO);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得使用次数")
    CommonResult<CountRecordDTO> getCountRecordById(@RequestParam("id") Long id);

    @PostMapping(PREFIX + "/update")
    @Operation(summary = "更新使用次数")
    CommonResult<Boolean> updateCountRecord(@Valid @RequestBody CountRecordDTO countRecordDTO);


}
