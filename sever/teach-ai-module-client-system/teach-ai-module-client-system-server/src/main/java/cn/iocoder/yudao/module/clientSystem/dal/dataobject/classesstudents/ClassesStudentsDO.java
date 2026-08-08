package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 班级学生 DO
 *
 * @author waynelam
 */
@TableName("client_classes_students")
@KeySequence("client_classes_students_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassesStudentsDO extends BaseDO {

    /**
     * 班级学生id
     */
    @TableId
    private Long id;
    /**
     * 班级id
     */
    private Long classesId;
    /**
     * 学生用户id
     */
    private Long studentUserId;


}
