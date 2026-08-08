package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientUsersUserInfoRespVO {

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27964")
    @ExcelProperty("客户端用户id")
    private Long id;

    @Schema(description = "客户端用户名称", example = "赵六")
    @ExcelProperty("客户端用户名称")
    private String nickname;

    @Schema(description = "客户端用户头像")
    @ExcelProperty("客户端用户头像")
    private String clientAvator;

    @Schema(description = "客户端账号", example = "芋艿")
    @ExcelProperty("客户端账号")
    private String clientUsername;

    @Schema(description = "客户端账号角色", example = "教师")
    @ExcelProperty("客户端账号角色")
    private String clientRole;

}
