package cn.iocoder.teach-ai.module.clientChat.service.agent;

/**
 * 多智能体角色定义 — 赛题要求 ≥6 个 Agent 角色
 */
public enum AgentRole {
    PROFILER("ProfilerAgent", "画像分析", "分析学生对话，提取学习特征画像"),
    PLANNER("ChatReviewerAgent", "路径规划", "基于画像规划个性化学习路径"),
    CONTENT_GENERATOR("ContentGeneratorAgent", "内容生成", "生成教案、习题、思维导图等教学资源"),
    REVIEWER("ChatReviewerAgent", "质量审核", "审核生成内容的准确性，防幻觉与安全过滤"),
    TUTOR("TutorAgent", "智能辅导", "提供即时答疑、图解说明、短视频讲解等多模态辅导"),
    ASSESSMENT("AssessmentAgent", "学习评估", "多维度评估学习效果，触发路径动态调整反馈");

    private final String id;
    private final String label;
    private final String description;

    AgentRole(String id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }
}
