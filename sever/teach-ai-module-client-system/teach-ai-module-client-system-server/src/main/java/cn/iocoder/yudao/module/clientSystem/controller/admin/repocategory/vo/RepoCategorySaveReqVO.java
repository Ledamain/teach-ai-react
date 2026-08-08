package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 知识库类别新增/修改 Request VO")
@Data
public class RepoCategorySaveReqVO {

    @Schema(description = "知识库类别id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29118")
    private Long id;

    @Schema(description = "知识库类别名称", example = "李四")
    private String repoCategoryName;

    @Schema(description = "课程组id", example = "1")
    private Long courseGroupId;

    @Schema(description = "教师id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29118")
    private Long teacherUserId;

}
