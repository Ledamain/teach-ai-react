package cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 系统提示词新增/修改 Request VO")
@Data
public class SystemMessageSaveReqVO {

    @Schema(description = "提示词主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "20886")
    private Long id;

    @Schema(description = "系统提示词标题")
    private String systemMessageTitle;

    @Schema(description = "提示词内容")
    private String systemMessageText;

    @Schema(description = "系统提示词文件url地址", example = "https://www.iocoder.cn")
    private String systemMessageTextUrl;

    @Schema(description = "启用状态0-启用1-禁止", example = "1")
    private String status;

    @Schema(description = "提示词是否存在0-存在1-不存在", example = "2")
    private String textStatus;

}
