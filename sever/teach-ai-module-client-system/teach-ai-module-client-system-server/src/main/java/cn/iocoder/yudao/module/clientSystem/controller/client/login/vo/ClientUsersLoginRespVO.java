package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 登录 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUsersLoginRespVO {

    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "happy")
    private String accessToken;

    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "nice")
    private String refreshToken;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

    @Schema(description = "用户信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private ClientUsersUserInfoRespVO userInfo;

}
