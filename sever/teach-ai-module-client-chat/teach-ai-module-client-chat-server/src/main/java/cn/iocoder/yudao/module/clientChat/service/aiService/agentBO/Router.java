package cn.iocoder.teach-ai.module.clientChat.service.aiService.agentBO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import static java.lang.String.format;

@Data
public class Router {

    @Description("Next worker: coder / rag / researcher / FINISH. Default to coder when unsure.")
    String next;

    @Override
    public String toString() {
        return format( "Router[next: %s]",next);
    }

}
