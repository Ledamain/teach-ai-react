package cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 使用次数记录分页 Request VO")
@Data
public class CountRecordPageReqVO extends PageParam {

    @Schema(description = "记录使用数量", example = "3894")
    private Long recordCount;

    @Schema(description = "客户端用户id", example = "16245")
    private Long userId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
