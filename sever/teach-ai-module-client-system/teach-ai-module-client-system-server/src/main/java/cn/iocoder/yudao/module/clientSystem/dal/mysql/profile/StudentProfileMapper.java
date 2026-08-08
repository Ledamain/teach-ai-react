package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.profile;

import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生画像 Mapper
 */
@Mapper
public interface StudentProfileMapper extends BaseMapperX<StudentProfileDO> {

    default StudentProfileDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<StudentProfileDO>()
                .eq(StudentProfileDO::getUserId, userId));
    }
}
