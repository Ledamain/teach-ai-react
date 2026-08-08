package cn.iocoder.teach-ai.module.clientSystem.service.systemmessage;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.systemmessage.SystemMessageDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.systemmessage.SystemMessageMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.*;

/**
 * 系统提示词 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SystemMessageServiceImpl implements SystemMessageService {

    @Resource
    private SystemMessageMapper systemMessageMapper;

    @Override
    public Long createSystemMessage(SystemMessageSaveReqVO createReqVO) {

        if (createReqVO.getSystemMessageTitle() == null){
            throw exception(SYSTEM_MESSAGE_TITLE_NOT_NULL);
        }
        if (createReqVO.getSystemMessageText() == null && createReqVO.getSystemMessageTextUrl() == null){
            throw exception(SYSTEM_MESSAGE_CONTENT_NOT_NULL);
        }
        if (createReqVO.getSystemMessageText() != null && createReqVO.getSystemMessageTextUrl() != null){
            throw exception(SYSTEM_MESSAGE_CONTENT_ONLY_ONE_TYPE);
        }

        // 插入
        SystemMessageDO systemMessage = BeanUtils.toBean(createReqVO, SystemMessageDO.class);
        if (systemMessage.getSystemMessageTextUrl() != null){
            systemMessage.setTextStatus("0");
        }
        systemMessageMapper.insert(systemMessage);

        // 返回
        return systemMessage.getId();
    }

    @Override
    public void updateSystemMessage(SystemMessageSaveReqVO updateReqVO) {

        if (updateReqVO.getSystemMessageTitle() == null){
            throw exception(SYSTEM_MESSAGE_TITLE_NOT_NULL);
        }
        if (updateReqVO.getSystemMessageText() == null && updateReqVO.getSystemMessageTextUrl() == null){
            throw exception(SYSTEM_MESSAGE_CONTENT_NOT_NULL);
        }
        if (updateReqVO.getSystemMessageText() != null && updateReqVO.getSystemMessageTextUrl() != null){
            throw exception(SYSTEM_MESSAGE_CONTENT_ONLY_ONE_TYPE);
        }

        // 校验存在
        validateSystemMessageExists(updateReqVO.getId());
        // 更新
        SystemMessageDO updateObj = BeanUtils.toBean(updateReqVO, SystemMessageDO.class);
        if (updateObj.getSystemMessageTextUrl() != null){
            updateObj.setTextStatus("0");
        }
        systemMessageMapper.updateById(updateObj);
    }

    @Override
    public void deleteSystemMessage(Long id) {
        // 校验存在
        validateSystemMessageExists(id);
        // 删除
        systemMessageMapper.deleteById(id);
    }

    @Override
        public void deleteSystemMessageListByIds(List<Long> ids) {
        // 删除
        systemMessageMapper.deleteByIds(ids);
        }


    private void validateSystemMessageExists(Long id) {
        if (systemMessageMapper.selectById(id) == null) {
            throw exception(SYSTEM_MESSAGE_NOT_EXISTS);
        }
    }

    @Override
    public SystemMessageDO getSystemMessage(Long id) {
        return systemMessageMapper.selectById(id);
    }

    @Override
    public PageResult<SystemMessageDO> getSystemMessagePage(SystemMessagePageReqVO pageReqVO) {
        return systemMessageMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SystemMessageDO> getSystemMessageList(SystemMessagePageReqVO pageReqVO) {
        return  systemMessageMapper.selectList(pageReqVO);
    }

}
