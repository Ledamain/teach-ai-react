package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repo;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.*;

/**
 * 知识库 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface RepoMapper extends BaseMapperX<RepoDO> {

    default PageResult<RepoDTO> selectPage(RepoPageReqVO reqVO) {
        Page<RepoDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<RepoDO> wrapper = new MPJLambdaWrapper<RepoDO>()
                .selectAll(RepoDO.class)
                .select(RepoCategoryDO::getRepoCategoryName)
                .select(RepoGroupDO::getRepoGroupName)
                .leftJoin(RepoCategoryDO.class, RepoCategoryDO::getId, RepoDO::getRepoCategoryId)
                .leftJoin(RepoGroupDO.class, RepoGroupDO::getId, RepoDO::getRepoGroupId)
                .eqIfExists(RepoDO::getRepoTitle, reqVO.getRepoTitle())
                .eqIfExists(RepoDO::getRepoFile, reqVO.getRepoFile())
                .eqIfExists(RepoDO::getRepoDesp, reqVO.getRepoDesp())
                .eqIfExists(RepoDO::getRepoCategoryId, reqVO.getRepoCategoryId())
                .eqIfExists(RepoDO::getRepoStatus, reqVO.getRepoStatus())
                .orderByDesc(RepoDO::getId);
        IPage<RepoDTO> pageResult = selectJoinPage(page, RepoDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    default List<RepoDTO> selectList(RepoPageReqVO reqVO) {
        MPJLambdaWrapper<RepoDO> wrapper = new MPJLambdaWrapper<RepoDO>()
                .selectAll(RepoDO.class)
                .select(RepoCategoryDO::getRepoCategoryName)
                .leftJoin(RepoCategoryDO.class, RepoCategoryDO::getId, RepoDO::getRepoCategoryId)
                .eqIfExists(RepoDO::getRepoTitle, reqVO.getRepoTitle())
                .eqIfExists(RepoDO::getRepoFile, reqVO.getRepoFile())
                .eqIfExists(RepoDO::getRepoDesp, reqVO.getRepoDesp())
                .eqIfExists(RepoDO::getRepoCategoryId, reqVO.getRepoCategoryId())
                .eqIfExists(RepoDO::getRepoStatus, reqVO.getRepoStatus())
                .orderByDesc(RepoDO::getId);
        return selectJoinList(RepoDTO.class, wrapper);
    }

    /** 根据学科分类查询知识库文档（按启用状态过滤） */
    default List<RepoDO> selectByCategoryId(Long repoCategoryId) {
        return selectList(new LambdaQueryWrapperX<RepoDO>()
                .eq(RepoDO::getRepoCategoryId, repoCategoryId)
                .eq(RepoDO::getRepoStatus, "ENABLE"));
    }
}
