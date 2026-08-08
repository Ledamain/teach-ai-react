package cn.iocoder.teach-ai.module.clientSystem.api.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学生画像响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRespDTO {

    private Long id;
    private Long userId;

    private String knowledgeLevel;
    private String knowledgeSummary;
    private String masteredTags;

    private String cognitiveStyle;
    private String cognitiveStyleDesc;

    private String learningStyle;
    private String learningStyleDesc;

    private String errorPreferenceSummary;
    private String errorTags;

    private String attentionLevel;
    private String bestStudyTime;
    private Integer attentionSpanMinutes;

    private String learningPace;
    private Integer weeklyStudyMinutes;
    private Integer preferredSessionMinutes;

    private String interestTags;
    private String interestSummary;

    private String weakPointTags;
    private String weakPointDetail;

    private Integer profileVersion;
    private Integer conversationCount;
    private LocalDateTime lastExtractTime;
    private LocalDateTime createTime;
}
