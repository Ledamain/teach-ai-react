package cn.iocoder.teach-ai.module.clientSystem.api.system;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.SystemMessageApi;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.dto.SystemMessageDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo.SystemMessagePageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.service.systemmessage.SystemMessageService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class SystemMessageApiImpl implements SystemMessageApi {

    @Resource
    private SystemMessageService systemMessageService;

    @Override
    public CommonResult<List<SystemMessageDTO>> getSystemMessageList(SystemMessageDTO messageDTO) {
        SystemMessagePageReqVO bean = BeanUtils.toBean(messageDTO, SystemMessagePageReqVO.class);
        return CommonResult.success(BeanUtils.toBean(systemMessageService.getSystemMessageList(bean), SystemMessageDTO.class));
    }
}
