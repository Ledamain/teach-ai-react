package cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户端登录日志分页 Request VO")
@Data
public class LoginLogPageReqVO extends PageParam {

    @Schema(description = "用户名", example = "赵六")
    private String username;

    @Schema(description = "用户名称", example = "芋艿")
    private String nickname;

    @Schema(description = "登录时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] loginTime;

    @Schema(description = "登录结果", example = "0")
    private Long loginResult;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
