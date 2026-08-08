package cn.iocoder.teach-ai.module.clientSystem.service.exerciseresult;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 评判结果 Service 接口
 *
 * @author waynelam
 */
public interface ExerciseResultService {

    /**
     * 创建评判结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExerciseResult(@Valid ExerciseResultSaveReqVO createReqVO);

    /**
     * 更新评判结果
     *
     * @param updateReqVO 更新信息
     */
    void updateExerciseResult(@Valid ExerciseResultSaveReqVO updateReqVO);

    /**
     * 删除评判结果
     *
     * @param id 编号
     */
    void deleteExerciseResult(Long id);

    /**
    * 批量删除评判结果
    *
    * @param ids 编号
    */
    void deleteExerciseResultListByIds(List<Long> ids);

    /**
     * 获得评判结果
     *
     * @param id 编号
     * @return 评判结果
     */
    ExerciseResultDO getExerciseResult(Long id);

    /**
     * 获得评判结果分页
     *
     * @param pageReqVO 分页查询
     * @return 评判结果分页
     */
    PageResult<ExerciseResultDTO> getExerciseResultPage(ExerciseResultPageReqVO pageReqVO);

    /**
     * 获得已交作业人数
     *
     * @param exerciseId 作业id
     * @return 评判结果分页
     */
    Long getSubmissionCount(Long exerciseId);

    /**
     * 获得需交作业人数
     *
     * @param exerciseId 作业id
     * @return 评判结果分页
     */
    Long getStudentCount(Long exerciseId);

    /**
     * 获得评判结果列表
     *
     * @param pageReqVO 分页查询
     * @return 评判结果分页
     */
    PageResult<ExerciseResultDTO> getExerciseResultInfo(ExerciseResultPageReqVO pageReqVO);
}
