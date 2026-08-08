package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.exerciseresult;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.*;

/**
 * 评判结果 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface ExerciseResultMapper extends BaseMapperX<ExerciseResultDO> {

    default PageResult<ExerciseResultDTO> selectPage(ExerciseResultPageReqVO reqVO) {
        Page<ExerciseResultDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<ExerciseResultDO> wrapper = new MPJLambdaWrapper<ExerciseResultDO>()
                .selectAll(ExerciseResultDO.class)
                .leftJoin(UserDO.class,UserDO::getId,ExerciseResultDO::getStudentUserId)
                .selectAs(UserDO::getNickname,ExerciseResultDTO::getStudentUserName)
                .select(UserDO::getClientNum)
                .leftJoin(ClassesStudentsDO.class,ClassesStudentsDO::getStudentUserId,ExerciseResultDO::getStudentUserId)
                .leftJoin(ClassesDO.class,ClassesDO::getId,ClassesStudentsDO::getClassesId)
                .select(ClassesDO::getClassesName)
                .eqIfExists(ExerciseResultDO::getExerciseId, reqVO.getExerciseId())
                .eqIfExists(ExerciseResultDO::getStudentUserId, reqVO.getStudentUserId())
                .eqIfExists(ExerciseResultDO::getTranscript, reqVO.getTranscript())
                .eqIfExists(ExerciseResultDO::getCompleted, reqVO.getCompleted())
                .orderByDesc(ExerciseResultDO::getId);
        if (reqVO.getCreateTime() != null) {
            wrapper.between(ExerciseResultDO::getCreateTime, reqVO.getCreateTime()[0], reqVO.getCreateTime()[1]);
        }
        IPage<ExerciseResultDTO> pageResult = selectJoinPage(page, ExerciseResultDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());

    }

    default PageResult<ExerciseResultDTO> selectJoinPage(ExerciseResultPageReqVO reqVO) {
        Page<ExerciseResultDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<ExerciseResultDO> wrapper = new MPJLambdaWrapperX<ExerciseResultDO>()
                .selectAll(ExerciseResultDO.class)
                .select(ExerciseInfoDO::getRepoCategoryId)
                .leftJoin(ExerciseInfoDO.class, ExerciseInfoDO::getId, ExerciseResultDO::getExerciseId)
                .eqIfExists(ExerciseResultDO::getStudentUserId, reqVO.getStudentUserId())
                .eqIfExists(ExerciseResultDO::getCompleted, reqVO.getCompleted())
                .eqIfExists(ExerciseResultDO::getExerciseId, reqVO.getExerciseId())
                .eqIfExists(ExerciseInfoDO::getRepoCategoryId, reqVO.getRepoCategoryId())
                .orderByDesc(ExerciseResultDO::getId);
        IPage<ExerciseResultDTO> pageResult = selectJoinPage(page, ExerciseResultDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

}
