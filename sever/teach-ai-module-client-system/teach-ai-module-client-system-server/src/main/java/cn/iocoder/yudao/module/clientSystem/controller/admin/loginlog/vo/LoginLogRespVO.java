package cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.teach-ai.framework.excel.core.annotations.DictFormat;
import cn.iocoder.teach-ai.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 客户端登录日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class LoginLogRespVO {

    @Schema(description = "登录日志id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8615")
    @ExcelProperty("登录日志id")
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("用户名")
    private String username;

    @Schema(description = "用户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("用户名称")
    private String nickname;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "登录结果", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "登录结果", converter = DictConvert.class)
    @DictFormat("client_login_result") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Long loginResult;

    @Schema(description = "登录IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "登录IP不能为空")
    private String loginIp;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
