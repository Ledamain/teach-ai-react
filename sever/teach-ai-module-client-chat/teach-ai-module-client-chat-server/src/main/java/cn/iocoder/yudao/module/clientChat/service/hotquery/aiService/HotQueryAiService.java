package cn.iocoder.teach-ai.module.clientChat.service.hotquery.aiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, //指定手动装配
        chatModel = "openAiChatModel" //配置阻塞式对话模型
)
public interface HotQueryAiService {

    @SystemMessage("你需要生成9 条适合智能体首页展示的热门提问内容，内容为日常高频、实用易懂、用户感兴趣的问题，多以教学相关，风格简洁口语化，长度适中，适合用户点击查看。输出格式为纯 JSON 字符串数组，仅包含问题文本，不添加 id、序号、多余字段或说明，直接返回可被前端直接解析的 JSON 格式，不可以换行")
    String hotQueryChat(String prompt);

}
