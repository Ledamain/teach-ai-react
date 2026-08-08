package cn.iocoder.teach-ai.module.clientSystem.api.chathistory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "RPC 服务 - 聊天历史 Response DTO")
@Data
public class ChatHistoryRespDTO {

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27964")
    private Long id;

    @Schema(description = "客户端用户名称", example = "赵六")
    private String nickname;

    @Schema(description = "客户端用户头像")
    private String clientAvator;

    @Schema(description = "客户端账号", example = "芋艿")
    private String clientUsername;

    @Schema(description = "客户端账号密码")
    private String clientPassword;

    @Schema(description = "最后登陆时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
