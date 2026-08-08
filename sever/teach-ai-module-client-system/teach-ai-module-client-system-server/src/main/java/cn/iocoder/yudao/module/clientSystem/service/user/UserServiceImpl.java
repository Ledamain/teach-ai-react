package cn.iocoder.teach-ai.module.clientSystem.service.user;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.user.UserMapper;
import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.*;

/**
 * 客户端用户 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Long createUser(UserSaveReqVO createReqVO) {
        // 插入
        UserDO user = BeanUtils.toBean(createReqVO, UserDO.class);
        userMapper.insert(user);

        // 返回
        return user.getId();
    }

    @Override
    public void updateUser(UserSaveReqVO updateReqVO) {
        // 校验存在
        validateUserExists(updateReqVO.getId());
        // 更新
        UserDO updateObj = BeanUtils.toBean(updateReqVO, UserDO.class);
        log.info("更新用户信息: {}", updateObj);
        userMapper.updateById(updateObj);
    }

    @Override
    public void deleteUser(Long id) {
        // 校验存在
        validateUserExists(id);
        // 删除
        userMapper.deleteById(id);
    }

    @Override
        public void deleteUserListByIds(List<Long> ids) {
        // 删除
        userMapper.deleteByIds(ids);
        }


    private void validateUserExists(Long id) {
        if (userMapper.selectById(id) == null) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    @Override
    public UserDO getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public PageResult<UserDO> getUserPage(UserPageReqVO pageReqVO) {
        return userMapper.selectPage(pageReqVO);
    }

    @Override
    public UserDO getUserByUsernameAndPassword(String username, String password) {
        return userMapper.selectOne(UserDO::getClientUsername, username, UserDO::getClientPassword, password);
    }

    @Override
    public UserDO getUserByUsername(String username) {
        return userMapper.selectOne(UserDO::getClientUsername,username);
    }

    @Override
    public UserDO getUserByNickname(String nickname) {
        return userMapper.selectOne(UserDO::getNickname, nickname);
    }

    @Override
    public Long getUserCount() {
        return userMapper.selectCount();
    }

}
