package cn.iocoder.teach-ai.module.clientSystem.controller.client.user.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentsRespVO extends UserRespVO {

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("班级名称")
    private String classesName;

    @Schema(description = "记录使用数量", example = "3894")
    private Long recordCount;

}
