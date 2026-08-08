package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;

@Schema(description = "管理后台 - 班级新增/修改 Request VO")
@Data
public class ClassesSaveReqVO {

    @Schema(description = "班级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5869")
    private Long id;

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "班级名称不能为空")
    private String classesName;

    @Schema(description = "教师用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "14519")
    @NotNull(message = "教师用户id不能为空")
    private Long teacherUserId;

    @Schema(description = "知识库类别ids", requiredMode = Schema.RequiredMode.REQUIRED, example = "1,2,3")
    private String repoCategoryIds;

    @Schema(description = "班级学生列表")
    private List<ClassesStudentsDO> classesStudentss;

}
