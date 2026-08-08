package cn.iocoder.teach-ai.module.clientChat.framework.agent;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.service.aiService.ConsultantService;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.TokenStreamContext;
import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.SystemMessageApi;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.dto.SystemMessageDTO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoderAgent implements AsyncNodeAction<State> {

    @Resource
    private ConsultantService consultantService;

    @Value("${consultant.system-message}")
    private String defaultSystemMessage;

    @Resource
    private SystemMessageApi systemMessageApi;

    @Override
    public CompletableFuture<Map<String, Object>> apply(State state) {
        var fullContext = buildFullContext(state.messages());

        var result = new CompletableFuture<Map<String, Object>>();

        // 系统提示词
        String systemMessage = "";
        SystemMessageDTO messageDTO = new SystemMessageDTO().setStatus("0");
        CommonResult<List<SystemMessageDTO>> messageResult =
                systemMessageApi.getSystemMessageList(messageDTO);
        for (SystemMessageDTO dto : messageResult.getCheckedData()) {
            systemMessage += dto.getSystemMessageText();
        }
        if (systemMessage.isEmpty()) {
            systemMessage = defaultSystemMessage;
        }

        TokenStream tokenStream = consultantService.coderStream(fullContext, systemMessage);
        StringBuilder stringBuilder = new StringBuilder();
        tokenStream.onPartialResponse((String partialResponse) -> {
            var memoryId = ClientUserContext.getCurrentMemoryId();
            if (memoryId != null && TokenStreamContext.has(memoryId)) {
                TokenStreamContext.emitFiltered(memoryId, partialResponse);
            }
//            log.info( "{}", partialResponse );

        }).onToolExecuted((ToolExecution toolExecution) -> {
            log.info("""
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                {}
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                {}
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                """,
                    toolExecution.request().name(), toolExecution.result());
        }).onCompleteResponse((ChatResponse r) -> {
//            log.info( "{}\n{} ", r.metadata().toString(), r.toString());
            stringBuilder.append(r.aiMessage().text());
            result.complete( Map.of( "messages", AiMessage.from(stringBuilder.toString()) ) );
        }).onError((Throwable error) -> {
            log.error( "{}", error.getMessage(), error );
            result.completeExceptionally(error);
        }).start();

        return result;
    }

    private String buildFullContext(List<ChatMessage> messages) {
        return messages.stream()
            .map(m -> switch (m.type()) {
                case USER -> "用户: " + ((UserMessage) m).singleText();
                case AI   -> "助手: " + ((AiMessage) m).text();
                default   -> "";
            })
            .collect(Collectors.joining("\n\n"));
    }
}
