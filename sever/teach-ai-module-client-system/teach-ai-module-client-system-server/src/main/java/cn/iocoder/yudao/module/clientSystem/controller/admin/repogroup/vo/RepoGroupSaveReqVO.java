package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 课程文件夹新增/修改 Request VO")
@Data
public class RepoGroupSaveReqVO {

    @Schema(description = "课程文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    private Long id;

    @Schema(description = "课程文件夹名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "课程文件夹名称不能为空")
    private String repoGroupName;

    @Schema(description = "学科id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17668")
    @NotNull(message = "学科id不能为空")
    private Long repoCategoryId;

    @Schema(description = "课程文件夹描述", example = "17668")
    @NotEmpty(message = "课程文件夹描述不能为空")
    private String repoGroupDescription;

}
