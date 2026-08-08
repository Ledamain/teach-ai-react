package cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 使用次数记录新增/修改 Request VO")
@Data
public class CountRecordSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26883")
    private Long id;

    @Schema(description = "记录使用数量", example = "3894")
    private Long recordCount;

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16245")
    @NotNull(message = "客户端用户id不能为空")
    private Long userId;

}
