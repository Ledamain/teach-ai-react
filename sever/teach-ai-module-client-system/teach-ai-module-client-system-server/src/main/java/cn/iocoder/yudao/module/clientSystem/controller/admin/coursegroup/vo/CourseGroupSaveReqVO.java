package cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 课程组新增/修改 Request VO")
@Data
public class CourseGroupSaveReqVO {

    @Schema(description = "课程组id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27382")
    private Long id;

    @Schema(description = "课程组名称", example = "王五")
    private String courseGroupName;

}
