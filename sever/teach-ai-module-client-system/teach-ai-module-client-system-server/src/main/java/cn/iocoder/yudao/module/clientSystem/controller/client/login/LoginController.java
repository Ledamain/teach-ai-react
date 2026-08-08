package cn.iocoder.teach-ai.module.clientSystem.controller.client.login;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.teach-ai.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo.LoginLogSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service.IClientMiniJwtService;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersLoginReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersLoginRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo.ClientUsersRegisterReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.service.login.LoginService;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.HttpIpUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.module.clientSystem.service.loginlog.LoginLogService;
import cn.iocoder.teach-ai.module.clientSystem.service.user.UserService;
import com.fhs.core.trans.anno.TransMethodResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.teach-ai.framework.apilog.core.enums.OperateTypeEnum.OTHER;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Tag(name = "客户端接口 - 登录")
@RestController
@RequestMapping("/client-api/client-system")
@Validated
public class LoginController {


    @Resource
    private IClientMiniJwtService jwtService;

    @Resource
    private LoginService loginService;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private UserService userService;


    private static String generateName() {
        //生成“用户+六位英文”
        return "用户" + generateRandomString(6);
    }

    private static String generateRandomString(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            sb.append((char) (Math.random() * 26 + 'a'));
        }
        return sb.toString();
    }

    /**
     * 生成token
     */
    @PostMapping("/clientToken")
    @Operation(summary = "生成Token")
    @ApiAccessLog(operateType = OTHER)
    public CommonResult<String> wxToken(@RequestBody Long userId) {
        return success(jwtService.createToken(String.valueOf(userId)));
    }

    /**
     * 客户端登录
     *
     * @param userLoginReqVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/clientLogin")
    @TransMethodResult
    @Operation(summary = "客户端登陆")
    @ApiAccessLog(operateType = OTHER)
    public CommonResult<ClientUsersLoginRespVO> clientLogin(@RequestBody @Valid ClientUsersLoginReqVO userLoginReqVO) {
        // 用户登陆Id
        String clientIp = HttpIpUtil.getClientIp();
        UserDO userDO = userService.getUserByUsername(userLoginReqVO.getUsername());
        LoginLogSaveReqVO logSaveReqVO = new LoginLogSaveReqVO().setUsername(userLoginReqVO.getUsername()).setNickname(userDO == null ? "未知用户" : userDO.getNickname()).setLoginTime(LocalDateTimeUtil.now()).setLoginResult(1L).setLoginIp(clientIp);
        ClientUsersLoginRespVO resp = null;
        try {
            resp = loginService.clientLogin(userLoginReqVO);
            logSaveReqVO = new LoginLogSaveReqVO().setUsername(userLoginReqVO.getUsername()).setNickname(resp.getUserInfo().getNickname()).setLoginTime(LocalDateTimeUtil.now()).setLoginResult(0L).setLoginIp(clientIp);
        } catch (Exception ignored) {
        } finally {
            loginLogService.createLoginLog(logSaveReqVO);
        }

        return success(resp);
    }


    /**
     * 客户端注册
     *
     * @param usersRegisterReqVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/clientRegister")
    @TransMethodResult
    @Operation(summary = "客户端注册")
    @ApiAccessLog(operateType = OTHER)
    public CommonResult<Long> clientRegister(@RequestBody @Valid ClientUsersRegisterReqVO usersRegisterReqVO) {
        return success(loginService.clientRegister(usersRegisterReqVO));
    }


    /**
     * 更新用户信息
     */
//    @Transactional(rollbackFor = Exception.class)
//    @PostMapping("/updateUserInfo")
//    @Operation(summary = "更新用户信息")
//    public CommonResult<WxUserRespVO> updateUserInfo(@RequestBody WxUserSaveReqVO trWxUser) {
//        try {
//            String openId = trWxUser.getUOpenid();
//            String nickName = trWxUser.getUName();
//            String avatarUrl = trWxUser.getUAvatar();
//            String uPhoneNumber = trWxUser.getUPhoneNumber();
//            // 手动非空判断
//            if (openId == null || openId.trim().isEmpty()) {
//                return error(1000_0_3, "openId不能为空");
//            }
//            if (nickName == null || nickName.trim().isEmpty()) {
//                return error(1000_0_12, "nickName不能为空");
//            }
//            if (uPhoneNumber == null || uPhoneNumber.trim().isEmpty()) {
//                return error(1000_0_13, "uPhoneNumber不能为空");
//            }
//            WxUserDO user = wxUserService.getWxUserByOpenId(openId);
//            if (user == null) {
//                return error(WX_USER_NOT_EXISTS);
//            }
//            user.setUName(nickName);
//            user.setUAvatar(avatarUrl);
//            user.setUPhoneNumber(uPhoneNumber);
//            StudentUserDTO student = studentService.getStudentByPhoneNumber(uPhoneNumber);
//            if (student != null) {
//                student.setUserId(user.getId());
//                student.setUpdater("wx_user-" + user.getUOpenId());
//                studentService.updateStudent(BeanUtils.toBean(student, StudentSaveReqVO.class));
//            }
//            user.setUpdater("wx_user-" + user.getUOpenId());
//            wxUserService.updateWxUser(user);
//            return success(BeanUtils.toBean(user, WxUserRespVO.class));
//        } catch (Exception e) {
//            return error(1000_0_11, e.getMessage());
//        }
//    }


}
