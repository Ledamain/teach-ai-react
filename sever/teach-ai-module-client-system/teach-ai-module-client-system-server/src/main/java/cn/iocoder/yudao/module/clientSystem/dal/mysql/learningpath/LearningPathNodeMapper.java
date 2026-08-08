package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningpath;

import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathNodeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningPathNodeMapper extends BaseMapperX<LearningPathNodeDO> {

    default List<LearningPathNodeDO> selectByPathId(Long pathId) {
        return selectList(new LambdaQueryWrapperX<LearningPathNodeDO>()
                .eq(LearningPathNodeDO::getPathId, pathId)
                .orderByAsc(LearningPathNodeDO::getOrderIndex));
    }
}
