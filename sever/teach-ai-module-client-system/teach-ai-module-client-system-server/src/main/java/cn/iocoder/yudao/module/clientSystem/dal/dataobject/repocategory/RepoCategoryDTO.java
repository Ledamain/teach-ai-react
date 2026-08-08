package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RepoCategoryDTO extends RepoCategoryDO {
    /**
     * 客户端用户名称
     */
    private String nickname;

    /**
     * 课程组名称
     */
    private String courseGroupName;

    /**
     * 学生总数
     */
    private Long studentCount;
}
