package cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - PPT历史记录分页 Request VO")
@Data
public class PptHistoryPageReqVO extends PageParam {

    @Schema(description = "ppt生成记录标题")
    private String pptTitle;

    @Schema(description = "ppt文件")
    private String pptFile;

    @Schema(description = "文件类型")
    private String pptFiletype;

    @Schema(description = "客户端用户id", example = "27147")
    private Long clientUserId;

    @Schema(description = "客户端用户名称", example = "27147")
    private String nickname;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
