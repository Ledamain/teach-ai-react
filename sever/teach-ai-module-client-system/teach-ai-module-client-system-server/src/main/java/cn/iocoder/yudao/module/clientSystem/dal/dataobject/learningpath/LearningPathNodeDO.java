package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学习路径节点 DO
 */
@TableName("client_learning_path_node")
@KeySequence("client_learning_path_node_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathNodeDO extends BaseDO {

    @TableId
    private Long id;

    /** 所属路径ID */
    private Long pathId;

    /** 节点序号 (从1开始) */
    private Integer orderIndex;

    /** 节点标题 */
    private String title;

    /** 节点描述 */
    private String description;

    /** 资源类型: doc/video/exercise/ppt/reading */
    private String resourceType;

    /** 关联资源ID (知识库文件ID、习题ID等) */
    private Long resourceId;

    /** 资源名称 */
    private String resourceName;

    /** 前置节点ID (依赖，null表示无前置) */
    private Long dependsOn;

    /** 状态: pending / in_progress / completed */
    private String status;

    /** 预计学习时长(分钟) */
    private Integer estimatedMinutes;
}
