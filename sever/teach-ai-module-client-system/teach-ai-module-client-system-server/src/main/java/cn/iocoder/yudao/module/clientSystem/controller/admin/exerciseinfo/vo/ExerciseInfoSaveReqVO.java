package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 练习题新增/修改 Request VO")
@Data
public class ExerciseInfoSaveReqVO {

    @Schema(description = "习题id", requiredMode = Schema.RequiredMode.REQUIRED, example = "12784")
    private Long id;

    @Schema(description = "班级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "9298")
    private String classesId;

    @Schema(description = "课程id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10899")
    @NotNull(message = "课程id不能为空")
    private Long repoCategoryId;

    @Schema(description = "出题人id", requiredMode = Schema.RequiredMode.REQUIRED, example = "23788")
    @NotNull(message = "出题人id不能为空")
    private Long teacherUserId;

    @Schema(description = "作业名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "作业名称不能为空")
    private String exerciseName;

    @Schema(description = "题目内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "题目状态", example = "2")
    private Long status;

}
