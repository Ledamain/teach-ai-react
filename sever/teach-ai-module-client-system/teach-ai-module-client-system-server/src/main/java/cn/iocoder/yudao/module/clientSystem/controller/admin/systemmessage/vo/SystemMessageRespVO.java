package cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.teach-ai.framework.excel.core.annotations.DictFormat;
import cn.iocoder.teach-ai.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 系统提示词 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SystemMessageRespVO {

    @Schema(description = "提示词主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "20886")
    @ExcelProperty("提示词主键id")
    private Long id;

    @Schema(description = "系统提示词标题")
    @ExcelProperty("系统提示词标题")
    private String systemMessageTitle;

    @Schema(description = "提示词内容")
    @ExcelProperty("提示词内容")
    private String systemMessageText;

    @Schema(description = "系统提示词文件url地址", example = "https://www.iocoder.cn")
    @ExcelProperty("系统提示词文件url地址")
    private String systemMessageTextUrl;

    @Schema(description = "启用状态0-启用1-禁止", example = "1")
    @ExcelProperty(value = "启用状态0-启用1-禁止", converter = DictConvert.class)
    @DictFormat("chat_system_message_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "提示词是否存在0-存在1-不存在", example = "2")
    @ExcelProperty(value = "提示词是否存在0-存在1-不存在", converter = DictConvert.class)
    @DictFormat("chat_system_message_text") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String textStatus;

}
