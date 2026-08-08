package cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会话历史分页 Request VO")
@Data
public class ConversionPageReqVO extends PageParam {

    @Schema(description = "会话id", example = "21232")
    private String conversionId;

    @Schema(description = "客户端账号id", example = "13463")
    private Long clientUserId;

    @Schema(description = "客户端账号昵称", example = "abc")
    private String clientUserName;

    @Schema(description = "会话标题")
    private String title;

}
