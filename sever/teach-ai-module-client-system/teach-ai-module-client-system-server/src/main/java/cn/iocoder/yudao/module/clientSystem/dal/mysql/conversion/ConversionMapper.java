package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.conversion;

import java.time.LocalDateTime;
import java.util.*;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo.*;

/**
 * 会话历史 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface ConversionMapper extends BaseMapperX<ConversionDO> {

    default PageResult<ConversionDTO> selectPage(ConversionPageReqVO reqVO) {
        Page<ConversionDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        MPJLambdaWrapper<ConversionDO> wrapper = new MPJLambdaWrapper<ConversionDO>()
                .selectAll(ConversionDO.class)
                .selectAs(UserDO::getNickname, ConversionDTO::getClientUserName)
                .leftJoin(UserDO.class, UserDO::getId, ConversionDO::getClientUserId)
                .eqIfExists(ConversionDO::getConversionId, reqVO.getConversionId())
                .likeIfExists(UserDO::getNickname, reqVO.getClientUserName())
                .likeIfExists(ConversionDO::getTitle, reqVO.getTitle())
                .orderByDesc(ConversionDO::getCreateTime);
        IPage<ConversionDTO> pageResult = selectJoinPage(page, ConversionDTO.class, wrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());

//        return selectJoinPage(reqVO, new LambdaQueryWrapperX<ConversionDO>()
//                .eqIfPresent(ConversionDO::getConversionId, reqVO.getConversionId())
//                .eqIfPresent(ConversionDO::getClientUserId, reqVO.getClientUserId())
//                .eqIfPresent(ConversionDO::getTitle, reqVO.getTitle())
//                .orderByDesc(ConversionDO::getId));
    }

    default ConversionDO selectByConversionId(Long conversionId){
        return selectOne(new LambdaQueryWrapperX<ConversionDO>().eq(ConversionDO::getConversionId, conversionId));
    }

    default List<ConversionDTO> selectListRecentWeek() {
        LocalDateTime start = DateUtil.endOfDay(DateUtil.date()).toLocalDateTime().minusDays(7);
        LocalDateTime end = DateUtil.endOfDay(DateUtil.date()).toLocalDateTime();
        return selectJoinList(ConversionDTO.class, new MPJLambdaWrapperX<ConversionDO>()
                .selectAll(ConversionDO.class)
                .selectAs(UserDO::getNickname, ConversionDTO::getClientUserName)
                .leftJoin(UserDO.class, UserDO::getId, ConversionDO::getClientUserId)
                .ge(ConversionDO::getCreateTime, start)
                .le(ConversionDO::getCreateTime, end)
                .orderByDesc(ConversionDO::getCreateTime));
    }
}
