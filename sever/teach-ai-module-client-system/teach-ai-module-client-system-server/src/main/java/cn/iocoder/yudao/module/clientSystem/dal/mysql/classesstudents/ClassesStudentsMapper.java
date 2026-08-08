package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.classesstudents;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.StudentsDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.*;

/**
 * 班级学生 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface ClassesStudentsMapper extends BaseMapperX<ClassesStudentsDO> {

    default PageResult<ClassesStudentsDO> selectPage(ClassesStudentsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClassesStudentsDO>()
                .eqIfPresent(ClassesStudentsDO::getClassesId, reqVO.getClassesId())
                .eqIfPresent(ClassesStudentsDO::getStudentUserId, reqVO.getStudentUserId())
                .betweenIfPresent(ClassesStudentsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ClassesStudentsDO::getId));
    }

    default List<ClassesStudentsDO> selectListByClassesId(Long classesId) {
        return selectList(ClassesStudentsDO::getClassesId, classesId);
    }

    default List<ClassesStudentsDTO> selectListByClassesIdExt(Long classesId) {
        MPJLambdaWrapper<ClassesStudentsDO> wrapper = new MPJLambdaWrapper<ClassesStudentsDO>()
                .selectAll(ClassesStudentsDO.class)
                .select(UserDO::getNickname)
                .select(UserDO::getClientUsername)
                .leftJoin(UserDO.class, UserDO::getId, ClassesStudentsDO::getStudentUserId)
                .eq(ClassesStudentsDO::getClassesId, classesId)
                .orderByAsc(ClassesStudentsDO::getId);
        return selectJoinList(ClassesStudentsDTO.class, wrapper);
    }

    default List<StudentsDTO> selectListRecord(ClassesStudentsPageReqVO reqVO){
        MPJLambdaWrapper<ClassesStudentsDO> wrapper = new MPJLambdaWrapper<ClassesStudentsDO>()
                .selectAll(ClassesStudentsDO.class)
                .select(ClassesDO::getClassesName)
                .leftJoin(ClassesDO.class,ClassesDO::getId,ClassesStudentsDO::getClassesId)
                .select(UserDO::getNickname)
                .select(UserDO::getClientAvator)
                .select(UserDO::getClientNum)
                .select(UserDO::getClientTel)
                .leftJoin(UserDO.class,UserDO::getId,ClassesStudentsDO::getStudentUserId)
                .select(CountRecordDO::getRecordCount)
                .leftJoin(CountRecordDO.class,CountRecordDO::getUserId,ClassesStudentsDO::getStudentUserId)
                .eqIfExists(ClassesStudentsDO::getClassesId,reqVO.getClassesId())
                .eqIfExists(ClassesStudentsDO::getStudentUserId,reqVO.getStudentUserId())
                .orderByDesc(ClassesStudentsDO::getCreateTime);
        return selectJoinList(StudentsDTO.class, wrapper);
    }

    default List<ClassesStudentsDO> selectList(ClassesStudentsPageReqVO reqVO){
        LambdaQueryWrapperX<ClassesStudentsDO> wrapper = new LambdaQueryWrapperX<ClassesStudentsDO>()
                .eqIfPresent(ClassesStudentsDO::getClassesId,reqVO.getClassesId())
                .orderByDesc(ClassesStudentsDO::getCreateTime);
        return selectList(wrapper);
    }

    default int deleteByClassesId(Long classesId) {
        return delete(ClassesStudentsDO::getClassesId, classesId);
    }

    default int deleteByClassesIds(List<Long> classesIds) {
        return deleteBatch(ClassesStudentsDO::getClassesId, classesIds);
    }

}
