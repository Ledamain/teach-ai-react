package cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 会话历史 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ConversionRespVO {

    @Schema(description = "会话历史id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5832")
    @ExcelProperty("会话历史id")
    private Long id;

    @Schema(description = "会话id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21232")
    @ExcelProperty("会话id")
    private String conversionId;

    @Schema(description = "客户端账号id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13463")
    @ExcelProperty("客户端账号id")
    private Long clientUserId;

    @Schema(description = "客户端账号昵称", example = "abc")
    @ExcelProperty("客户端账号昵称")
    private String clientUserName;

    @Schema(description = "会话标题")
    @ExcelProperty("会话标题")
    private String title;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
