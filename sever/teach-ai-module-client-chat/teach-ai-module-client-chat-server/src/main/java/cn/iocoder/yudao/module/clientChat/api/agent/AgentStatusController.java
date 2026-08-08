package cn.iocoder.teach-ai.module.clientChat.api.agent;

import cn.iocoder.teach-ai.module.clientChat.service.agent.AgentOrchestrator;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 端点 — 前端订阅 Agent 协作进度。
 * <p>
 * GET /rpc-api/client-chat/agent/status/{taskId}
 * 返回 SSE 流，事件名 "agent-status"，数据为 AgentStatus JSON。
 */
@RestController
@RequestMapping("/rpc-api/client-chat/agent")
public class AgentStatusController {

    @Resource
    private AgentOrchestrator orchestrator;

    @GetMapping(value = "/status/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String taskId) {
        SseEmitter emitter = new SseEmitter(300_000L); // 5分钟超时
        orchestrator.getEmitter().register(taskId, emitter);

        // 发送初始连接确认
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"taskId\":\"" + taskId + "\"}"));
        } catch (Exception ignored) {}

        return emitter;
    }
}
