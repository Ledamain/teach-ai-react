package cn.iocoder.teach-ai.module.clientSystem.api.systemmessage;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.dto.SystemMessageDTO;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ApiConstants.NAME, primary = false) // ① @FeignClient 注解，primary = false 避免与本地实现冲突
@Tag(name = "RPC 服务 - 管理员用户") // ② Swagger 接口文档
public interface SystemMessageApi {

    String PREFIX = ApiConstants.PREFIX + "/system-message";

    @PostMapping(PREFIX + "/list")
    @Operation(summary = "获得系统提示词列表")
    CommonResult<List<SystemMessageDTO>> getSystemMessageList(@Valid @RequestBody SystemMessageDTO messageDTO);

}
