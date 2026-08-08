package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 知识库类别分页 Request VO")
@Data
public class RepoCategoryPageReqVO extends PageParam {

    @Schema(description = "知识库类别名称", example = "李四")
    private String repoCategoryName;

    @Schema(description = "教师id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29118")
    private Long teacherUserId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
