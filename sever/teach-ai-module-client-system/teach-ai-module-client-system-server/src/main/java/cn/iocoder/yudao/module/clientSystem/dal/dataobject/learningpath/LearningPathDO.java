package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学习路径主表 DO
 */
@TableName("client_learning_path")
@KeySequence("client_learning_path_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathDO extends BaseDO {

    @TableId
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 学科ID (repoCategoryId) */
    private Long repoCategoryId;

    /** 学科名称（冗余，方便展示） */
    private String repoCategoryName;

    /** 路径标题 */
    private String title;

    /** 路径描述 */
    private String description;

    /** 状态: active / completed / archived */
    private String status;

    /** 总节点数 */
    private Integer totalNodes;

    /** 已完成节点数 */
    private Integer completedNodes;

    /** 生成时间 */
    private java.time.LocalDateTime generatedAt;
}
