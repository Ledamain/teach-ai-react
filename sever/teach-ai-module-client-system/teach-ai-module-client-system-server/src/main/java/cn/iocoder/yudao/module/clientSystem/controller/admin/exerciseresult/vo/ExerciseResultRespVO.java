package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 评判结果 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExerciseResultRespVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32356")
    @ExcelProperty("主键id")
    private Long id;

    @Schema(description = "关联题目", requiredMode = Schema.RequiredMode.REQUIRED, example = "26160")
    @ExcelProperty("关联题目")
    private Long exerciseId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30342")
    @ExcelProperty("用户ID")
    private Long studentUserId;

    @Schema(description = "成绩单")
    @ExcelProperty("成绩单")
    private String transcript;

    @Schema(description = "是否完成")
    @ExcelProperty("是否完成")
    private Long completed;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "学生用户名")
    private String studentUserName;

    @Schema(description = "班级名")
    private String classesName;

}
