package cn.iocoder.teach-ai.module.clientChat.service.agent;

import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;

import java.util.*;

/**
 * 多智能体协作的共享上下文，在责任链中传递。
 */
public class AgentContext {

    // ===== 输入参数 =====
    private Long userId;
    private Long repoCategoryId;
    private String repoCategoryName;

    // ===== Agent 产出 =====
    private StudentProfileRespDTO profile;
    private LearningPathRespDTO learningPath;
    private String generatedContent;         // ContentGenerator 产出
    private String reviewedContent;          // Reviewer 修正后内容
    private String tutoringAnswer;           // Tutor 产出
    private Map<String, Object> attributes = new HashMap<>(); // 扩展属性

    // ===== 状态跟踪 =====
    private String taskType;                 // "path_generation" / "content_generation" / "tutoring"
    private List<AgentStatus> statusLog = new ArrayList<>();
    private boolean cancelled;

    public void logStatus(AgentStatus status) {
        this.statusLog.add(status);
    }

    public List<AgentStatus> getStatusLog() { return statusLog; }

    // Getters & Setters (Lombok-free, explicit)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRepoCategoryId() { return repoCategoryId; }
    public void setRepoCategoryId(Long repoCategoryId) { this.repoCategoryId = repoCategoryId; }
    public String getRepoCategoryName() { return repoCategoryName; }
    public void setRepoCategoryName(String repoCategoryName) { this.repoCategoryName = repoCategoryName; }
    public StudentProfileRespDTO getProfile() { return profile; }
    public void setProfile(StudentProfileRespDTO profile) { this.profile = profile; }
    public LearningPathRespDTO getLearningPath() { return learningPath; }
    public void setLearningPath(LearningPathRespDTO learningPath) { this.learningPath = learningPath; }
    public String getGeneratedContent() { return generatedContent; }
    public void setGeneratedContent(String generatedContent) { this.generatedContent = generatedContent; }
    public String getReviewedContent() { return reviewedContent; }
    public void setReviewedContent(String reviewedContent) { this.reviewedContent = reviewedContent; }
    public String getTutoringAnswer() { return tutoringAnswer; }
    public void setTutoringAnswer(String tutoringAnswer) { this.tutoringAnswer = tutoringAnswer; }
    public Map<String, Object> getAttributes() { return attributes; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
