package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 练习题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExerciseInfoRespVO {

    @Schema(description = "习题id", requiredMode = Schema.RequiredMode.REQUIRED, example = "12784")
    @ExcelProperty("习题id")
    private Long id;

    @Schema(description = "班级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "9298")
    @ExcelProperty("班级id")
    private String classesId;

    @Schema(description = "班级id列表", example = "111")
    private List<String> classesIdsList;

    @Schema(description = "知识库类别名称", example = "李四")
    private String repoCategoryName;

    @Schema(description = "客户端用户名称", example = "赵六")
    private String nickname;

    @Schema(description = "课程id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10899")
    @ExcelProperty("课程id")
    private Long repoCategoryId;

    @Schema(description = "出题人id", requiredMode = Schema.RequiredMode.REQUIRED, example = "23788")
    @ExcelProperty("出题人id")
    private Long teacherUserId;

    @Schema(description = "作业名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("作业名称")
    private String exerciseName;

    @Schema(description = "题目内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("题目内容")
    private String content;

    @Schema(description = "用户得分", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用户得分")
    private Integer userScore;

    @Schema(description = "是否完成")
    private Long completed;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "题目总分")
    private Integer totalScore;

    @Schema(description = "开始时间")
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "题目状态", example = "2")
    @ExcelProperty("题目状态")
    private Long status;

    @Schema(description = "已提交人数", example = "2")
    private Long submissionCount;

    @Schema(description = "总人数", example = "2")
    private Long totalStudents;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
