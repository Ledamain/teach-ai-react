package cn.iocoder.teach-ai.module.clientChat.service.aiService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import cn.iocoder.teach-ai.module.clientChat.mcp.tool.ToolCallEvent;

/**
 * SSE 聊天流事件数据结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatStreamEvent {

    /** 事件类型: text / clarification / confirmation / end */
    private String type;

    /** 文本内容 (type=text 时使用) */
    private String text;

    /** 追问问题列表 (type=clarification 时使用) */
    private List<ClarificationQuestion> questions;

    /** 已确认的需求项 (type=confirmation 时使用) */
    private Map<String, String> confirmedRequirements;

    /** 确认摘要文本 (type=confirmation 时使用) */
    private String confirmationSummary;

    /** 教学意图结构化提取 (type=teaching_intent 时使用) */
    private TeachingIntent teachingIntent;

    /** RAG 检索到的文本块列表 */
    private List<RagChunk> ragChunks;

    /** MCP 工具调用事件列表 */
    private List<ToolCallEvent> toolCalls;

    /**
     * 追问问题结构
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClarificationQuestion {
        private String id;
        private String text;
        /** 可选的快捷选项 */
        private List<String> options;
    }

    /**
     * 教学意图结构化提取结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeachingIntent {
        /** 学科 */
        private String subject;
        /** 年级/学段 */
        private String grade;
        /** 课题名称 */
        private String topic;
        /** 知识点清单 */
        private List<String> knowledgePoints;
        /** 教学重点 */
        private List<String> keyPoints;
        /** 教学难点 */
        private List<String> difficultPoints;
        /** 教学逻辑顺序/环节 */
        private List<TeachingStep> teachingSequence;
    }

    /**
     * 教学环节步骤
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeachingStep {
        /** 环节名称（如"导入""新授""练习"） */
        private String name;
        /** 时长（如"5分钟"） */
        private String duration;
        /** 环节描述 */
        private String description;
    }

    // --- 工厂方法 ---

    public static ChatStreamEvent text(String chunk) {
        return ChatStreamEvent.builder()
                .type("text")
                .text(chunk)
                .build();
    }

    public static ChatStreamEvent clarification(List<ClarificationQuestion> questions) {
        return ChatStreamEvent.builder()
                .type("clarification")
                .questions(questions)
                .build();
    }

    public static ChatStreamEvent confirmation(String summary, Map<String, String> requirements) {
        return ChatStreamEvent.builder()
                .type("confirmation")
                .confirmationSummary(summary)
                .confirmedRequirements(requirements)
                .build();
    }

    public static ChatStreamEvent end() {
        return ChatStreamEvent.builder()
                .type("end")
                .build();
    }

    public static ChatStreamEvent teachingIntent(TeachingIntent intent) {
        return ChatStreamEvent.builder()
                .type("teaching_intent")
                .teachingIntent(intent)
                .build();
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RagChunk {
        private String fileId;
        private String fileName;
        private String text;
        private Double score;
    }
}
