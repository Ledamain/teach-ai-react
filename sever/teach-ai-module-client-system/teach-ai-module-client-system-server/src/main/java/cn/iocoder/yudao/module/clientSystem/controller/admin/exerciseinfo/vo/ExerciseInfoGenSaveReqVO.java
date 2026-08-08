package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 练习题新增/修改 Request VO")
@Data
public class ExerciseInfoGenSaveReqVO extends ExerciseInfoSaveReqVO {

    @Schema(description = "要求（提示词）", requiredMode = Schema.RequiredMode.REQUIRED, example = "12784")
    private String description;

}
