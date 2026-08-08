package cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 使用次数记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CountRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26883")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "记录使用数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "3894")
    @ExcelProperty("记录使用数量")
    private Long recordCount;

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16245")
    @ExcelProperty("客户端用户id")
    private Long userId;

    @Schema(description = "客户端账号昵称", example = "abc")
    @ExcelProperty("客户端账号昵称")
    private String clientUserName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
