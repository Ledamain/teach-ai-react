package cn.iocoder.teach-ai.module.clientSystem.service.learningpath;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathNodeDO;

import java.util.List;

public interface LearningPathService {

    LearningPathDO getById(Long id);

    LearningPathDO getActiveByUserAndCategory(Long userId, Long repoCategoryId);

    List<LearningPathDO> listByUserId(Long userId);

    List<LearningPathDO> listActiveByUserId(Long userId);

    List<LearningPathNodeDO> getNodesByPathId(Long pathId);

    /** 创建或替换路径（删除旧active路径，创建新的） */
    LearningPathDO createPath(Long userId, Long repoCategoryId, String repoCategoryName,
                               String title, String description, List<LearningPathNodeDO> nodes);

    /** 更新节点状态 */
    void updateNodeStatus(Long nodeId, String status);

    /** 设置节点前置依赖 */
    void updateNodeDependsOn(Long nodeId, Long dependsOn);
}
