package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 知识库类别 DO
 *
 * @author waynelam
 */
@TableName("client_repo_category")
@KeySequence("client_repo_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoCategoryDO extends BaseDO {

    /**
     * 知识库类别id
     */
    @TableId
    private Long id;

    /**
     * 知识库类别名称
     */
    private String repoCategoryName;

    /**
     * 课程组id
     */
    private Long courseGroupId;

    /**
     * 教师id
     */
    private Long teacherUserId;


}
