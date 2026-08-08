package cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 课程组 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseGroupRespVO {

    @Schema(description = "课程组id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27382")
    @ExcelProperty("课程组id")
    private Long id;

    @Schema(description = "课程组名称", example = "王五")
    @ExcelProperty("课程组名称")
    private String courseGroupName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
