package cn.iocoder.teach-ai.module.clientChat.service.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多智能体编排器 — 责任链模式。
 * <p>
 * 根据任务类型（路径生成 / 内容生成 / 辅导）组装不同的 Agent 链，
 * 按 order 排序后依次执行，各 Agent 通过 AgentStatusEmitter 报告进度。
 */
@Component
public class AgentOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);

    /** 所有已注册的 Agent */
    private final List<Agent> allAgents;

    /** 全局状态广播器 */
    private final AgentStatusEmitter emitter = new AgentStatusEmitter();

    public AgentOrchestrator(List<Agent> allAgents) {
        this.allAgents = allAgents;
        this.allAgents.sort(Comparator.comparingInt(Agent::getOrder));
        log.info("AgentOrchestrator 初始化: 已注册 {} 个 Agent — {}",
                allAgents.size(),
                allAgents.stream().map(a -> a.getRole().getId()).toList());
    }

    public AgentStatusEmitter getEmitter() {
        return emitter;
    }

    /**
     * 执行学习路径生成的完整 Agent 链:
     * ProfilerAgent → ChatReviewerAgent → ContentGeneratorAgent → ChatReviewerAgent
     */
    public AgentContext executePathGeneration(Long userId, Long repoCategoryId, String repoCategoryName) {
        AgentContext ctx = new AgentContext();
        ctx.setUserId(userId);
        ctx.setRepoCategoryId(repoCategoryId);
        ctx.setRepoCategoryName(repoCategoryName);
        ctx.setTaskType("path_generation");

        String taskId = UUID.randomUUID().toString();
        ctx.getAttributes().put("taskId", taskId);

        List<AgentRole> pipeline = List.of(
                AgentRole.PROFILER,
                AgentRole.PLANNER,
                AgentRole.CONTENT_GENERATOR,
                AgentRole.REVIEWER
        );

        return executePipeline(ctx, pipeline, taskId);
    }

    /**
     * 执行智能辅导的 Agent 链:
     * ProfilerAgent → TutorAgent → ChatReviewerAgent
     */
    public AgentContext executeTutoring(Long userId, Long repoCategoryId,
                                         String repoCategoryName, String studentQuestion) {
        AgentContext ctx = new AgentContext();
        ctx.setUserId(userId);
        ctx.setRepoCategoryId(repoCategoryId);
        ctx.setRepoCategoryName(repoCategoryName);
        ctx.setTaskType("tutoring");
        ctx.getAttributes().put("studentQuestion", studentQuestion);

        String taskId = UUID.randomUUID().toString();
        ctx.getAttributes().put("taskId", taskId);

        List<AgentRole> pipeline = List.of(
                AgentRole.PROFILER,
                AgentRole.TUTOR,
                AgentRole.REVIEWER
        );

        return executePipeline(ctx, pipeline, taskId);
    }

    private AgentContext executePipeline(AgentContext ctx, List<AgentRole> pipeline, String taskId) {
        log.info("开始执行 Agent 链: taskId={}, pipeline={}", taskId,
                pipeline.stream().map(AgentRole::getId).toList());

        emitter.emit(taskId, new AgentStatus(null, "pipeline_started",
                "启动多智能体协作 (" + pipeline.size() + " 个Agent)", 0));

        for (AgentRole role : pipeline) {
            if (ctx.isCancelled()) {
                emitter.emit(taskId, AgentStatus.skipped(role, "任务已取消"));
                continue;
            }

            Agent agent = findAgent(role);
            if (agent == null) {
                log.warn("未找到 Agent: {}", role);
                emitter.emit(taskId, AgentStatus.skipped(role, "Agent 未注册"));
                continue;
            }

            try {
                emitter.emit(taskId, AgentStatus.started(role,
                        role.getLabel() + " 开始工作..."));
                ctx = agent.process(ctx, emitter);
                emitter.emit(taskId, AgentStatus.completed(role,
                        role.getLabel() + " 完成"));
            } catch (Exception e) {
                log.error("Agent {} 执行异常: {}", role.getId(), e.getMessage(), e);
                emitter.emit(taskId, AgentStatus.error(role,
                        role.getLabel() + " 异常: " + e.getMessage(),
                        e.getMessage()));
                // 非关键 Agent 失败不中断流程
            }
        }

        emitter.emit(taskId, new AgentStatus(null, "pipeline_completed",
                "所有 Agent 协作完成", 100));
        emitter.complete(taskId);

        log.info("Agent 链执行完成: taskId={}, statusLogSize={}",
                taskId, ctx.getStatusLog().size());
        return ctx;
    }

    private Agent findAgent(AgentRole role) {
        return allAgents.stream()
                .filter(a -> a.getRole() == role)
                .findFirst()
                .orElse(null);
    }
}
