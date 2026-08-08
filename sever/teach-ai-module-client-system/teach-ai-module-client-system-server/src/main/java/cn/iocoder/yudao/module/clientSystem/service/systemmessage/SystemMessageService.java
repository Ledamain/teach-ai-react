package cn.iocoder.teach-ai.module.clientSystem.service.systemmessage;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.systemmessage.SystemMessageDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 系统提示词 Service 接口
 *
 * @author 芋道源码
 */
public interface SystemMessageService {

    /**
     * 创建系统提示词
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSystemMessage(@Valid SystemMessageSaveReqVO createReqVO);

    /**
     * 更新系统提示词
     *
     * @param updateReqVO 更新信息
     */
    void updateSystemMessage(@Valid SystemMessageSaveReqVO updateReqVO);

    /**
     * 删除系统提示词
     *
     * @param id 编号
     */
    void deleteSystemMessage(Long id);

    /**
    * 批量删除系统提示词
    *
    * @param ids 编号
    */
    void deleteSystemMessageListByIds(List<Long> ids);

    /**
     * 获得系统提示词
     *
     * @param id 编号
     * @return 系统提示词
     */
    SystemMessageDO getSystemMessage(Long id);

    /**
     * 获得系统提示词分页
     *
     * @param pageReqVO 分页查询
     * @return 系统提示词分页
     */
    PageResult<SystemMessageDO> getSystemMessagePage(SystemMessagePageReqVO pageReqVO);

    /**
     * 获得全部系统提示词
     *
      * @return 系统提示词列表
     */
    List<SystemMessageDO> getSystemMessageList(SystemMessagePageReqVO pageReqVO);

}
