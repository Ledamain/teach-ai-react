package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 练习题 DO
 *
 * @author waynelam
 */
@TableName("client_exercise_info")
@KeySequence("client_exercise_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseInfoDO extends BaseDO {

    /**
     * 习题id
     */
    @TableId
    private Long id;
    /**
     * 班级id
     */
    private String classesId;
    /**
     * 课程id
     */
    private Long repoCategoryId;
    /**
     * 出题人id
     */
    private Long teacherUserId;
    /**
     * 作业名称
     */
    private String exerciseName;
    /**
     * 题目内容
     */
    private String content;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 题目状态
     */
    private Long status;


}
