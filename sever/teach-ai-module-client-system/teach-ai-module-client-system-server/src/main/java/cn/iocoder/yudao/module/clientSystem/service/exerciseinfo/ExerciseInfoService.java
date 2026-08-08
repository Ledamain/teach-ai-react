package cn.iocoder.teach-ai.module.clientSystem.service.exerciseinfo;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 练习题 Service 接口
 *
 * @author waynelam
 */
public interface ExerciseInfoService {

    /**
     * 创建练习题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExerciseInfo(@Valid ExerciseInfoSaveReqVO createReqVO);

    /**
     * 更新练习题
     *
     * @param updateReqVO 更新信息
     */
    void updateExerciseInfo(@Valid ExerciseInfoSaveReqVO updateReqVO);

    /**
     * 删除练习题
     *
     * @param id 编号
     */
    void deleteExerciseInfo(Long id);

    /**
    * 批量删除练习题
    *
    * @param ids 编号
    */
    void deleteExerciseInfoListByIds(List<Long> ids);

    /**
     * 获得练习题
     *
     * @param id 编号
     * @return 练习题
     */
    ExerciseInfoDO getExerciseInfo(Long id);

    /**
     * 获得练习题分页
     *
     * @param pageReqVO 分页查询
     * @return 练习题分页
     */
    PageResult<ExerciseInfoDO> getExerciseInfoPage(ExerciseInfoPageReqVO pageReqVO);

    /**
     * 获得练习题连表分页
     *
     * @param pageReqVO 分页查询
     * @return 练习题分页
     */
    PageResult<ExerciseInfoDTO> getExerciseInfoJoinPage(ExerciseInfoPageReqVO pageReqVO);


    /**
     * 获取课程数据分析
     *
     * @param repoCategoryId 课程分类ID
     * @return 分析数据
     */
    CourseAnalyticsRespVO getCourseAnalytics(Long repoCategoryId);

}
