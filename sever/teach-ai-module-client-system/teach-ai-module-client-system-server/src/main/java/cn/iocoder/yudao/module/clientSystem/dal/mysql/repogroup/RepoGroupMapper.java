package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repogroup;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo.*;

/**
 * 课程文件夹 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface RepoGroupMapper extends BaseMapperX<RepoGroupDO> {

//    default PageResult<RepoGroupDO> selectPage(RepoGroupPageReqVO reqVO) {
//        return selectPage(reqVO, new LambdaQueryWrapperX<RepoGroupDO>()
//                .likeIfPresent(RepoGroupDO::getRepoGroupName, reqVO.getRepoGroupName())
//                .eqIfPresent(RepoGroupDO::getRepoCategoryId, reqVO.getRepoCategoryId())
//                .betweenIfPresent(RepoGroupDO::getCreateTime, reqVO.getCreateTime())
//                .orderByDesc(RepoGroupDO::getId));
//    }

    default PageResult<RepoGroupDTO> selectPage(RepoGroupPageReqVO reqVO) {
        Page<RepoGroupDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<RepoGroupDO> wrapper = new MPJLambdaWrapper<RepoGroupDO>()
                .selectAll(RepoGroupDO.class)
                .select(RepoCategoryDO::getRepoCategoryName)
                .leftJoin(RepoCategoryDO.class, RepoCategoryDO::getId, RepoGroupDO::getRepoCategoryId)
                .eqIfExists(RepoCategoryDO::getId, reqVO.getRepoCategoryId())
                .likeIfExists(RepoGroupDO::getRepoGroupDescription,reqVO.getRepoGroupDescription())
                .likeIfExists(RepoGroupDO::getRepoGroupName, reqVO.getRepoGroupName())
                .likeIfExists(RepoCategoryDO::getRepoCategoryName, reqVO.getRepoCategoryName())
                .orderByDesc(RepoGroupDO::getId);
        IPage<RepoGroupDTO> pageResult = selectJoinPage(page, RepoGroupDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    default List<RepoGroupDO> selectListByRepoCategoryId(Long repoCategoryId) {
        return selectList(new LambdaQueryWrapperX<RepoGroupDO>().eq(RepoGroupDO::getRepoCategoryId, repoCategoryId));
    }

}
