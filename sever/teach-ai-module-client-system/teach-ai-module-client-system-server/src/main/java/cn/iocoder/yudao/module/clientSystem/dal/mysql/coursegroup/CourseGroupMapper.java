package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.coursegroup;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.coursegroup.CourseGroupDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo.*;

/**
 * 课程组 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface CourseGroupMapper extends BaseMapperX<CourseGroupDO> {

    default PageResult<CourseGroupDO> selectPage(CourseGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CourseGroupDO>()
                .likeIfPresent(CourseGroupDO::getCourseGroupName, reqVO.getCourseGroupName())
                .betweenIfPresent(CourseGroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CourseGroupDO::getId));
    }

}
