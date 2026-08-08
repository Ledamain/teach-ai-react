package cn.iocoder.teach-ai.module.clientChat.service.agent;

/**
 * 智能体统一接口 — 责任链模式
 */
public interface Agent {
    /** 返回该 Agent 的角色 */
    AgentRole getRole();

    /** 返回该 Agent 在链中的执行顺序（越小越先执行） */
    int getOrder();

    /** 执行 Agent 逻辑，处理 context 并返回（可能修改 context） */
    AgentContext process(AgentContext context, AgentStatusEmitter emitter);
}
