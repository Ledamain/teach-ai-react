package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.assessment;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

import java.time.LocalDateTime;

/**
 * AI 学习效果评估报告 DO
 */
@TableName("client_assessment_report")
@KeySequence("client_assessment_report_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentReportDO extends BaseDO {

    @TableId
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 评估周期开始时间 */
    private LocalDateTime periodStart;

    /** 评估周期结束时间 */
    private LocalDateTime periodEnd;

    /** 综合评分 (0-100) */
    private Integer overallScore;

    /** 维度评分 JSON: {"knowledge":85,"engagement":70,"weaknessProgress":60,...} */
    private String dimensions;

    /** 优势列表 JSON: ["已掌握Python基础","练习正确率高"] */
    private String strengths;

    /** 薄弱列表 JSON: ["递归理解","动态规划"] */
    private String weaknesses;

    /** AI 调整建议 JSON: ["建议1","建议2","建议3"] */
    private String suggestions;

    /** 评估文本摘要 */
    private String summary;
}
