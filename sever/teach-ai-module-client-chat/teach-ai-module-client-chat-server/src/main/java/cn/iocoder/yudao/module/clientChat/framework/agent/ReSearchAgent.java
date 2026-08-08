package cn.iocoder.teach-ai.module.clientChat.framework.agent;

import cn.iocoder.teach-ai.module.clientChat.service.aiService.ConsultantService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReSearchAgent implements NodeAction<State> {

    @Resource
    private ConsultantService consultantService;

    @Override
    public Map<String, Object> apply(State state) throws Exception {
        var fullContext = buildFullContext(state.messages());
        var result = consultantService.researchChat(fullContext);
        log.info("ReSearch Result: {}", result );
        return Map.of("messages", AiMessage.from(result));
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
