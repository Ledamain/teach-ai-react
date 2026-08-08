package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 评判结果分页 Request VO")
@Data
public class ExerciseResultPageReqVO extends PageParam {

    @Schema(description = "关联题目", example = "26160")
    private Long exerciseId;

    @Schema(description = "用户ID", example = "30342")
    private Long studentUserId;

    @Schema(description = "成绩单")
    private String transcript;

    @Schema(description = "是否完成")
    private Long completed;

    @Schema(description = "课程id")
    private Long repoCategoryId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
