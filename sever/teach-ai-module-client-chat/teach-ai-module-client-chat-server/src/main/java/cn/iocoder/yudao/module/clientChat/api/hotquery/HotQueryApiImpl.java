package cn.iocoder.teach-ai.module.clientChat.api.hotquery;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.hotquery.dto.HotQueryDTO;
import cn.iocoder.teach-ai.module.clientChat.service.hotquery.aiService.HotQueryAiService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Valid
public class HotQueryApiImpl implements HotQueryApi{

    @Resource
    private HotQueryAiService hotQueryAiService;

    @Override
    public CommonResult<String> getHotQuery() {
        return CommonResult.success(hotQueryAiService.hotQueryChat("你是智能助手，请回答热门问题"));
    }
}
