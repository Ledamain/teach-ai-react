package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 知识库分页 Request VO")
@Data
public class RepoPageReqVO extends PageParam {

    @Schema(description = "知识库标题")
    private String repoTitle;

    @Schema(description = "知识库文件链接")
    private String repoFile;

    @Schema(description = "知识库描述")
    private String repoDesp;

    @Schema(description = "知识库类别id", example = "489")
    private Long repoCategoryId;

    @Schema(description = "学科文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "489")
    private Long repoGroupId;

    @Schema(description = "启用状态", example = "2")
    private String repoStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
