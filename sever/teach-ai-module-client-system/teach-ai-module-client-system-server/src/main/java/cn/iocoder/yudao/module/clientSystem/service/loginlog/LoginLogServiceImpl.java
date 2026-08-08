package cn.iocoder.teach-ai.module.clientSystem.service.loginlog;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.loginlog.LoginLogDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.loginlog.LoginLogMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.LOGIN_LOG_NOT_EXISTS;

/**
 * 客户端登录日志 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public Long createLoginLog(LoginLogSaveReqVO createReqVO) {
        // 插入
        LoginLogDO loginLog = BeanUtils.toBean(createReqVO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);

        // 返回
        return loginLog.getId();
    }

    @Override
    public void updateLoginLog(LoginLogSaveReqVO updateReqVO) {
        // 校验存在
        validateLoginLogExists(updateReqVO.getId());
        // 更新
        LoginLogDO updateObj = BeanUtils.toBean(updateReqVO, LoginLogDO.class);
        loginLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteLoginLog(Long id) {
        // 校验存在
        validateLoginLogExists(id);
        // 删除
        loginLogMapper.deleteById(id);
    }

    @Override
        public void deleteLoginLogListByIds(List<Long> ids) {
        // 删除
        loginLogMapper.deleteByIds(ids);
        }


    private void validateLoginLogExists(Long id) {
        if (loginLogMapper.selectById(id) == null) {
            throw exception(LOGIN_LOG_NOT_EXISTS);
        }
    }

    @Override
    public LoginLogDO getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

}
