package cn.iocoder.teach-ai.module.system.api.notify;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.iocoder.teach-ai.module.system.service.notify.NotifySendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class NotifyMessageSendApiImpl implements NotifyMessageSendApi {

    @Resource
    private NotifySendService notifySendService;

    @Override
    public CommonResult<Long> sendSingleMessageToAdmin(NotifySendSingleToUserReqDTO reqDTO) {
        return success(notifySendService.sendSingleNotifyToAdmin(reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

    @Override
    public CommonResult<Long> sendSingleMessageToMember(NotifySendSingleToUserReqDTO reqDTO) {
        return success(notifySendService.sendSingleNotifyToMember(reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams()));
    }

}
