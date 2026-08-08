package cn.iocoder.teach-ai.module.clientChat.framework.agent.config;

import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.studio.LangGraphStudioServer;
import org.bsc.langgraph4j.studio.springboot.LangGraphStudioConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
public class StudioConfiguration extends LangGraphStudioConfig implements WebMvcConfigurer {

    private final StateGraph<State> workFlow;
    private final CompileConfig compileConfig;

    public StudioConfiguration(StateGraph<State> workFlow,
                               CompileConfig compileConfig) {
        this.workFlow = workFlow;
        this.compileConfig = compileConfig;
    }

    @Override
    public Map<String, LangGraphStudioServer.Instance> instanceMap() {
        var instance = LangGraphStudioServer.Instance.builder()
                .title("AI智学教学辅助智能体")
                .addInputStringArg("messages")
                .graph(workFlow)
                .compileConfig(compileConfig)
                .build();

        return Map.of("consultant", instance);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html",
                        "/favicon*.svg",
                        "/webui*",
                        "/katex*",
                        "/*Diagram*",
                        "/flowchart*",
                        "/mindmap*",
                        "/timeline*")
                .addResourceLocations("classpath:/static/");
    }
}
