package cn.iocoder.teach-ai.module.clientSystem.service.profile;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileHistoryDO;

import java.util.List;

/**
 * 学生画像 Service 接口
 */
public interface StudentProfileService {

    /** 根据用户ID获取当前画像 */
    StudentProfileDO getProfileByUserId(Long userId);

    /** 获取画像历史快照列表 */
    List<StudentProfileHistoryDO> getProfileHistory(Long userId, int limit);

    /**
     * 更新或创建画像（增量合并）。
     * @param userId 用户ID
     * @param extractedProfile 从对话中提取的画像增量数据
     * @param memoryId 触发更新的对话ID
     * @param changeSummary LLM生成的更新摘要
     */
    void upsertProfile(Long userId, StudentProfileDO extractedProfile, String memoryId, String changeSummary);
}
