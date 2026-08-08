package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repocategory;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.coursegroup.CourseGroupDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.*;

/**
 * 知识库类别 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface RepoCategoryMapper extends BaseMapperX<RepoCategoryDO> {

//    default PageResult<RepoCategoryDO> selectPage(RepoCategoryPageReqVO reqVO) {
//        return selectPage(reqVO, new LambdaQueryWrapperX<RepoCategoryDO>()
//                .likeIfPresent(RepoCategoryDO::getRepoCategoryName, reqVO.getRepoCategoryName())
//                .betweenIfPresent(RepoCategoryDO::getCreateTime, reqVO.getCreateTime())
//                .orderByDesc(RepoCategoryDO::getId));
//    }

    default PageResult<RepoCategoryDTO> selectPage(RepoCategoryPageReqVO reqVO) {
        Page<RepoCategoryDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<RepoCategoryDO> wrapper = new MPJLambdaWrapper<RepoCategoryDO>()
                .selectAll(RepoCategoryDO.class)
                .select(UserDO::getNickname)
                .select(CourseGroupDO::getCourseGroupName)
                .leftJoin(UserDO.class, UserDO::getId, RepoCategoryDO::getTeacherUserId)
                .leftJoin(CourseGroupDO.class, CourseGroupDO::getId, RepoCategoryDO::getCourseGroupId)
                .likeIfExists(RepoCategoryDO::getRepoCategoryName, reqVO.getRepoCategoryName())
                .orderByDesc(RepoCategoryDO::getId);
        IPage<RepoCategoryDTO> pageResult = selectJoinPage(page, RepoCategoryDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    default List<RepoCategoryDO> selectList(RepoCategoryPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<RepoCategoryDO>()
                .likeIfPresent(RepoCategoryDO::getRepoCategoryName, reqVO.getRepoCategoryName())
                .betweenIfPresent(RepoCategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RepoCategoryDO::getId));
    }

    default List<RepoCategoryDTO> selectListForClient(RepoCategoryPageReqVO reqVO) {
        MPJLambdaWrapper<RepoCategoryDO> wrapper = new MPJLambdaWrapper<RepoCategoryDO>()
                .selectAll(RepoCategoryDO.class)
                .select(UserDO::getNickname)
                .select(CourseGroupDO::getCourseGroupName)
                .leftJoin(UserDO.class, UserDO::getId, RepoCategoryDO::getTeacherUserId)
                .leftJoin(CourseGroupDO.class, CourseGroupDO::getId, RepoCategoryDO::getCourseGroupId)
                .likeIfExists(RepoCategoryDO::getRepoCategoryName, reqVO.getRepoCategoryName())
                .eqIfExists(RepoCategoryDO::getTeacherUserId,reqVO.getTeacherUserId())
                .orderByDesc(RepoCategoryDO::getId);
        return selectJoinList(RepoCategoryDTO.class,wrapper);
    }

    default RepoCategoryDTO getRepoCategoryForClient(Long id){
        if (id == null){
            return null;
        }
        MPJLambdaWrapper<RepoCategoryDO> wrapper = new MPJLambdaWrapper<RepoCategoryDO>()
                .selectAll(RepoCategoryDO.class)
                .select(UserDO::getNickname)
                .select(CourseGroupDO::getCourseGroupName)
                .leftJoin(UserDO.class, UserDO::getId, RepoCategoryDO::getTeacherUserId)
                .leftJoin(CourseGroupDO.class, CourseGroupDO::getId, RepoCategoryDO::getCourseGroupId)
                .eqIfExists(RepoCategoryDO::getId, id);
        return selectJoinOne(RepoCategoryDTO.class,wrapper);
    }

}
