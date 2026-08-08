package cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 客户端用户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserRespVO {

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

    @Schema(description = "客户端账号密码")
    @ExcelProperty("客户端账号密码")
    private String clientPassword;

    @Schema(description = "客户端用户角色")
    @ExcelProperty("客户端用户角色")
    private String clientRole;

    @Schema(description = "客户端用户性别")
    @ExcelProperty("客户端用户性别")
    private String clientGender;

    @Schema(description = "学号/工号")
    @ExcelProperty("学号/工号")
    private String clientNum;

    @Schema(description = "用户昵称-学号")
    private String nicknameWithNum;

    @Schema(description = "手机号")
    @ExcelProperty("手机号")
    private String clientTel;

    @Schema(description = "最后登陆时间")
    @ExcelProperty("最后登陆时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
