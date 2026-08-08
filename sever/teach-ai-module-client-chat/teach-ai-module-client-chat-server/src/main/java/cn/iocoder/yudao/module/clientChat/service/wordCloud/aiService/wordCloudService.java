package cn.iocoder.teach-ai.module.clientChat.service.wordCloud.aiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, //指定手动装配
        chatModel = "openAiChatModel" //配置阻塞式对话模型
)
public interface wordCloudService {

    @SystemMessage("我正在做一个AI会话热门话题分析工具，你不需要严格匹配，只需要意思相近即可算作同一个热门词，请用下面的格式组成一个JSON数组，为我输出热门词，输出的内容一行显示，不要任何换行：{ name: '互联网服务', value: 1000 }，其中name字段是热门词，value为词频，下面是我的会话标题列表，由多个标题组成，不同标题之间用&符号分隔：")
    String wordCloudGen(String prompt);
}
