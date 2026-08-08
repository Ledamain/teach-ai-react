package cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 客户端登录日志新增/修改 Request VO")
@Data
public class LoginLogSaveReqVO {

    @Schema(description = "登录日志id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8615")
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(description = "用户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "用户名称不能为空")
    private String nickname;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "登录时间不能为空")
    private LocalDateTime loginTime;

    @Schema(description = "登录IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "登录IP不能为空")
    private String loginIp;

    @Schema(description = "登录结果", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "登录结果不能为空")
    private Long loginResult;

}
