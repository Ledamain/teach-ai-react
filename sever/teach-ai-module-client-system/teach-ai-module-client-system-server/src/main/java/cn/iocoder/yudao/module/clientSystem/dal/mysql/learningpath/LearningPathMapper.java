package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningpath;

import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningPathMapper extends BaseMapperX<LearningPathDO> {

    default List<LearningPathDO> selectByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<LearningPathDO>()
                .eq(LearningPathDO::getUserId, userId)
                .orderByDesc(LearningPathDO::getGeneratedAt));
    }

    default LearningPathDO selectByUserIdAndCategory(Long userId, Long repoCategoryId) {
        return selectOne(new LambdaQueryWrapperX<LearningPathDO>()
                .eq(LearningPathDO::getUserId, userId)
                .eq(LearningPathDO::getRepoCategoryId, repoCategoryId)
                .eq(LearningPathDO::getStatus, "active")
                .orderByDesc(LearningPathDO::getGeneratedAt)
                .last("LIMIT 1"));
    }

    default List<LearningPathDO> selectActiveByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<LearningPathDO>()
                .eq(LearningPathDO::getUserId, userId)
                .eq(LearningPathDO::getStatus, "active")
                .orderByDesc(LearningPathDO::getGeneratedAt));
    }
}
