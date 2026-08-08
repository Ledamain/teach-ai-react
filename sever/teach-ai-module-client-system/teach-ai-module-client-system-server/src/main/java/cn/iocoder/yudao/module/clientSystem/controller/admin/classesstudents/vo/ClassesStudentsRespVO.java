package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 班级学生 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClassesStudentsRespVO {

    @Schema(description = "班级学生id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28041")
    @ExcelProperty("班级学生id")
    private Long id;

    @Schema(description = "班级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "30467")
    @ExcelProperty("班级id")
    private Long classesId;

    @Schema(description = "学生用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "23567")
    @ExcelProperty("学生用户id")
    private Long studentUserId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
