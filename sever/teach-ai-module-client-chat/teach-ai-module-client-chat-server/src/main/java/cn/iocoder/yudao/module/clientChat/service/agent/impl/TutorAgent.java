package cn.iocoder.teach-ai.module.clientChat.service.agent.impl;

import cn.iocoder.teach-ai.module.clientChat.service.agent.*;
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
 * 智能辅导 Agent。
 * 结合学生画像和课程上下文，提供个性化答疑，
 * 支持文字解答、概念图解说明、学习建议等多模态输出。
 */
@Component
@Order(50)
public class TutorAgent implements Agent {

    private static final Logger log = LoggerFactory.getLogger(TutorAgent.class);

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Resource
    private ChatModel openAiChatModel;

    @Override
    public AgentRole getRole() { return AgentRole.TUTOR; }

    @Override
    public int getOrder() { return 50; }

    @Override
    public AgentContext process(AgentContext ctx, AgentStatusEmitter emitter) {
        String taskId = (String) ctx.getAttributes().get("taskId");
        String question = (String) ctx.getAttributes().get("studentQuestion");

        if (question == null || question.isEmpty()) {
            emitter.emit(taskId, AgentStatus.skipped(AgentRole.TUTOR,
                    "非辅导任务，跳过"));
            ctx.logStatus(AgentStatus.skipped(AgentRole.TUTOR, "非辅导模式"));
            return ctx;
        }

        emitter.emit(taskId, AgentStatus.processing(AgentRole.TUTOR,
                "分析学生问题，准备辅导方案...", 30));

        try {
            String answer = generateTutoringAnswer(ctx.getProfile(), question,
                    ctx.getRepoCategoryName());
            ctx.setTutoringAnswer(answer);

            emitter.emit(taskId, AgentStatus.processing(AgentRole.TUTOR,
                    "辅导内容已生成（" + (answer != null ? answer.length() : 0) + " 字符）", 80));
            ctx.logStatus(AgentStatus.completed(AgentRole.TUTOR,
                    "已生成个性化辅导回答"));
        } catch (Exception e) {
            log.warn("TutorAgent 异常: {}", e.getMessage());
            emitter.emit(taskId, AgentStatus.error(AgentRole.TUTOR,
                    "辅导生成异常", e.getMessage()));
        }

        return ctx;
    }

    private String generateTutoringAnswer(StudentProfileRespDTO profile,
                                           String question, String subject) {
        String profileInfo = "未知";
        if (profile != null) {
            profileInfo = String.format(
                    "知识水平: %s, 学习风格: %s, 薄弱点: %s, 兴趣: %s",
                    profile.getKnowledgeLevel(), profile.getLearningStyle(),
                    profile.getWeakPointDetail(), profile.getInterestSummary()
            );
        }

        String prompt = """
            作为AI辅导教师，请回答学生关于「%s」课程的问题。

            学生画像: %s
            学生问题: %s

            回答要求:
            1. 用学生能理解的语言解释，结合其知识水平调整深度
            2. 如涉及概念，给出通俗比喻或类比帮助理解
            3. 指出常见易错点（如果学生画像有薄弱点信息则重点强调）
            4. 提供1-2道自测题或思考题巩固理解
            5. 推荐相关拓展学习方向
            6. 若适合，用文字描述可制作的图解/示意图结构
            """.formatted(subject, profileInfo, question);

        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        SystemMessage.from("你是经验丰富的AI辅导教师。"),
                        UserMessage.from(prompt)
                ))
                .parameters(OpenAiChatRequestParameters.builder().modelName(modelName).build())
                .build();

        ChatResponse response = openAiChatModel.doChat(request);
        return response.aiMessage().text();
    }
}
