package cn.iocoder.teach-ai.module.clientSystem.service.profile;

import cn.iocoder.teach-ai.framework.common.util.json.JsonUtils;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileHistoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.profile.StudentProfileHistoryMapper;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.profile.StudentProfileMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生画像 Service 实现
 */
@Slf4j
@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    @Resource
    private StudentProfileMapper profileMapper;

    @Resource
    private StudentProfileHistoryMapper historyMapper;

    @Override
    public StudentProfileDO getProfileByUserId(Long userId) {
        return profileMapper.selectByUserId(userId);
    }

    @Override
    public List<StudentProfileHistoryDO> getProfileHistory(Long userId, int limit) {
        return historyMapper.selectByUserIdLimit(userId, limit);
    }

    @Override
    @Transactional
    public void upsertProfile(Long userId, StudentProfileDO extracted, String memoryId, String changeSummary) {
        StudentProfileDO existing = profileMapper.selectByUserId(userId);

        if (existing == null) {
            // 首次创建画像
            extracted.setUserId(userId);
            extracted.setProfileVersion(1);
            extracted.setConversationCount(1);
            extracted.setLastExtractTime(LocalDateTime.now());
            profileMapper.insert(extracted);

            // 保存首个历史快照
            saveHistorySnapshot(userId, 1, memoryId, extracted, changeSummary);
            log.info("创建学生画像: userId={}", userId);
        } else {
            // 增量合并
            mergeProfile(existing, extracted);
            int newVersion = (existing.getProfileVersion() != null ? existing.getProfileVersion() : 0) + 1;
            existing.setProfileVersion(newVersion);
            existing.setConversationCount(
                    (existing.getConversationCount() != null ? existing.getConversationCount() : 0) + 1);
            existing.setLastExtractTime(LocalDateTime.now());
            profileMapper.updateById(existing);

            // 保存历史快照
            saveHistorySnapshot(userId, newVersion, memoryId, existing, changeSummary);
            log.info("更新学生画像: userId={}, version={}", userId, newVersion);
        }
    }

    /**
     * 增量合并策略：
     * 1. 对非空字符串字段，如果现有值存在则不覆盖（保留历史积累）
     * 2. 标签类字段做智能合并（去重追加）
     */
    private void mergeProfile(StudentProfileDO existing, StudentProfileDO incoming) {
        if (existing == null || incoming == null) return;

        // 知识基础
        if (isEmpty(existing.getKnowledgeLevel()) && !isEmpty(incoming.getKnowledgeLevel()))
            existing.setKnowledgeLevel(incoming.getKnowledgeLevel());
        if (isEmpty(existing.getKnowledgeSummary()) && !isEmpty(incoming.getKnowledgeSummary()))
            existing.setKnowledgeSummary(incoming.getKnowledgeSummary());
        existing.setMasteredTags(mergeJsonArray(existing.getMasteredTags(), incoming.getMasteredTags()));

        // 认知风格
        if (isEmpty(existing.getCognitiveStyle()) && !isEmpty(incoming.getCognitiveStyle()))
            existing.setCognitiveStyle(incoming.getCognitiveStyle());
        if (isEmpty(existing.getCognitiveStyleDesc()) && !isEmpty(incoming.getCognitiveStyleDesc()))
            existing.setCognitiveStyleDesc(incoming.getCognitiveStyleDesc());

        // 学习风格
        if (isEmpty(existing.getLearningStyle()) && !isEmpty(incoming.getLearningStyle()))
            existing.setLearningStyle(incoming.getLearningStyle());
        if (isEmpty(existing.getLearningStyleDesc()) && !isEmpty(incoming.getLearningStyleDesc()))
            existing.setLearningStyleDesc(incoming.getLearningStyleDesc());

        // 易错点偏好
        if (isEmpty(existing.getErrorPreferenceSummary()) && !isEmpty(incoming.getErrorPreferenceSummary()))
            existing.setErrorPreferenceSummary(incoming.getErrorPreferenceSummary());
        existing.setErrorTags(mergeJsonArray(existing.getErrorTags(), incoming.getErrorTags()));

        // 注意力特征
        if (isEmpty(existing.getAttentionLevel()) && !isEmpty(incoming.getAttentionLevel()))
            existing.setAttentionLevel(incoming.getAttentionLevel());
        if (isEmpty(existing.getBestStudyTime()) && !isEmpty(incoming.getBestStudyTime()))
            existing.setBestStudyTime(incoming.getBestStudyTime());
        if (existing.getAttentionSpanMinutes() == null && incoming.getAttentionSpanMinutes() != null)
            existing.setAttentionSpanMinutes(incoming.getAttentionSpanMinutes());

        // 学习节奏
        if (isEmpty(existing.getLearningPace()) && !isEmpty(incoming.getLearningPace()))
            existing.setLearningPace(incoming.getLearningPace());
        if (existing.getWeeklyStudyMinutes() == null && incoming.getWeeklyStudyMinutes() != null)
            existing.setWeeklyStudyMinutes(incoming.getWeeklyStudyMinutes());
        if (existing.getPreferredSessionMinutes() == null && incoming.getPreferredSessionMinutes() != null)
            existing.setPreferredSessionMinutes(incoming.getPreferredSessionMinutes());

        // 兴趣方向
        existing.setInterestTags(mergeJsonArray(existing.getInterestTags(), incoming.getInterestTags()));
        if (isEmpty(existing.getInterestSummary()) && !isEmpty(incoming.getInterestSummary()))
            existing.setInterestSummary(incoming.getInterestSummary());

        // 薄弱点
        existing.setWeakPointTags(mergeJsonArray(existing.getWeakPointTags(), incoming.getWeakPointTags()));
        if (isEmpty(existing.getWeakPointDetail()) && !isEmpty(incoming.getWeakPointDetail()))
            existing.setWeakPointDetail(incoming.getWeakPointDetail());
    }

    /** 合并 JSON 数组字符串，去重 */
    private String mergeJsonArray(String existing, String incoming) {
        if (isEmpty(incoming)) return existing;
        if (isEmpty(existing)) return incoming;
        try {
            List<String> existList = JsonUtils.parseArray(existing, String.class);
            List<String> incomeList = JsonUtils.parseArray(incoming, String.class);
            if (existList == null) existList = java.util.Collections.emptyList();
            if (incomeList == null) incomeList = java.util.Collections.emptyList();
            java.util.Set<String> merged = new java.util.LinkedHashSet<>(existList);
            merged.addAll(incomeList);
            return JsonUtils.toJsonString(merged);
        } catch (Exception e) {
            log.warn("合并JSON数组失败: existing={}, incoming={}, error={}", existing, incoming, e.getMessage());
            return existing;
        }
    }

    private void saveHistorySnapshot(Long userId, int version, String memoryId,
                                      StudentProfileDO profile, String changeSummary) {
        StudentProfileHistoryDO history = StudentProfileHistoryDO.builder()
                .userId(userId)
                .profileVersion(version)
                .memoryId(memoryId)
                .snapshotJson(JsonUtils.toJsonString(profile))
                .changeSummary(changeSummary)
                .build();
        historyMapper.insert(history);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
