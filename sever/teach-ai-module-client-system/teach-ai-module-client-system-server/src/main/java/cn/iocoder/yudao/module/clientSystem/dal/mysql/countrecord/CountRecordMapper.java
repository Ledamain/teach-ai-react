package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.countrecord;

import java.time.LocalDateTime;
import java.util.*;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo.*;

/**
 * 使用次数记录 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface CountRecordMapper extends BaseMapperX<CountRecordDO> {

    default PageResult<CountRecordDO> selectPage(CountRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CountRecordDO>()
                .eqIfPresent(CountRecordDO::getRecordCount, reqVO.getRecordCount())
                .eqIfPresent(CountRecordDO::getUserId, reqVO.getUserId())
                .betweenIfPresent(CountRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CountRecordDO::getId));
    }

    default List<CountRecordDTO> selectTrend() {
        LocalDateTime start = DateUtil.beginOfDay(DateUtil.date()).toLocalDateTime();
        LocalDateTime end = DateUtil.endOfDay(DateUtil.date()).toLocalDateTime();
        return selectJoinList(CountRecordDTO.class, new MPJLambdaWrapperX<CountRecordDO>()
                .selectAll(CountRecordDO.class)
                .selectAs(UserDO::getNickname, CountRecordDTO::getClientUserName)
                .leftJoin(UserDO.class, UserDO::getId, CountRecordDTO::getUserId)
                .ge(CountRecordDO::getCreateTime, start)
                .le(CountRecordDO::getCreateTime, end)
                .orderByDesc(CountRecordDO::getRecordCount)
                .last(" LIMIT 5")
        );
    }

    default Long selectDaily() {
        LocalDateTime start = DateUtil.beginOfDay(DateUtil.date()).toLocalDateTime();
        LocalDateTime end = DateUtil.endOfDay(DateUtil.date()).toLocalDateTime();
        QueryWrapperX<CountRecordDO> wrapper = new QueryWrapperX<>();
        wrapper.select("IFNULL(SUM(record_count), 0)")
                .ge("create_time", start)
                .le("create_time", end);
        List<Object> result = selectObjs(wrapper);
        if (result != null && !result.isEmpty() && result.get(0) != null) {
            return ((Number) result.get(0)).longValue();
        }
        return 0L;
    }

    default Boolean isUserRecordExists(Long userId) {
        LocalDateTime start = DateUtil.beginOfDay(DateUtil.date()).toLocalDateTime();
        LocalDateTime end = DateUtil.endOfDay(DateUtil.date()).toLocalDateTime();
        return exists(new LambdaQueryWrapperX<CountRecordDO>()
                .eq(CountRecordDO::getUserId, userId)
                .ge(CountRecordDO::getCreateTime, start)
                .le(CountRecordDO::getCreateTime, end));
    }

    default CountRecordDO selectByUserId(Long userId) {
        LocalDateTime start = DateUtil.beginOfDay(DateUtil.date()).toLocalDateTime();
        LocalDateTime end = DateUtil.endOfDay(DateUtil.date()).toLocalDateTime();
        return selectOne(new LambdaQueryWrapperX<CountRecordDO>()
                .eq(CountRecordDO::getUserId, userId)
                .ge(CountRecordDO::getCreateTime, start)
                .le(CountRecordDO::getCreateTime, end));
    }

}
