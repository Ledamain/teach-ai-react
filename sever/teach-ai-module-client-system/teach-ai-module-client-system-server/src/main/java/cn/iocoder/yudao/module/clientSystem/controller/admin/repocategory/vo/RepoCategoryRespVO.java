package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 知识库类别 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RepoCategoryRespVO {

    @Schema(description = "知识库类别id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29118")
    @ExcelProperty("知识库类别id")
    private Long id;

    @Schema(description = "知识库类别名称", example = "李四")
    @ExcelProperty("知识库类别名称")
    private String repoCategoryName;

    @Schema(description = "课程组id", example = "123")
    @ExcelProperty("课程组id")
    private Long courseGroupId;

    @Schema(description = "课程组名称", example = "王五")
    @ExcelProperty("课程组名称")
    private String courseGroupName;

    @Schema(description = "教师id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29118")
    @ExcelProperty("教师id")
    private Long teacherUserId;

    @Schema(description = "客户端用户名称", example = "赵六")
    @ExcelProperty("客户端用户名称")
    private String nickname;

    @Schema(description = "学生总数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("学生总数")
    private Long studentCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
