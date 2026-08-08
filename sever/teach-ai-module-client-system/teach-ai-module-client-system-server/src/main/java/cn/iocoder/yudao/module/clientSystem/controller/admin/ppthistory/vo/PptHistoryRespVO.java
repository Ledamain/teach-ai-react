package cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - PPT历史记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PptHistoryRespVO {

    @Schema(description = "ppt生成记录id", requiredMode = Schema.RequiredMode.REQUIRED, example = "25035")
    @ExcelProperty("ppt生成记录id")
    private Long id;

    @Schema(description = "ppt生成记录标题")
    @ExcelProperty("ppt生成记录标题")
    private String pptTitle;

    @Schema(description = "ppt文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("ppt文件")
    private String pptFile;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pptFiletype;

    @Schema(description = "客户端用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27147")
    @ExcelProperty("客户端用户id")
    private Long clientUserId;

    @Schema(description = "客户端用户名称", example = "王五")
    @ExcelProperty("客户端用户名称")
    private String nickname;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
