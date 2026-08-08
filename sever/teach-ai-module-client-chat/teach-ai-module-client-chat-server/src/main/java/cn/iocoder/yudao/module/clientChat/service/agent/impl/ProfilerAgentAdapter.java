package cn.iocoder.teach-ai.module.clientChat.service.agent.impl;

import cn.iocoder.teach-ai.module.clientChat.service.agent.*;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.LearningPathApi;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.StudentProfileApi;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 画像分析 Agent 适配器。
 * 从 context 中获取 userId，通过 Feign 调用 client-system 获取学生画像。
 */
@Component
@Order(10)
public class ProfilerAgentAdapter implements Agent {

    private static final Logger log = LoggerFactory.getLogger(ProfilerAgentAdapter.class);

    @Resource
    private StudentProfileApi studentProfileApi;

    @Override
    public AgentRole getRole() { return AgentRole.PROFILER; }

    @Override
    public int getOrder() { return 10; }

    @Override
    public AgentContext process(AgentContext ctx, AgentStatusEmitter emitter) {
        String taskId = (String) ctx.getAttributes().get("taskId");

        emitter.emit(taskId, AgentStatus.processing(AgentRole.PROFILER, "查询学生画像...", 30));
        try {
            var r = studentProfileApi.getProfileByUserId(ctx.getUserId());
            StudentProfileRespDTO profile = r != null ? r.getCheckedData() : null;
            ctx.setProfile(profile);

            String summary = profile != null
                    ? "知识水平: " + profile.getKnowledgeLevel() + ", 风格: " + profile.getLearningStyle()
                    : "画像未建立，使用默认策略";

            emitter.emit(taskId, AgentStatus.processing(AgentRole.PROFILER, summary, 60));
            ctx.logStatus(AgentStatus.completed(AgentRole.PROFILER, summary));
        } catch (Exception e) {
            log.warn("ProfilerAgent 获取画像失败: {}", e.getMessage());
            emitter.emit(taskId, AgentStatus.processing(AgentRole.PROFILER,
                    "画像暂不可用，使用通用策略", 60));
        }

        return ctx;
    }
}
