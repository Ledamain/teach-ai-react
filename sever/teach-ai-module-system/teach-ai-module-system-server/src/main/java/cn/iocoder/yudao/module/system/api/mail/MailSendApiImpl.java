package cn.iocoder.teach-ai.module.system.api.mail;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import cn.iocoder.teach-ai.module.system.service.mail.MailSendService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class MailSendApiImpl implements MailSendApi {

    @Resource
    private MailSendService mailSendService;

    @Override
    public CommonResult<Long> sendSingleMailToAdmin(MailSendSingleToUserReqDTO reqDTO) {
        return success(mailSendService.sendSingleMailToAdmin(reqDTO.getUserId(),
                reqDTO.getToMails(), reqDTO.getCcMails(), reqDTO.getBccMails(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams(), reqDTO.getAttachments()));
    }

    @Override
    public CommonResult<Long> sendSingleMailToMember(MailSendSingleToUserReqDTO reqDTO) {
        return success(mailSendService.sendSingleMailToMember(reqDTO.getUserId(),
                reqDTO.getToMails(), reqDTO.getCcMails(), reqDTO.getBccMails(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams(), reqDTO.getAttachments()));
    }

}
