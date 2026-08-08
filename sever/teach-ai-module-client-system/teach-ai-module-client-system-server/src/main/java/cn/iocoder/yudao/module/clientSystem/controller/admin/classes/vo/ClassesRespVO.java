package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 班级 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClassesRespVO {

    @Schema(description = "班级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5869")
    @ExcelProperty("班级id")
    private Long id;

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("班级名称")
    private String classesName;

    @Schema(description = "教师用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "14519")
    @ExcelProperty("教师用户id")
    private Long teacherUserId;

    @Schema(description = "知识库类别ids", requiredMode = Schema.RequiredMode.REQUIRED, example = "14519")
    @ExcelProperty("知识库类别ids")
    private String repoCategoryIds;

    @Schema(description = "知识库类别列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "14519")
    private List<String> repoCategoryIdsList;

    @Schema(description = "教师", example = "王五")
    @ExcelProperty("教师")
    private String nickname;

    @Schema(description = "教师客户端账号", example = "王五")
    @ExcelProperty("教师客户端账号")
    private String clientUsername;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
