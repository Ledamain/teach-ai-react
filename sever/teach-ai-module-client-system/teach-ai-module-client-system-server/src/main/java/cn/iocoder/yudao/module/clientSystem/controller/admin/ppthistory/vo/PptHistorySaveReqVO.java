package cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - PPT历史记录新增/修改 Request VO")
@Data
public class PptHistorySaveReqVO {

    @Schema(description = "ppt生成记录id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25035")
    private Long id;

    @Schema(description = "ppt生成记录标题")
    private String pptTitle;

    @Schema(description = "ppt文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ppt文件不能为空")
    private String pptFile;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件类型不能为空")
    private String pptFiletype;

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27147")
    @NotNull(message = "客户端用户id不能为空")
    private Long clientUserId;

}
