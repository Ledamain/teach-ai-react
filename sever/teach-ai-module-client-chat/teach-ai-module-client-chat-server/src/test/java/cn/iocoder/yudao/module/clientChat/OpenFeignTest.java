package cn.iocoder.teach-ai.module.clientChat;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.chat.ClientChatApi;
import cn.iocoder.teach-ai.module.clientChat.api.exercises.dto.ExamPaperDTO;
import cn.iocoder.teach-ai.module.clientChat.api.wordCloud.WordCloudApi;
import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import cn.iocoder.teach-ai.module.clientChat.repository.ChatMemoryRepository;
import cn.iocoder.teach-ai.module.clientChat.service.exercises.ExercisesService;
import cn.iocoder.teach-ai.module.clientSystem.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.chathistory.dto.ChatHistoryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.ConversionApi;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

@SpringBootTest
public class OpenFeignTest {

    @Resource
    private ChatHistoryApi chatHistoryApi;

    @Resource
    private cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi ChatHistoryApi;

    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @Resource
    private ConversionApi conversionApi;

    @Resource
    private WordCloudApi wordCloudApi;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ExercisesService exercisesService;

    @Test
    void openFeignTest(){
        CommonResult<ChatHistoryRespDTO> clientLoginUser = chatHistoryApi.getClientLoginUser();
        System.out.println(clientLoginUser);
    }

    @Test
    void chatHistoryTest(){
//        CommonResult<List<ChatMessage>> chatHistory = ChatHistoryApi.getChatHistory("用户2-1770913835014");
//        System.out.println(chatHistory);

        Optional<ChatMemoryDO> memoryDO = chatMemoryRepository.findById("用户2-1770913835014");
        System.out.println("聊天记录："+memoryDO);
    }

    @Test
    void conversionTest(){
        CommonResult<ConversionDTO> conversionByConversionId = conversionApi.getConversionByConversionId(1774068538560L);
        System.out.println(conversionByConversionId);
    }

    @Test
    void chatTest(){
        String content = "如何提高工作效率&什么是区块链&帮我写一份营销方案&推荐适合新手的单反相机&怎样快速入睡&Python入门指南&帮我起个好听的英文名&如何制作草莓蛋糕&2026年旅游推荐&帮我翻译这篇文章&什么是机器学习&帮我写一篇科幻小说&怎样才能学会游泳&推荐几本心理学书籍&如何准备面试&解释一下相对论&帮我写个请假条&纽约现在几点&怎样种植多肉植物&什么是Web3.0&帮我写一段Java代码&推荐好玩的Switch游戏&如何缓解焦虑情绪&帮我修改英文语法&什么是元宇宙&制定减肥计划&关于环保的演讲稿&如何拍好人像摄影&推荐几部高分日剧&怎样学好英语口语&解释一下黑洞&帮我写一封情书&怎么做番茄炒蛋&什么是云计算&帮我写个Python脚本&推荐好用的效率工具&如何克服拖延症&帮我总结这篇报告&什么是AIGC&杭州游攻略&短视频脚本撰写&如何保养真皮沙发&推荐几款好喝的红酒&怎样提高记忆力&解释一下薛定谔的猫&帮我写一封感谢信&怎么做冰糖葫芦&什么是大数据&帮我写个前端页面&好听的华语流行歌曲";
        System.out.println(wordCloudApi.wordCloudGen(content));
    }

    @Test
    void redisTest(){
        stringRedisTemplate.opsForValue().set("test", "test", Duration.ofSeconds(10));
    }

    @Test
    void exercisesTest(){
        ExamPaperDTO paper = exercisesService.createExamPaper("帮我生成一份考试试卷");
        System.out.println("考试试卷："+paper);
    }

}
