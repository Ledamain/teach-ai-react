package cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PptHistoryDTO {

    @Schema(description = "ppt生成记录id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25035")
    private Long id;

    @Schema(description = "ppt生成记录标题")
    private String pptTitle;

    @Schema(description = "ppt文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ppt文件不能为空")
    private String pptFile;

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27147")
    @NotNull(message = "客户端用户id不能为空")
    private Long clientUserId;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件类型不能为空")
    private String pptFiletype;

}

