package cn.iocoder.teach-ai.module.clientChat.service.agent;

import java.time.Instant;

/**
 * 单个 Agent 工作状态，前端用于展示协作过程。
 */
public class AgentStatus {
    private AgentRole role;
    private String phase;           // 当前阶段: "started" / "processing" / "completed" / "error"
    private String message;         // 给前端展示的文字
    private int progress;           // 0-100 进度
    private long timestamp;
    private String detail;          // 可选补充信息
    private String error;           // 出错时的错误信息

    public AgentStatus() {}

    public AgentStatus(AgentRole role, String phase, String message, int progress) {
        this.role = role;
        this.phase = phase;
        this.message = message;
        this.progress = progress;
        this.timestamp = Instant.now().toEpochMilli();
    }

    // Getters & Setters
    public AgentRole getRole() { return role; }
    public void setRole(AgentRole role) { this.role = role; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    // Factory methods
    public static AgentStatus started(AgentRole role, String message) {
        return new AgentStatus(role, "started", message, 0);
    }
    public static AgentStatus processing(AgentRole role, String message, int progress) {
        return new AgentStatus(role, "processing", message, progress);
    }
    public static AgentStatus completed(AgentRole role, String message) {
        return new AgentStatus(role, "completed", message, 100);
    }
    public static AgentStatus error(AgentRole role, String message, String errorDetail) {
        AgentStatus s = new AgentStatus(role, "error", message, 100);
        s.setError(errorDetail);
        return s;
    }
    public static AgentStatus skipped(AgentRole role, String reason) {
        AgentStatus s = new AgentStatus(role, "skipped", reason, 100);
        return s;
    }
}
