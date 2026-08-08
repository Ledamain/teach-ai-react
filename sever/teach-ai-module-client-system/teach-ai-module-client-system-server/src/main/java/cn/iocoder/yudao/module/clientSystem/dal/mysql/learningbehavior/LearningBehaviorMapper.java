package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningbehavior;

import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningbehavior.LearningBehaviorDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LearningBehaviorMapper extends BaseMapperX<LearningBehaviorDO> {

    /** 查询用户某段时间内的行为 */
    default List<LearningBehaviorDO> selectByUserIdAndPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        return selectList(new LambdaQueryWrapperX<LearningBehaviorDO>()
                .eq(LearningBehaviorDO::getUserId, userId)
                .ge(LearningBehaviorDO::getCreateTime, start)
                .le(LearningBehaviorDO::getCreateTime, end)
                .orderByAsc(LearningBehaviorDO::getCreateTime));
    }

    /** 统计某类事件次数 */
    default long countByEventType(Long userId, String eventType, LocalDateTime start, LocalDateTime end) {
        return selectCount(new LambdaQueryWrapperX<LearningBehaviorDO>()
                .eq(LearningBehaviorDO::getUserId, userId)
                .eq(LearningBehaviorDO::getEventType, eventType)
                .ge(LearningBehaviorDO::getCreateTime, start)
                .le(LearningBehaviorDO::getCreateTime, end));
    }

    /** 批量插入 */
    default void batchInsert(List<LearningBehaviorDO> list) {
        for (LearningBehaviorDO item : list) {
            insert(item);
        }
    }
}
