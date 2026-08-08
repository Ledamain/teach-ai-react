package cn.iocoder.teach-ai.module.clientChat.service.chatmemory.aiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, //指定手动装配
        chatModel = "openAiChatModel" //配置阻塞式对话模型
)
public interface MemoryTitleAiService {

    @SystemMessage("你是一个标题生成助手。请用不超过15个字概括以下用户问题的主题，直接输出标题文本，不要加引号、标点或任何修饰语")
    String generateTitle(String prompt);

}

