package cn.iocoder.teach-ai.module.clientChat.service.agent.impl;

import cn.iocoder.teach-ai.module.clientChat.service.agent.*;
import cn.iocoder.teach-ai.module.clientChat.service.learningpath.PlannerAgent;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathRespDTO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 路径规划 Agent 适配器。
 * 调用现有 PlannerAgent 生成学习路径。
 */
@Component
@Order(20)
public class PlannerAgentAdapter implements Agent {

    private static final Logger log = LoggerFactory.getLogger(PlannerAgentAdapter.class);

    @Resource
    private PlannerAgent plannerAgent;

    @Override
    public AgentRole getRole() { return AgentRole.PLANNER; }

    @Override
    public int getOrder() { return 20; }

    @Override
    public AgentContext process(AgentContext ctx, AgentStatusEmitter emitter) {
        String taskId = (String) ctx.getAttributes().get("taskId");

        emitter.emit(taskId, AgentStatus.processing(AgentRole.PLANNER,
                "结合画像设计 " + ctx.getRepoCategoryName() + " 学习路径...", 40));

        LearningPathRespDTO path = plannerAgent.generateAndSave(
                ctx.getUserId(),
                ctx.getRepoCategoryId(),
                ctx.getRepoCategoryName());

        ctx.setLearningPath(path);
        String msg = path != null
                ? "已生成路径「" + path.getTitle() + "」, " + path.getTotalNodes() + " 个节点"
                : "路径生成失败";
        emitter.emit(taskId, AgentStatus.processing(AgentRole.PLANNER, msg, 70));
        ctx.logStatus(path != null
                ? AgentStatus.completed(AgentRole.PLANNER, msg)
                : AgentStatus.error(AgentRole.PLANNER, msg, "返回null"));

        return ctx;
    }
}
