package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningbehavior;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学习行为事件 DO — 前端埋点上报的学生行为数据
 */
@TableName("client_learning_behavior")
@KeySequence("client_learning_behavior_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningBehaviorDO extends BaseDO {

    @TableId
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 事件类型: page_view / node_complete / exercise_submit / resource_click / chat_message */
    private String eventType;

    /** 关联学科ID (可选) */
    private Long repoCategoryId;

    /** 关联资源ID (可选, 如节点ID、练习ID) */
    private Long resourceId;

    /** 事件持续时长(秒), page_view类事件记录停留时长 */
    private Integer durationSeconds;

    /** 扩展元数据 JSON: { score, questionCount, nodeStatus, resourceType, ... } */
    private String metadata;
}
