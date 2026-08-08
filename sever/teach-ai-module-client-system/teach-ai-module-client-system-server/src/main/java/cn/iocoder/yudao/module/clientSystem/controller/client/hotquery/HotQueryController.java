package cn.iocoder.teach-ai.module.clientSystem.controller.client.hotquery;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.hotquery.HotQueryApi;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.ChatParamDTO;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.InitiatePptCreationResp;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client-api/client-system/hot-query")
public class HotQueryController {

    @Resource
    private HotQueryApi hotQueryApi;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/get")
    @ResponseBody
    public CommonResult<String> initiatePptCreation() {
        String hotQuery = (String) redisTemplate.opsForValue().get("hot_query");
        if (hotQuery == null) {
            CommonResult<String> result = hotQueryApi.getHotQuery();
            redisTemplate.opsForValue().set("hot_query", result.getCheckedData(), 2, TimeUnit.HOURS);
            return result;
        }
        return CommonResult.success(hotQuery);
    }

}
