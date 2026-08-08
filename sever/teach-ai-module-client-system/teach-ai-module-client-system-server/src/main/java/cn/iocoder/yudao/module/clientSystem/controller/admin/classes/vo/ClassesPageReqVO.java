package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 班级分页 Request VO")
@Data
public class ClassesPageReqVO extends PageParam {

    @Schema(description = "班级名称", example = "王五")
    private String classesName;

    @Schema(description = "教师用户id", example = "14519")
    private Long teacherUserId;

    @Schema(description = "教师", example = "王五")
    private String nickname;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
