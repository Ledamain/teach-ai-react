package cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 系统提示词分页 Request VO")
@Data
public class SystemMessagePageReqVO extends PageParam {

    @Schema(description = "系统提示词标题")
    private String systemMessageTitle;

    @Schema(description = "提示词内容")
    private String systemMessageText;

    @Schema(description = "系统提示词文件url地址", example = "https://www.iocoder.cn")
    private String systemMessageTextUrl;

    @Schema(description = "启用状态0-启用1-禁止", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "提示词是否存在0-存在1-不存在", example = "2")
    private String textStatus;

}
