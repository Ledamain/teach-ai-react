package cn.iocoder.teach-ai.module.clientSystem.service.countrecord;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.countrecord.CountRecordMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * 使用次数记录 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class CountRecordServiceImpl implements CountRecordService {

    @Resource
    private CountRecordMapper countRecordMapper;

    @Override
    public Long createCountRecord(CountRecordSaveReqVO createReqVO) {
        if (!countRecordMapper.isUserRecordExists(createReqVO.getUserId())) {
            // 插入
            CountRecordDO countRecord = BeanUtils.toBean(createReqVO, CountRecordDO.class);
            countRecordMapper.insert(countRecord);

            // 返回
            return countRecord.getId();
        } else {
            return countRecordMapper.selectByUserId(createReqVO.getUserId()).getId();
        }
    }

    @Override
    public void updateCountRecord(CountRecordSaveReqVO updateReqVO) {
        // 校验存在
        validateCountRecordExists(updateReqVO.getId());
        // 更新
        CountRecordDO updateObj = BeanUtils.toBean(updateReqVO, CountRecordDO.class);
        countRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteCountRecord(Long id) {
        // 校验存在
        validateCountRecordExists(id);
        // 删除
        countRecordMapper.deleteById(id);
    }

    @Override
        public void deleteCountRecordListByIds(List<Long> ids) {
        // 删除
        countRecordMapper.deleteByIds(ids);
        }


    private void validateCountRecordExists(Long id) {
        if (countRecordMapper.selectById(id) == null) {
            throw exception(COUNT_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public CountRecordDO getCountRecord(Long id) {
        return countRecordMapper.selectById(id);
    }

    @Override
    public PageResult<CountRecordDO> getCountRecordPage(CountRecordPageReqVO pageReqVO) {
        return countRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CountRecordDTO> getCountRecordTrend() {
        return countRecordMapper.selectTrend();
    }

    @Override
    public Long getCountRecordDaily() {
        return countRecordMapper.selectDaily();
    }

}
