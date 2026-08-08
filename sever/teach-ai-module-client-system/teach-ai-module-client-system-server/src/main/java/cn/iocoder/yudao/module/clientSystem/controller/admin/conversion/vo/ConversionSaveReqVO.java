package cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 会话历史新增/修改 Request VO")
@Data
public class ConversionSaveReqVO {

    @Schema(description = "会话历史id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5832")
    private Long id;

    @Schema(description = "会话id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21232")
    @NotEmpty(message = "会话id不能为空")
    private String conversionId;

    @Schema(description = "客户端账号id", requiredMode = Schema.RequiredMode.REQUIRED, example = "13463")
    @NotNull(message = "客户端账号id不能为空")
    private Long clientUserId;

    @Schema(description = "会话标题")
    private String title;

}
