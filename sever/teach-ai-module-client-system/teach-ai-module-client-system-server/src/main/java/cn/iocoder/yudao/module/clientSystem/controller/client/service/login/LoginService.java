package cn.iocoder.teach-ai.module.clientSystem.controller.client.service.login;

import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersLoginReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersLoginRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersRegisterReqVO;
import org.apache.ibatis.annotations.Lang;

public interface LoginService {

    ClientUsersLoginRespVO clientLogin(ClientUsersLoginReqVO reqVO);

    Long clientRegister(ClientUsersRegisterReqVO reqVO);

}
