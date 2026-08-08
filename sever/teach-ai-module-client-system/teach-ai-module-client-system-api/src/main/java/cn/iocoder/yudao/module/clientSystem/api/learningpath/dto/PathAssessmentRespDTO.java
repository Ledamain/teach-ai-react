package cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学习效果评估 + 路径调整响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PathAssessmentRespDTO {

    /** 综合评分 0-100 */
    private Integer overallScore;

    /** 各维度评分 JSON */
    private String dimensions;

    /** 优势列表 JSON */
    private String strengths;

    /** 薄弱点列表 JSON */
    private String weaknesses;

    /** 改进建议 JSON */
    private String suggestions;

    /** 评估总结 */
    private String summary;

    /** 匹配到的薄弱点数量 */
    private Integer matchCount;

    /** 建议增加的补救节点数 */
    private Integer remediationNodeCount;

    /** 补救节点详情 */
    private List<RemediationNodeDTO> remediationNodes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemediationNodeDTO {
        /** 目标薄弱节点ID */
        private Long targetNodeId;
        /** 插入位置 */
        private String position; // "before" | "after"
        /** 补救节点标题 */
        private String title;
        /** 补救节点描述 */
        private String description;
        /** 补救节点资源类型 */
        private String resourceType;
    }
}
