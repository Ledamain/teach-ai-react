package cn.iocoder.teach-ai.module.clientChat.service.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 状态广播器 — 将 Agent 协作状态实时推送到前端。
 * <p>
 * 每个任务（通过 taskId 标识）持有一个 SseEmitter，
 * 前端通过订阅 /agent/status/{taskId} 接收实时进度。
 */
public class AgentStatusEmitter {

    private static final Logger log = LoggerFactory.getLogger(AgentStatusEmitter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** taskId → SseEmitter */
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void register(String taskId, SseEmitter emitter) {
        emitters.put(taskId, emitter);
        emitter.onCompletion(() -> emitters.remove(taskId));
        emitter.onTimeout(() -> emitters.remove(taskId));
        emitter.onError(e -> emitters.remove(taskId));
    }

    public void emit(String taskId, AgentStatus status) {
        SseEmitter emitter = emitters.get(taskId);
        if (emitter == null) return;
        try {
            String json = objectMapper.writeValueAsString(status);
            emitter.send(SseEmitter.event()
                    .name("agent-status")
                    .data(json));
        } catch (IOException e) {
            log.warn("SSE 推送失败: taskId={}, error={}", taskId, e.getMessage());
            emitters.remove(taskId);
        }
    }

    /** 发送完成信号并关闭 */
    public void complete(String taskId) {
        SseEmitter emitter = emitters.get(taskId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name("complete").data("{}"));
        } catch (IOException ignored) {}
        emitter.complete();
        emitters.remove(taskId);
    }

    /** 发送错误信号并关闭 */
    public void error(String taskId, String errorMessage) {
        SseEmitter emitter = emitters.get(taskId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data("{\"error\":\"" + escapeJson(errorMessage) + "\"}"));
        } catch (IOException ignored) {}
        emitter.complete();
        emitters.remove(taskId);
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
