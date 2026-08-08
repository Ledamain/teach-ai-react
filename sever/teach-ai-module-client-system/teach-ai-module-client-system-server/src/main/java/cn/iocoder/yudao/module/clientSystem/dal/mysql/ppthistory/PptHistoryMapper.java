package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.ppthistory;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.method.SelectJoinList;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.*;

/**
 * PPT历史记录 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface PptHistoryMapper extends BaseMapperX<PptHistoryDO> {

//    default PageResult<PptHistoryDO> selectPage(PptHistoryPageReqVO reqVO) {
//        return selectPage(reqVO, new LambdaQueryWrapperX<PptHistoryDO>()
//                .eqIfPresent(PptHistoryDO::getPptTitle, reqVO.getPptTitle())
//                .eqIfPresent(PptHistoryDO::getPptFile, reqVO.getPptFile())
//                .eqIfPresent(PptHistoryDO::getClientUserId, reqVO.getClientUserId())
//                .betweenIfPresent(PptHistoryDO::getCreateTime, reqVO.getCreateTime())
//                .orderByDesc(PptHistoryDO::getId));
//    }

    default List<cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO> selectList(cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO reqVO) {
        MPJLambdaWrapper<PptHistoryDO> wrapper = new MPJLambdaWrapper<PptHistoryDO>()
                .selectAll(PptHistoryDO.class)
                .select(UserDO::getNickname)
                .leftJoin(UserDO.class, UserDO::getId, PptHistoryDO::getClientUserId)
                .eqIfExists(PptHistoryDO::getPptTitle, reqVO.getPptTitle())
                .eqIfExists(PptHistoryDO::getPptFiletype, reqVO.getPptFiletype())
                .eqIfExists(PptHistoryDO::getClientUserId, reqVO.getClientUserId())
                .orderByDesc(PptHistoryDO::getId);
        return selectJoinList(cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO.class, wrapper);
    }

    default PageResult<PptHistoryDTO> selectPage(PptHistoryPageReqVO reqVO) {
        Page<PptHistoryDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<PptHistoryDO> wrapper = new MPJLambdaWrapper<PptHistoryDO>()
                .selectAll(PptHistoryDO.class)
                .select(UserDO::getNickname)
                .leftJoin(UserDO.class, UserDO::getId, PptHistoryDO::getClientUserId)
                .eqIfExists(PptHistoryDO::getPptTitle, reqVO.getPptTitle())
                .eqIfExists(PptHistoryDO::getPptFiletype, reqVO.getPptFiletype())
                .eqIfExists(UserDO::getNickname, reqVO.getNickname())
                .eqIfExists(PptHistoryDO::getClientUserId, reqVO.getClientUserId())
                .orderByDesc(PptHistoryDO::getId);
        IPage<PptHistoryDTO> pageResult = selectJoinPage(page, PptHistoryDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }


}
