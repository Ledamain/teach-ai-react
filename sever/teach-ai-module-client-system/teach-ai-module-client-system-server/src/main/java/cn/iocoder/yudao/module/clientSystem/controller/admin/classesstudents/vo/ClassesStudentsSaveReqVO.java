package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 班级学生新增/修改 Request VO")
@Data
public class ClassesStudentsSaveReqVO {

    @Schema(description = "班级学生id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28041")
    private Long id;

    @Schema(description = "班级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "30467")
    @NotNull(message = "班级id不能为空")
    private Long classesId;

    @Schema(description = "学生用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "23567")
    @NotNull(message = "学生用户id不能为空")
    private Long studentUserId;

}
