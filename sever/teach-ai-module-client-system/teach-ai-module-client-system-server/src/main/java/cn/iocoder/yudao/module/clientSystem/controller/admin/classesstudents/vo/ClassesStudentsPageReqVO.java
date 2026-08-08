package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 班级学生分页 Request VO")
@Data
public class ClassesStudentsPageReqVO extends PageParam {

    @Schema(description = "班级id", example = "30467")
    private Long classesId;

    @Schema(description = "学生用户id", example = "23567")
    private Long studentUserId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
