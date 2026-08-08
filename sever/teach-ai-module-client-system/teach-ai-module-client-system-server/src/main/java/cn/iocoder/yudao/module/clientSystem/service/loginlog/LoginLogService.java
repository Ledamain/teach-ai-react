package cn.iocoder.teach-ai.module.clientSystem.service.loginlog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.loginlog.LoginLogDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 客户端登录日志 Service 接口
 *
 * @author waynelam
 */
public interface LoginLogService {

    /**
     * 创建客户端登录日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLoginLog(@Valid LoginLogSaveReqVO createReqVO);

    /**
     * 更新客户端登录日志
     *
     * @param updateReqVO 更新信息
     */
    void updateLoginLog(@Valid LoginLogSaveReqVO updateReqVO);

    /**
     * 删除客户端登录日志
     *
     * @param id 编号
     */
    void deleteLoginLog(Long id);

    /**
    * 批量删除客户端登录日志
    *
    * @param ids 编号
    */
    void deleteLoginLogListByIds(List<Long> ids);

    /**
     * 获得客户端登录日志
     *
     * @param id 编号
     * @return 客户端登录日志
     */
    LoginLogDO getLoginLog(Long id);

    /**
     * 获得客户端登录日志分页
     *
     * @param pageReqVO 分页查询
     * @return 客户端登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

}
