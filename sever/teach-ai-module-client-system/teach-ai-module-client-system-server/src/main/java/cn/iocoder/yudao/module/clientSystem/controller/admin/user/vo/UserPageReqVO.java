package cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户端用户分页 Request VO")
@Data
public class UserPageReqVO extends PageParam {

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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastLoginTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
