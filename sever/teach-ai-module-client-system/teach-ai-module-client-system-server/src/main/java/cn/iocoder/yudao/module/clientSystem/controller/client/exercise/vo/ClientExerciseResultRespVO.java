package cn.iocoder.teach-ai.module.clientSystem.controller.client.exercise.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo.ExerciseInfoRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.ExerciseResultRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientExerciseResultRespVO extends ExerciseResultRespVO {

    @Schema(description = "姓名",  example = "12784")
    @ExcelProperty("作答学生姓名")
    private String studentUserName;

    @Schema(description = "班级名称",  example = "12784")
    @ExcelProperty("班级名称")
    private String classesName;

    @Schema(description = "学号",  example = "12784")
    @ExcelProperty("学号")
    private String ClientNum;

    @Schema(description = "得分",  example = "100")
    @ExcelProperty("得分")
    private Integer score;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("修改时间")
    private LocalDateTime updateTime;
}
