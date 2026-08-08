package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import dev.langchain4j.data.message.ChatMessage;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG 子图边条件 — 判断生成结果质量。
 * 返回: "useful" | "not_supported" (幻觉) | "not_useful" (未解决问题)
 *
 * 用法: edge_async(gradeGenerationAgent::apply)
 */
@Service
@Slf4j
public class GradeGenerationAgent {

    private final ChatModel chatModel;

    public GradeGenerationAgent(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String apply(State state) {
        log.debug("---GRADE GENERATION---");
        var question = state.ragQuestion();
        var generation = state.ragGeneration().orElse("");
        var docs = state.ragDocuments();

        // 1. 幻觉检查
        var context = String.join("\n", docs);
        var hallucinationCheck = List.<ChatMessage>of(
                SystemMessage.from("判断以下回答是否基于给定的事实。只回答 yes 或 no。"),
                UserMessage.from("事实:\n" + context + "\n\n回答: " + generation)
        );
        var hResp = chatModel.chat(hallucinationCheck);
        if (!hResp.aiMessage().text().trim().toLowerCase().contains("yes")) {
            log.debug("---回答未基于事实，重试生成---");
            return "not_supported";
        }

        // 2. 答案质量检查
        var answerCheck = List.<ChatMessage>of(
                SystemMessage.from("判断以下回答是否解决了用户问题。只回答 yes 或 no。"),
                UserMessage.from("问题: " + question + "\n\n回答: " + generation)
        );
        var aResp = chatModel.chat(answerCheck);
        if (aResp.aiMessage().text().trim().toLowerCase().contains("yes")) {
            log.debug("---回答合格，结束---");
            return "useful";
        }

        log.debug("---回答未解决问题，改写重试---");
        return "not_useful";
    }
}
