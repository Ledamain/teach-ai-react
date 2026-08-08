package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.profile;

import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileHistoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 学生画像历史 Mapper
 */
@Mapper
public interface StudentProfileHistoryMapper extends BaseMapperX<StudentProfileHistoryDO> {

    default List<StudentProfileHistoryDO> selectByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<StudentProfileHistoryDO>()
                .eq(StudentProfileHistoryDO::getUserId, userId)
                .orderByAsc(StudentProfileHistoryDO::getProfileVersion));
    }

    default List<StudentProfileHistoryDO> selectByUserIdLimit(Long userId, int limit) {
        return selectList(new LambdaQueryWrapperX<StudentProfileHistoryDO>()
                .eq(StudentProfileHistoryDO::getUserId, userId)
                .orderByDesc(StudentProfileHistoryDO::getProfileVersion)
                .last("LIMIT " + limit));
    }
}
