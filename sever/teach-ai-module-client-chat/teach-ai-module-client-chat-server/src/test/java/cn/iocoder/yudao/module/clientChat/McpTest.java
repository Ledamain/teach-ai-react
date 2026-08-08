package cn.iocoder.teach-ai.module.clientChat;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

@SpringBootTest
public class McpTest {

    @Resource
    private McpToolProvider provider;

    @Resource
    private McpClient mcpClient;

    interface chatService {
        String chat(String prompt);
    }

    @Test
    public void listTools(){
        mcpClient.listTools().forEach(tool -> System.out.println(tool.name()));
    }

    @Test
    public void mcpTest() {

        ChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-63f5743ce95546c1864d0a0ff7ee7e57")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("deepseek-v4-pro")
                .logRequests(true)
                .logResponses(true)
                .build();
        chatService build = AiServices.builder(chatService.class)
                .chatModel(model)
                .toolProvider(provider)
                .build();
        String result = build.chat("我身边的小度设备有哪些");
        System.out.println("任务结果："+result);
    }

    @Test
    public void skillPathTest() {
        System.out.println(Path.of("skills/"));
    }

}
