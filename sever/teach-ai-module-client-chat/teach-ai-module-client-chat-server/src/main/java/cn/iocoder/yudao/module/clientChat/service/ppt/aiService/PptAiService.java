package cn.iocoder.teach-ai.module.clientChat.service.ppt.aiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, //指定手动装配
        chatModel = "openAiChatModel", //配置阻塞式对话模型
        contentRetriever = "PptContentRetriever"
)
public interface PptAiService {

    @SystemMessage("你是一个用于生成ppt大纲的文件分析助手，请结合我给你的问题和将从向量数据库中获取的文件内容总结成一段500字以内的ppt主题的提示词")
    String pptChat(String prompt);

}
