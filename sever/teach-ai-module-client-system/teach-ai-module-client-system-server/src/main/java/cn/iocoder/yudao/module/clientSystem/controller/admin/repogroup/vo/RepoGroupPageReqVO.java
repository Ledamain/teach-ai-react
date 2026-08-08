package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程文件夹分页 Request VO")
@Data
public class RepoGroupPageReqVO extends PageParam {

    @Schema(description = "课程文件夹名称", example = "张三")
    private String repoGroupName;

    @Schema(description = "学科id", example = "17668")
    private Long repoCategoryId;

    @Schema(description = "学科", example = "17668")
    private String repoCategoryName;

    @Schema(description = "课程文件夹描述", example = "17668")
    private String repoGroupDescription;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
