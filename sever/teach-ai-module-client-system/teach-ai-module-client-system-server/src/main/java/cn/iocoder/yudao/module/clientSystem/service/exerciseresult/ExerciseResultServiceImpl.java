package cn.iocoder.teach-ai.module.clientSystem.service.exerciseresult;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.exerciseresult.ExerciseResultMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * 评判结果 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class ExerciseResultServiceImpl implements ExerciseResultService {

    @Resource
    private ExerciseResultMapper exerciseResultMapper;

    @Override
    public Long createExerciseResult(ExerciseResultSaveReqVO createReqVO) {
        // 插入
        ExerciseResultDO exerciseResult = BeanUtils.toBean(createReqVO, ExerciseResultDO.class);
        exerciseResultMapper.insert(exerciseResult);

        // 返回
        return exerciseResult.getId();
    }

    @Override
    public void updateExerciseResult(ExerciseResultSaveReqVO updateReqVO) {
        // 校验存在
        validateExerciseResultExists(updateReqVO.getId());
        // 更新
        ExerciseResultDO updateObj = BeanUtils.toBean(updateReqVO, ExerciseResultDO.class);
        exerciseResultMapper.updateById(updateObj);
    }

    @Override
    public void deleteExerciseResult(Long id) {
        // 校验存在
        validateExerciseResultExists(id);
        // 删除
        exerciseResultMapper.deleteById(id);
    }

    @Override
        public void deleteExerciseResultListByIds(List<Long> ids) {
        // 删除
        exerciseResultMapper.deleteByIds(ids);
        }


    private void validateExerciseResultExists(Long id) {
        if (exerciseResultMapper.selectById(id) == null) {
            throw exception(EXERCISE_RESULT_NOT_EXISTS);
        }
    }

    @Override
    public ExerciseResultDO getExerciseResult(Long id) {
        return exerciseResultMapper.selectById(id);
    }

    @Override
    public PageResult<ExerciseResultDTO> getExerciseResultPage(ExerciseResultPageReqVO pageReqVO) {
        return exerciseResultMapper.selectPage(pageReqVO);
    }

    @Override
    public Long getSubmissionCount(Long exerciseId) {
        return exerciseResultMapper.selectCount(new LambdaQueryWrapper<ExerciseResultDO>().eq(ExerciseResultDO::getExerciseId, exerciseId).eq(ExerciseResultDO::getCompleted, 1L));
    }

    @Override
    public Long getStudentCount(Long exerciseId) {
        return exerciseResultMapper.selectCount(new LambdaQueryWrapper<ExerciseResultDO>().eq(ExerciseResultDO::getExerciseId, exerciseId));
    }

    @Override
    public PageResult<ExerciseResultDTO> getExerciseResultInfo(ExerciseResultPageReqVO pageReqVO) {
        return exerciseResultMapper.selectJoinPage(pageReqVO);
    }

}
