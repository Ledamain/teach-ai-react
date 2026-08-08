package cn.iocoder.teach-ai.module.clientSystem.service.user;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import jakarta.validation.Valid;

/**
 * 客户端用户 Service 接口
 *
 * @author 芋道源码
 */
public interface UserService {

    /**
     * 创建客户端用户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUser(@Valid UserSaveReqVO createReqVO);

    /**
     * 更新客户端用户
     *
     * @param updateReqVO 更新信息
     */
    void updateUser(@Valid UserSaveReqVO updateReqVO);

    /**
     * 删除客户端用户
     *
     * @param id 编号
     */
    void deleteUser(Long id);

    /**
    * 批量删除客户端用户
    *
    * @param ids 编号
    */
    void deleteUserListByIds(List<Long> ids);

    /**
     * 获得客户端用户
     *
     * @param id 编号
     * @return 客户端用户
     */
    UserDO getUser(Long id);

    /**
     * 获得客户端用户分页
     *
     * @param pageReqVO 分页查询
     * @return 客户端用户分页
     */
    PageResult<UserDO> getUserPage(UserPageReqVO pageReqVO);

    /**
     * 获得客户端用户
     *
     * @param username 用户名
     * @param password 密码
     * @return 客户端用户
     */
    UserDO getUserByUsernameAndPassword(String username, String password);

    /**
     * 获得客户端用户
     *
     * @param username 用户名
     * @return 客户端用户
     */
    UserDO getUserByUsername(String username);

    /**
     * 获得客户端用户
     *
     * @param nickname 用户名
     * @return 客户端用户
     */
    UserDO getUserByNickname(String nickname);
    /**
     * 获得用户数量
     *
     * @return 用户数量
     */
    Long getUserCount();

}
