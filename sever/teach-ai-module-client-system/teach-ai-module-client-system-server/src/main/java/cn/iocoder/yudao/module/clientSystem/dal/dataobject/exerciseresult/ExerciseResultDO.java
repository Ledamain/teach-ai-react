package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 评判结果 DO
 *
 * @author waynelam
 */
@TableName("client_exercise_result")
@KeySequence("client_exercise_result_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResultDO extends BaseDO {

    /**
     * 主键id
     */
    @TableId
    private Long id;
    /**
     * 关联题目
     */
    private Long exerciseId;
    /**
     * 用户ID
     */
    private Long studentUserId;
    /**
     * 成绩单
     */
    private String transcript;
    /**
     * 是否完成
     */
    private Long completed;


}
