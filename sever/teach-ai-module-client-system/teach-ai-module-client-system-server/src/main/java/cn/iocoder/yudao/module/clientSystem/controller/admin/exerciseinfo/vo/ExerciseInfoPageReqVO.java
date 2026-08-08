package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 练习题分页 Request VO")
@Data
public class ExerciseInfoPageReqVO extends PageParam {

    @Schema(description = "班级id", example = "9298")
    private String classesId;

    @Schema(description = "课程id", example = "10899")
    private Long repoCategoryId;

    @Schema(description = "出题人id", example = "23788")
    private Long teacherUserId;

    @Schema(description = "学生用户id", example = "23788")
    private Long studentUserId;

    @Schema(description = "作业名称", example = "芋艿")
    private String exerciseName;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "题目状态", example = "2")
    private Long status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
