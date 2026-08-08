package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 课程文件夹 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RepoGroupRespVO {

    @Schema(description = "课程文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    @ExcelProperty("课程文件夹id")
    private Long id;

    @Schema(description = "课程文件夹名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("课程文件夹名称")
    private String repoGroupName;

    @Schema(description = "学科id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17668")
    @ExcelProperty("学科id")
    private Long repoCategoryId;

    @Schema(description = "学科", example = "17668")
    @ExcelProperty("学科")
    private String repoCategoryName;
    @Schema(description = "课程文件夹描述", example = "17668")
    private String repoGroupDescription;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
