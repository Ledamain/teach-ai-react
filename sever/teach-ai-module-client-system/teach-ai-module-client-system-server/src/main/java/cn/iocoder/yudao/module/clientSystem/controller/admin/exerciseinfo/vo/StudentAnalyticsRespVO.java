package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学生个人数据分析响应 VO
 */
@Schema(description = "学生个人数据分析")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnalyticsRespVO {

    @Schema(description = "学生ID")
    private String studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "总做题数")
    private Integer totalQuestions;

    @Schema(description = "主修学科")
    private String mainSubject;

    @Schema(description = "最近活跃时间")
    private String lastActive;

    @Schema(description = "平均得分")
    private Integer averageScore;

    @Schema(description = "班级排名")
    private Integer rankInClass;

    @Schema(description = "班级总人数")
    private Integer totalStudents;

    @Schema(description = "复杂度分布")
    private List<ComplexityDistItem> complexityDistribution;

    @Schema(description = "学习趋势")
    private List<LearningTrendItem> learningTrend;

    @Schema(description = "学科分布")
    private List<SubjectDistItem> subjectDistribution;

    @Schema(description = "最近作业")
    private List<RecentAssignmentItem> recentAssignments;

    @Schema(description = "学习建议")
    private List<String> learningAdvice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplexityDistItem {
        private String level;
        private Integer count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningTrendItem {
        private String date;
        private Integer score;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectDistItem {
        private String subject;
        private Integer count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentAssignmentItem {
        private String id;
        private String title;
        private Integer score;
        private Integer totalScore;
        private String submitTime;
    }
}
