package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 课程数据分析响应 VO
 *
 * @author waynelam
 */
@Schema(description = "课程数据分析")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAnalyticsRespVO {

    @Schema(description = "总提问数", example = "1280")
    private Long totalQuestions;

    @Schema(description = "参与学生数", example = "45")
    private Long participantStudents;

    @Schema(description = "学科分类数", example = "8")
    private Long subjectCategories;

    @Schema(description = "今日提问数", example = "23")
    private Long todayQuestions;

    @Schema(description = "问题类型分布")
    private List<TypeDistItem> questionTypeDistribution;

    @Schema(description = "提问趋势")
    private List<DateCountItem> questionTrend;

    @Schema(description = "活跃学生趋势")
    private List<DateCountItem> activeStudents;

    @Schema(description = "复杂度分布")
    private List<ComplexityItem> complexityDistribution;

    @Schema(description = "学科分布")
    private List<SubjectItem> subjectDistribution;

    @Schema(description = "热门关键词")
    private List<KeywordItem> hotKeywords;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeDistItem {
        @Schema(description = "类型名称")
        private String type;
        @Schema(description = "数量")
        private Long count;
        @JsonProperty("percentage")
        @Schema(description = "占比")
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateCountItem {
        @Schema(description = "日期")
        private String date;
        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplexityItem {
        @Schema(description = "复杂度等级")
        private String level;
        @Schema(description = "数量")
        private Long count;
        @JsonProperty("percentage")
        @Schema(description = "占比")
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectItem {
        @Schema(description = "学科名称")
        private String subject;
        @Schema(description = "数量")
        private Long count;
        @JsonProperty("percentage")
        @Schema(description = "占比")
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordItem {
        @Schema(description = "关键词")
        private String keyword;
        @Schema(description = "出现次数")
        private Long count;
        @Schema(description = "趋势 up/down/stable")
        private String trend;
    }
}
