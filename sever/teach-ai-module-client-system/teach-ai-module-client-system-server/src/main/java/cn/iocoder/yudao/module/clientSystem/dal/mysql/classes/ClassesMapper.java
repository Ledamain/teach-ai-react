package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.classes;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.*;

/**
 * 班级 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface ClassesMapper extends BaseMapperX<ClassesDO> {

    default PageResult<ClassesDTO> selectPage(ClassesPageReqVO reqVO) {
        Page<ClassesDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<ClassesDO> wrapper = new MPJLambdaWrapper<ClassesDO>()
                .selectAll(ClassesDO.class)
                .select(UserDO::getNickname)
                .select(UserDO::getClientUsername)
                .leftJoin(UserDO.class, UserDO::getId, ClassesDO::getTeacherUserId)
                .likeIfExists(ClassesDO::getClassesName, reqVO.getClassesName())
                .eqIfExists(ClassesDO::getTeacherUserId, reqVO.getTeacherUserId())
                .likeIfExists(UserDO::getNickname, reqVO.getNickname())
                .orderByDesc(ClassesDO::getId);
        IPage<ClassesDTO> pageResult = selectJoinPage(page, ClassesDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    default List<ClassesDTO> selectList(ClassesPageReqVO reqVO) {
        MPJLambdaWrapper<ClassesDO> wrapper = new MPJLambdaWrapper<ClassesDO>()
                .selectAll(ClassesDO.class)
                .select(UserDO::getNickname)
                .select(UserDO::getClientUsername)
                .leftJoin(UserDO.class, UserDO::getId, ClassesDO::getTeacherUserId)
                .likeIfExists(ClassesDO::getClassesName, reqVO.getClassesName())
                .eqIfExists(ClassesDO::getTeacherUserId, reqVO.getTeacherUserId())
                .likeIfExists(UserDO::getNickname, reqVO.getNickname())
                .orderByDesc(ClassesDO::getId);
        return selectJoinList(ClassesDTO.class,wrapper);
    }

//    default PageResult<ClassesDO> selectPage(ClassesPageReqVO reqVO) {
//        return selectPage(reqVO, new LambdaQueryWrapperX<ClassesDO>()
//                .likeIfPresent(ClassesDO::getClassesName, reqVO.getClassesName())
//                .eqIfPresent(ClassesDO::getTeacherUserId, reqVO.getTeacherUserId())
//                .betweenIfPresent(ClassesDO::getCreateTime, reqVO.getCreateTime())
//                .orderByDesc(ClassesDO::getId));
//    }

}
