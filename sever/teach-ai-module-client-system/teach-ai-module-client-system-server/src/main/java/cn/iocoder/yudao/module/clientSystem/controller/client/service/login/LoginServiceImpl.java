package cn.iocoder.teach-ai.module.clientSystem.controller.client.service.login;

import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service.IClientMiniJwtService;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersLoginReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersLoginRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersRegisterReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersUserInfoRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.module.clientSystem.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;



@Service
public class LoginServiceImpl implements LoginService{

    @Value("${consultant.client-user-default-avator}")
    private String defaultAvator;

    @Resource
    private UserService userService;

    @Resource
    private IClientMiniJwtService jwtService;

    @Override
    public ClientUsersLoginRespVO clientLogin(ClientUsersLoginReqVO reqVO) {

        //参数校验
        if (reqVO.getUsername() == null){
            throw exception(USERNAME_NOT_NULL);
        }
        if (reqVO.getPassword() == null){
            throw exception(PASSWORD_NOT_NULL);
        }
        UserDO user = userService.getUserByUsernameAndPassword(reqVO.getUsername(), reqVO.getPassword());
        if (user == null){
            throw exception(USER_NOT_EXISTS);
        }

        String token = jwtService.createToken(String.valueOf(user.getId()));
        ClientUsersLoginRespVO clientUsersLoginRespVO = new ClientUsersLoginRespVO();
        clientUsersLoginRespVO.setUserInfo(BeanUtils.toBean(user, ClientUsersUserInfoRespVO.class));
        clientUsersLoginRespVO.setAccessToken(token);

        // 设置登录时间
        UserSaveReqVO userSaveReqVO = new UserSaveReqVO();
        userSaveReqVO.setId(user.getId());
        userSaveReqVO.setLastLoginTime(LocalDateTime.now());
        userService.updateUser(userSaveReqVO);

        return clientUsersLoginRespVO;
    }

    @Override
    public Long clientRegister(ClientUsersRegisterReqVO reqVO) {
        //参数校验
        if (reqVO.getUsername() == null){
            throw exception(USERNAME_NOT_NULL);
        }
        if (reqVO.getPassword() == null) {
            throw exception(PASSWORD_NOT_NULL);
        }
        if(reqVO.getNickname() == null){
            throw exception(NICKNAME_NOT_NULL);
        }
        UserDO userByUsername = userService.getUserByUsername(reqVO.getUsername());
        if (userByUsername != null) {
            throw exception(USERNAME_HAS_EXIST);
        }
        UserDO userByNickname = userService.getUserByNickname(reqVO.getNickname());
        if (userByNickname != null) {
            throw exception(NICKNAME_HAS_EXIST);
        }

        UserSaveReqVO userSaveReqVO = new UserSaveReqVO();
        userSaveReqVO.setClientUsername(reqVO.getUsername());
        userSaveReqVO.setNickname(reqVO.getNickname());
        userSaveReqVO.setClientPassword(reqVO.getPassword());
        userSaveReqVO.setClientAvator(defaultAvator);
        return userService.createUser(userSaveReqVO);
    }
}
