package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 评判结果新增/修改 Request VO")
@Data
public class ExerciseResultSaveReqVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32356")
    private Long id;

    @Schema(description = "关联题目", requiredMode = Schema.RequiredMode.REQUIRED, example = "26160")
    private Long exerciseId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30342")
    private Long studentUserId;

    @Schema(description = "成绩单")
    private String transcript;

    @Schema(description = "是否完成")
    private Long completed;

}
