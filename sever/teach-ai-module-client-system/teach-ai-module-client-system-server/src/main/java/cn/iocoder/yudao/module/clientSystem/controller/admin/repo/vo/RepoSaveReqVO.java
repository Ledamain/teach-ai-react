package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 知识库新增/修改 Request VO")
@Data
public class RepoSaveReqVO {

    @Schema(description = "知识库id", requiredMode = Schema.RequiredMode.REQUIRED, example = "14187")
    private Long id;

    @Schema(description = "知识库标题")
    private String repoTitle;

    @NotNull(message = "知识库文件链接不能为空")
    @Schema(description = "知识库文件链接")
    private String repoFile;

    @Schema(description = "知识库描述")
    private String repoDesp;

    @Schema(description = "知识库类别id", requiredMode = Schema.RequiredMode.REQUIRED, example = "489")
    @NotNull(message = "知识库类别id不能为空")
    private Long repoCategoryId;

    @Schema(description = "学科文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "489")
    @NotNull(message = "学科文件夹id不能为空")
    private Long repoGroupId;

    @Schema(description = "启用状态", example = "2")
    private String repoStatus;

}
