package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学生画像 DO
 *
 * 支持 8 个维度的动态学生画像，随学随新。
 */
@TableName("client_student_profile")
@KeySequence("client_student_profile_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDO extends BaseDO {

    @TableId
    private Long id;

    /** 学生用户ID */
    private Long userId;

    // ==== 维度 1：知识基础 (knowledgeBase) ====
    /** 综合评级 (novice/intermediate/advanced/expert) */
    private String knowledgeLevel;
    /** 概述，如 "已掌握Python基础语法，对面向对象理解较浅" */
    private String knowledgeSummary;
    /** 已掌握知识点标签，JSON数组 */
    private String masteredTags;

    // ==== 维度 2：认知风格 (cognitiveStyle) ====
    /** 类型: field_dependent(场依存) / field_independent(场独立) / mixed */
    private String cognitiveStyle;
    /** 描述 */
    private String cognitiveStyleDesc;

    // ==== 维度 3：学习风格 (learningStyle) ====
    /** 类型: visual(视觉型) / auditory(听觉型) / kinesthetic(动手型) / reading_writing(读写型) / mixed */
    private String learningStyle;
    /** 描述 */
    private String learningStyleDesc;

    // ==== 维度 4：易错点偏好 (errorPreference) ====
    /** 概要 */
    private String errorPreferenceSummary;
    /** 易错知识点标签，JSON数组 */
    private String errorTags;

    // ==== 维度 5：注意力特征 (attentionCharacteristic) ====
    /** 集中度评级: high / medium / low */
    private String attentionLevel;
    /** 最佳学习时段，如 "上午9-11点" */
    private String bestStudyTime;
    /** 单次注意力持续时间(分钟) */
    private Integer attentionSpanMinutes;

    // ==== 维度 6：学习节奏 (learningPace) ====
    /** 节奏类型: fast/medium/slow */
    private String learningPace;
    /** 周均学习时长(分钟) */
    private Integer weeklyStudyMinutes;
    /** 偏好每次学习时长(分钟) */
    private Integer preferredSessionMinutes;

    // ==== 维度 7：兴趣方向 (interestDirection) ====
    /** 兴趣标签，JSON数组 */
    private String interestTags;
    /** 概要描述 */
    private String interestSummary;

    // ==== 维度 8：薄弱知识点标签 (weakPointTags) ====
    /** 薄弱点标签，JSON数组 */
    private String weakPointTags;
    /** 薄弱点详细描述 */
    private String weakPointDetail;

    // ==== 元信息 ====
    /** 画像版本号，每次更新自增 */
    private Integer profileVersion;
    /** 生成画像所依据的对话总数 */
    private Integer conversationCount;
    /** 最近一次从对话更新画像的时间 */
    private java.time.LocalDateTime lastExtractTime;
}
