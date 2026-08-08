package cn.iocoder.teach-ai.module.clientSystem.controller.client.digital;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.digital.vo.DigitalHumanRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service.impl.ClientMiniJwtServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 数字人")
@RestController
@RequestMapping("/client-api/client-system/digital-human")
@Validated
public class ClientDigitalHumanController {

    @Resource
    private ClientMiniJwtServiceImpl clientMiniJwtService;

    @Value("${digital-human.default.conversation-id}")
    private String conversationId;

    @Value("${digital-human.app-id}")
    private String appId;

    @GetMapping("/create")
    @Operation(summary = "创建数字人聊天")
    public CommonResult<DigitalHumanRespVO> createDigitalHumanChat() {
        String sign = clientMiniJwtService.createDigitalToken();
        return success(new DigitalHumanRespVO().setSign(sign).setConversationId(conversationId));
    }

    @GetMapping("/close")
    @Operation(summary = "关闭数字人聊天")
    public CommonResult<Boolean> closeDigitalHumanChat(@RequestParam String sign) {
        HttpResponse<String> response = Unirest.get("https://api.duix.ai/duix-openapi-v2/sdk/v2/distroyCallSessionsByAppId?appId="+appId)
                .header("token", sign)
                .asString();
        return success(true);
    }

}
