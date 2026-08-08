package cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 客户端用户新增/修改 Request VO")
@Data
public class UserSaveReqVO {

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

    @Schema(description = "客户端用户角色")
    private String clientRole;

    @Schema(description = "客户端用户性别")
    private String clientGender;

    @Schema(description = "学号/工号")
    private String clientNum;

    @Schema(description = "手机号")
    private String clientTel;

    @Schema(description = "最后登陆时间")
    private LocalDateTime lastLoginTime;

}
