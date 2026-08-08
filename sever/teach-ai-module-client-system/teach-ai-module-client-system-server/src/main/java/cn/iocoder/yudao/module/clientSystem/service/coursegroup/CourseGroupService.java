package cn.iocoder.teach-ai.module.clientSystem.service.coursegroup;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.coursegroup.CourseGroupDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 课程组 Service 接口
 *
 * @author waynelam
 */
public interface CourseGroupService {

    /**
     * 创建课程组
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCourseGroup(@Valid CourseGroupSaveReqVO createReqVO);

    /**
     * 更新课程组
     *
     * @param updateReqVO 更新信息
     */
    void updateCourseGroup(@Valid CourseGroupSaveReqVO updateReqVO);

    /**
     * 删除课程组
     *
     * @param id 编号
     */
    void deleteCourseGroup(Long id);

    /**
    * 批量删除课程组
    *
    * @param ids 编号
    */
    void deleteCourseGroupListByIds(List<Long> ids);

    /**
     * 获得课程组
     *
     * @param id 编号
     * @return 课程组
     */
    CourseGroupDO getCourseGroup(Long id);

    /**
     * 获得课程组分页
     *
     * @param pageReqVO 分页查询
     * @return 课程组分页
     */
    PageResult<CourseGroupDO> getCourseGroupPage(CourseGroupPageReqVO pageReqVO);

    /**
     * 获得课程组列表
     *
     * @return 课程组列表
     */
    List<CourseGroupDO> getCourseGroupList();
}
