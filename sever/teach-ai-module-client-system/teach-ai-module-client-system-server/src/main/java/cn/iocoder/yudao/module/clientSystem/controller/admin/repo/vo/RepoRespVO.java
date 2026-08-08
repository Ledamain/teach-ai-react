package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 知识库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RepoRespVO {

    @Schema(description = "知识库id", requiredMode = Schema.RequiredMode.REQUIRED, example = "14187")
    @ExcelProperty("知识库id")
    private Long id;

    @Schema(description = "知识库标题")
    @ExcelProperty("知识库标题")
    private String repoTitle;

    @Schema(description = "知识库文件链接")
    @ExcelProperty("知识库文件链接")
    private String repoFile;

    @Schema(description = "知识库描述")
    @ExcelProperty("知识库描述")
    private String repoDesp;

    @Schema(description = "知识库类别id", requiredMode = Schema.RequiredMode.REQUIRED, example = "489")
    @ExcelProperty("知识库类别id")
    private Long repoCategoryId;

    @Schema(description = "知识库类别名称", example = "489")
    @ExcelProperty("知识库类别名称")
    private String repoCategoryName;

    @Schema(description = "学科文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "489")
    @ExcelProperty("学科文件夹id")
    private Long repoGroupId;

    @Schema(description = "学科文件夹名称", example = "489")
    @ExcelProperty("学科文件夹名称")
    private String repoGroupName;

    @Schema(description = "启用状态", example = "2")
    @ExcelProperty("启用状态")
    private String repoStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
