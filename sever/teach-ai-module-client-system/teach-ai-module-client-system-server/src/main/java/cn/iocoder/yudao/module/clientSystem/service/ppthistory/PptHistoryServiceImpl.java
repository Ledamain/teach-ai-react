package cn.iocoder.teach-ai.module.clientSystem.service.ppthistory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.ppthistory.PptHistoryMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * PPT历史记录 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class PptHistoryServiceImpl implements PptHistoryService {

    @Resource
    private PptHistoryMapper pptHistoryMapper;

    @Override
    public Long createPptHistory(PptHistorySaveReqVO createReqVO) {
        // 插入
        PptHistoryDO pptHistory = BeanUtils.toBean(createReqVO, PptHistoryDO.class);
        pptHistoryMapper.insert(pptHistory);

        // 返回
        return pptHistory.getId();
    }

    @Override
    public void updatePptHistory(PptHistorySaveReqVO updateReqVO) {
        // 校验存在
        validatePptHistoryExists(updateReqVO.getId());
        // 更新
        PptHistoryDO updateObj = BeanUtils.toBean(updateReqVO, PptHistoryDO.class);
        pptHistoryMapper.updateById(updateObj);
    }

    @Override
    public void deletePptHistory(Long id) {
        // 校验存在
        validatePptHistoryExists(id);
        // 删除
        pptHistoryMapper.deleteById(id);
    }

    @Override
        public void deletePptHistoryListByIds(List<Long> ids) {
        // 删除
        pptHistoryMapper.deleteByIds(ids);
        }


    private void validatePptHistoryExists(Long id) {
        if (pptHistoryMapper.selectById(id) == null) {
            throw exception(PPT_HISTORY_NOT_EXISTS);
        }
    }

    @Override
    public PptHistoryDO getPptHistory(Long id) {
        return pptHistoryMapper.selectById(id);
    }

    @Override
    public PptHistoryDO getPptHistoryByFileName(String fileName) {
        return pptHistoryMapper.selectOne(PptHistoryDO::getPptFile, fileName);
    }

    @Override
    public PageResult<PptHistoryDTO> getPptHistoryPage(PptHistoryPageReqVO pageReqVO) {
        return pptHistoryMapper.selectPage(pageReqVO);
    }

    @Override
    public List<cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO> getPptHistoryList(cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO historyDTO) {
        return pptHistoryMapper.selectList(historyDTO);
    }

}
