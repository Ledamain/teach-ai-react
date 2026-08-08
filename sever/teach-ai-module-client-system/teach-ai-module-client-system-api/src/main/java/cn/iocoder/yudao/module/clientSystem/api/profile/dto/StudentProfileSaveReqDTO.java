package cn.iocoder.teach-ai.module.clientSystem.api.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生画像请求 DTO（用于 RPC 调用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileSaveReqDTO {

    private Long userId;

    // 知识基础
    private String knowledgeLevel;
    private String knowledgeSummary;
    private String masteredTags;

    // 认知风格
    private String cognitiveStyle;
    private String cognitiveStyleDesc;

    // 学习风格
    private String learningStyle;
    private String learningStyleDesc;

    // 易错点偏好
    private String errorPreferenceSummary;
    private String errorTags;

    // 注意力特征
    private String attentionLevel;
    private String bestStudyTime;
    private Integer attentionSpanMinutes;

    // 学习节奏
    private String learningPace;
    private Integer weeklyStudyMinutes;
    private Integer preferredSessionMinutes;

    // 兴趣方向
    private String interestTags;
    private String interestSummary;

    // 薄弱点
    private String weakPointTags;
    private String weakPointDetail;

    // 元信息
    private String memoryId;
    private String changeSummary;
}
