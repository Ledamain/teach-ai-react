package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 班级 DO
 *
 * @author waynelam
 */
@TableName("client_classes")
@KeySequence("client_classes_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassesDO extends BaseDO {

    /**
     * 班级id
     */
    @TableId
    private Long id;
    /**
     * 班级名称
     */
    private String classesName;
    /**
     * 教师用户id
     */
    private Long teacherUserId;
    /**
     * 知识库类别ids
     */
    private String repoCategoryIds;


}
