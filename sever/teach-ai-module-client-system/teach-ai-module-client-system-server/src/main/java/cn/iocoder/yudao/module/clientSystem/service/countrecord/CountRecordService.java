package cn.iocoder.teach-ai.module.clientSystem.service.countrecord;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 使用次数记录 Service 接口
 *
 * @author waynelam
 */
public interface CountRecordService {

    /**
     * 创建使用次数记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCountRecord(@Valid CountRecordSaveReqVO createReqVO);

    /**
     * 更新使用次数记录
     *
     * @param updateReqVO 更新信息
     */
    void updateCountRecord(@Valid CountRecordSaveReqVO updateReqVO);

    /**
     * 删除使用次数记录
     *
     * @param id 编号
     */
    void deleteCountRecord(Long id);

    /**
    * 批量删除使用次数记录
    *
    * @param ids 编号
    */
    void deleteCountRecordListByIds(List<Long> ids);

    /**
     * 获得使用次数记录
     *
     * @param id 编号
     * @return 使用次数记录
     */
    CountRecordDO getCountRecord(Long id);

    /**
     * 获得使用次数记录分页
     *
     * @param pageReqVO 分页查询
     * @return 使用次数记录分页
     */
    PageResult<CountRecordDO> getCountRecordPage(CountRecordPageReqVO pageReqVO);

    /**
     * 获得今日使用次数排行
     *
     * @return 使用次数记录列表
     */
    List<CountRecordDTO> getCountRecordTrend();

    /**
     * 获得今日使用次数
     *
     * @return 今日使用次数记录
     */
    Long getCountRecordDaily();

}
