package cn.iocoder.teach-ai.module.clientChat.service.agent.impl;

import cn.iocoder.teach-ai.module.clientChat.service.agent.*;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 内容生成 Agent。
 * 基于学习路径，生成配套的教学内容摘要。
 */
@Component
@Order(30)
public class ContentGeneratorAgent implements Agent {

    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorAgent.class);

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Resource
    private ChatModel openAiChatModel;

    @Override
    public AgentRole getRole() { return AgentRole.CONTENT_GENERATOR; }

    @Override
    public int getOrder() { return 30; }

    @Override
    public AgentContext process(AgentContext ctx, AgentStatusEmitter emitter) {
        String taskId = (String) ctx.getAttributes().get("taskId");

        LearningPathRespDTO path = ctx.getLearningPath();
        if (path == null) {
            emitter.emit(taskId, AgentStatus.skipped(AgentRole.CONTENT_GENERATOR,
                    "无学习路径，跳过内容生成"));
            ctx.logStatus(AgentStatus.skipped(AgentRole.CONTENT_GENERATOR, "无路径数据"));
            return ctx;
        }

        emitter.emit(taskId, AgentStatus.processing(AgentRole.CONTENT_GENERATOR,
                "为路径节点生成配套教学内容...", 50));

        try {
            String content = generateContentSummary(ctx.getProfile(), path, ctx.getRepoCategoryName());
            ctx.setGeneratedContent(content);

            emitter.emit(taskId, AgentStatus.processing(AgentRole.CONTENT_GENERATOR,
                    "教学内容摘要已生成（" + (content != null ? content.length() : 0) + " 字符）", 80));
            ctx.logStatus(AgentStatus.completed(AgentRole.CONTENT_GENERATOR,
                    "已为 " + path.getTotalNodes() + " 个节点生成教学内容"));
        } catch (Exception e) {
            log.warn("ContentGenerator 异常: {}", e.getMessage());
            emitter.emit(taskId, AgentStatus.error(AgentRole.CONTENT_GENERATOR,
                    "内容生成异常", e.getMessage()));
        }

        return ctx;
    }

    private String generateContentSummary(StudentProfileRespDTO profile,
                                           LearningPathRespDTO path, String subject) {
        String profileInfo = profile != null
                ? "知识水平: " + profile.getKnowledgeLevel() + ", 学习风格: " + profile.getLearningStyle()
                : "通用";

        String prompt = String.format("""
            为以下学习路径生成一篇教学指导摘要。

            学科: %s
            学生画像: %s
            路径标题: %s
            路径描述: %s
            路径节点数: %d

            要求:
            1. 200-400字中文摘要
            2. 说明路径设计的逻辑和学习建议
            3. 推荐1-2个补充学习资源方向
            4. 结合学生画像给出个性化学习建议
            """, subject, profileInfo, path.getTitle(), path.getDescription(), path.getTotalNodes());

        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        SystemMessage.from("你是课程教学专家。"),
                        UserMessage.from(prompt)
                ))
                .parameters(OpenAiChatRequestParameters.builder().modelName(modelName).build())
                .build();

        ChatResponse response = openAiChatModel.doChat(request);
        return response.aiMessage().text();
    }
}
