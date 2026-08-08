package cn.iocoder.teach-ai.module.clientSystem.api.chathistory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.chathistory.dto.ChatHistoryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ClientUserContext;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.module.clientSystem.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class chatHistoryApiImpl implements ChatHistoryApi {

    @Resource
    private UserService userService;

    @Override
    public CommonResult<ChatHistoryRespDTO> getClientLoginUser() {
        String userId = ClientUserContext.getCurrentUserId();
        UserDO user = userService.getUser(Long.valueOf(userId));
        System.out.println("openFeign Finishing");
        return success(BeanUtils.toBean(user, ChatHistoryRespDTO.class));    }
}
