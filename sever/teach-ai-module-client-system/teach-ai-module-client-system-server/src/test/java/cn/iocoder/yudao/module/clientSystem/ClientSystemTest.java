package cn.iocoder.teach-ai.module.clientSystem;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientChat.api.wordCloud.WordCloudApi;
import cn.iocoder.teach-ai.module.clientSystem.service.conversion.ConversionService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ClientSystemTest {


    @Resource
    private ChatHistoryApi chatHistoryApi;

    @Resource
    private ConversionService conversionService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private WordCloudApi wordCloudApi;

    void historyTest(){

        CommonResult<ChatMemoryDTO> chatHistory = chatHistoryApi.getChatHistory("用户2-1774002215576");
        System.out.println("历史记录为："+chatHistory);

    }

    @Test
    void wordCloudTest(){
        stringRedisTemplate.opsForValue().set("wordCloudData1", "1", Duration.ofSeconds(100));
//        if (stringRedisTemplate.opsForValue().get("wordCloudData") == null) {
//            List<ConversionDTO> list = conversionService.getConversionRecentWeek();
//            String content = "";
//            for (ConversionDTO conversion : list) {
//                content = content.concat("&" + conversion.getTitle());
//            }
//            content = content.substring(1);
//            stringRedisTemplate.opsForValue().set("wordCloudData", wordCloudApi.wordCloudGen(content).getData(), Duration.ofDays(1));
//        }
//        System.out.println(stringRedisTemplate.opsForValue().get("wordCloudData"));
    }
}
