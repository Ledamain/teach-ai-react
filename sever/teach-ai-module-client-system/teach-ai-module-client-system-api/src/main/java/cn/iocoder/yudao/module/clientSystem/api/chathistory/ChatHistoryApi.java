package cn.iocoder.teach-ai.module.clientSystem.api.chathistory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.api.chathistory.dto.ChatHistoryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = ApiConstants.NAME, primary = false) // ① @FeignClient 注解，primary = false 避免与本地实现冲突
@Tag(name = "RPC 服务 - 管理员用户") // ② Swagger 接口文档
public interface ChatHistoryApi {

    String PREFIX = ApiConstants.PREFIX + "/chat-history";

    @GetMapping(PREFIX + "/get-client-login-user")
    @Operation(summary = "获得客户端当前登录信息")
    CommonResult<ChatHistoryRespDTO> getClientLoginUser();

}
