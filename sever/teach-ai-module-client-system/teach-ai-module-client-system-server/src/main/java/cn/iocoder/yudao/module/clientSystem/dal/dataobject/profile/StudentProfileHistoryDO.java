package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学生画像历史快照 DO
 *
 * 每次画像更新时保存一份快照，用于展示画像的历史演变趋势。
 */
@TableName("client_student_profile_history")
@KeySequence("client_student_profile_history_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileHistoryDO extends BaseDO {

    @TableId
    private Long id;

    /** 学生用户ID */
    private Long userId;

    /** 快照时的画像版本号 */
    private Integer profileVersion;

    /** 本次更新的对话ID（memoryId） */
    private String memoryId;

    /** 画像快照的JSON序列化，保存完整的StudentProfileDO字段 */
    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.LONGVARCHAR)
    private String snapshotJson;

    /** 本次更新变化摘要（由LLM生成） */
    private String changeSummary;
}
