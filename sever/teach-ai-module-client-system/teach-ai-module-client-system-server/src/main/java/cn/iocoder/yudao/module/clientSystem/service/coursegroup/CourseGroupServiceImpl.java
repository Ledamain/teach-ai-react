package cn.iocoder.teach-ai.module.clientSystem.service.coursegroup;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.coursegroup.CourseGroupDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.coursegroup.CourseGroupMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * 课程组 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class CourseGroupServiceImpl implements CourseGroupService {

    @Resource
    private CourseGroupMapper courseGroupMapper;

    @Override
    public Long createCourseGroup(CourseGroupSaveReqVO createReqVO) {
        // 插入
        CourseGroupDO courseGroup = BeanUtils.toBean(createReqVO, CourseGroupDO.class);
        courseGroupMapper.insert(courseGroup);

        // 返回
        return courseGroup.getId();
    }

    @Override
    public void updateCourseGroup(CourseGroupSaveReqVO updateReqVO) {
        // 校验存在
        validateCourseGroupExists(updateReqVO.getId());
        // 更新
        CourseGroupDO updateObj = BeanUtils.toBean(updateReqVO, CourseGroupDO.class);
        courseGroupMapper.updateById(updateObj);
    }

    @Override
    public void deleteCourseGroup(Long id) {
        // 校验存在
        validateCourseGroupExists(id);
        // 删除
        courseGroupMapper.deleteById(id);
    }

    @Override
        public void deleteCourseGroupListByIds(List<Long> ids) {
        // 删除
        courseGroupMapper.deleteByIds(ids);
        }


    private void validateCourseGroupExists(Long id) {
        if (courseGroupMapper.selectById(id) == null) {
            throw exception(COURSE_GROUP_NOT_EXISTS);
        }
    }

    @Override
    public CourseGroupDO getCourseGroup(Long id) {
        return courseGroupMapper.selectById(id);
    }

    @Override
    public PageResult<CourseGroupDO> getCourseGroupPage(CourseGroupPageReqVO pageReqVO) {
        return courseGroupMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CourseGroupDO> getCourseGroupList() {
        return courseGroupMapper.selectList(null);
    }

}
