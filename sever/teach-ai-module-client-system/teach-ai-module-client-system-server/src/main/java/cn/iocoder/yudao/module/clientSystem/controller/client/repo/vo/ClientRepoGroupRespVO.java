package cn.iocoder.teach-ai.module.clientSystem.controller.client.repo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "客户端 - 课程文件夹")
@Data
public class ClientRepoGroupRespVO {

    @Schema(description = "课程文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    private Long id;

    @Schema(description = "知识库文件夹名称", example = "张三")
    private String repoGroupName;

    @Schema(description = "学科id", example = "17668")
    private Long repoCategoryId;

    @Schema(description = "学科", example = "17668")
    private String repoCategoryName;

    @Schema(description = "课程文件夹描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    private String repoGroupDescription;

    @Schema(description = "文件夹文件数量", example = "1")
    private Integer fileCount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] updateTime;

}
