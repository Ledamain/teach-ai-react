package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import dev.langchain4j.data.message.ChatMessage;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG 子图边条件 — 问题路由器。
 * 根据用户问题判断最佳数据源：向量库 or 网络搜索。
 * 未配置 Tavily 时自动退化为纯向量检索。
 *
 * 用法: edge_async(routeQuestionAgent::apply)
 */
@Service
@Slf4j
public class RouteQuestionAgent {

    private final ChatModel chatModel;
    private final String tavilyApiKey;

    public RouteQuestionAgent(
            @Qualifier("openAiChatModel") ChatModel chatModel,
            @Value("${tavily.api-key:${TAVILY_API_KEY:}}") String tavilyApiKey) {
        this.chatModel = chatModel;
        this.tavilyApiKey = tavilyApiKey;
    }

    public String apply(State state) {
        var question = state.ragQuestion();
        log.debug("---ROUTE QUESTION---");

        if (tavilyApiKey.isEmpty()) {
            log.debug("Tavily 未配置，默认走向量库");
            return "vectorstore";
        }

        var messages = List.<ChatMessage>of(
                SystemMessage.from("""
                    你是一个问题路由器。根据用户问题判断最佳数据源:
                    - vectorstore: 问题涉及公司产品、服务、课程预约、内部知识库
                    - web_search: 问题涉及通用知识、最新资讯、外部事件
                    只回答一个词: "vectorstore" 或 "web_search"。
                    """),
                UserMessage.from(question)
        );
        var response = chatModel.chat(messages);
        var routing = response.aiMessage().text().trim().toLowerCase();

        if (routing.contains("web")) {
            log.debug("---ROUTE TO WEB SEARCH---");
            return "web_search";
        }
        log.debug("---ROUTE TO VECTORSTORE---");
        return "vectorstore";
    }
}
