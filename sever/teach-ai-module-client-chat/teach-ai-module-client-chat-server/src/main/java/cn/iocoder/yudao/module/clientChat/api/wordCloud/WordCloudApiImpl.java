package cn.iocoder.teach-ai.module.clientChat.api.wordCloud;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.service.wordCloud.aiService.wordCloudService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@RestController
@Validated
public class WordCloudApiImpl implements WordCloudApi {
    @Resource
    private wordCloudService wordCloudService;

    @Override
    public CommonResult<String> wordCloudGen(String prompt){
        return success(wordCloudService.wordCloudGen(prompt));
    }
}
