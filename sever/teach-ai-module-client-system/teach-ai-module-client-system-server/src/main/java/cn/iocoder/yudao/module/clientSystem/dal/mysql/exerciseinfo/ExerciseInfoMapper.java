package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.exerciseinfo;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo.*;

/**
 * 练习题 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface ExerciseInfoMapper extends BaseMapperX<ExerciseInfoDO> {

    default PageResult<ExerciseInfoDO> selectPage(ExerciseInfoPageReqVO reqVO) {
        String classesId = reqVO.getClassesId();
        if (StrUtil.isNotEmpty(classesId)) {
            reqVO.setClassesId("_" + reqVO.getClassesId() + "C");
        }
        return selectPage(reqVO, new LambdaQueryWrapperX<ExerciseInfoDO>()
                .likeIfPresent(ExerciseInfoDO::getClassesId, reqVO.getClassesId())
                .eqIfPresent(ExerciseInfoDO::getRepoCategoryId, reqVO.getRepoCategoryId())
                .eqIfPresent(ExerciseInfoDO::getTeacherUserId, reqVO.getTeacherUserId())
                .likeIfPresent(ExerciseInfoDO::getExerciseName, reqVO.getExerciseName())
                .eqIfPresent(ExerciseInfoDO::getContent, reqVO.getContent())
                .betweenIfPresent(ExerciseInfoDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(ExerciseInfoDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(ExerciseInfoDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ExerciseInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExerciseInfoDO::getId));
    }

    default PageResult<ExerciseInfoDTO> selectJoinPage(ExerciseInfoPageReqVO reqVO) {
        String classesId = reqVO.getClassesId();
        if (StrUtil.isNotEmpty(classesId)) {
            reqVO.setClassesId("_" + reqVO.getClassesId() + "C");
        }
        Page<ExerciseInfoDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<ExerciseInfoDO> wrapper = new MPJLambdaWrapperX<ExerciseInfoDO>()
                .selectAll(ExerciseInfoDO.class)
                .select(UserDO::getNickname)
                .select(RepoCategoryDO::getRepoCategoryName)
                .leftJoin(UserDO.class, UserDO::getId, ExerciseInfoDO::getTeacherUserId)
                .leftJoin(RepoCategoryDO.class, RepoCategoryDO::getId, ExerciseInfoDO::getRepoCategoryId)
                .likeIfExists(ExerciseInfoDO::getClassesId, reqVO.getClassesId())
                .eqIfExists(ExerciseInfoDO::getRepoCategoryId, reqVO.getRepoCategoryId())
                .eqIfExists(ExerciseInfoDO::getTeacherUserId, reqVO.getTeacherUserId())
                .likeIfExists(ExerciseInfoDO::getExerciseName, reqVO.getExerciseName())
                .eqIfExists(ExerciseInfoDO::getContent, reqVO.getContent())
                .eqIfExists(ExerciseInfoDO::getStatus, reqVO.getStatus())
                .orderByDesc(ExerciseInfoDO::getId);
        IPage<ExerciseInfoDTO> pageResult = selectJoinPage(page, ExerciseInfoDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    /** 根据学科分类查询练习题 */
    default List<ExerciseInfoDO> selectByCategoryId(Long repoCategoryId) {
        return selectList(new LambdaQueryWrapperX<ExerciseInfoDO>()
                .eq(ExerciseInfoDO::getRepoCategoryId, repoCategoryId));
    }
}
