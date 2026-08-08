package cn.iocoder.teach-ai.module.clientChat.service.profile;

import cn.iocoder.teach-ai.module.clientSystem.api.profile.StudentProfileApi;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileSaveReqDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 学生画像提取 Agent。
 */
@Slf4j
@Service
public class ProfileAgent {

    @Value("${langchain4j.open-ai.streaming-chat-model.model-name}")
    private String modelName;

    @Resource
    private ChatModel openAiChatModel;

    @Resource
    private StudentProfileApi studentProfileApi;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void extractAndSave(Long userId, String memoryId, String userPrompt, String aiResponse) {
        try {
            StudentProfileSaveReqDTO profile = extractProfile(userPrompt, aiResponse);
            profile.setUserId(userId);
            profile.setMemoryId(memoryId);

            String changeSummary = generateChangeSummary(userPrompt, aiResponse, profile);
            profile.setChangeSummary(changeSummary);

            studentProfileApi.upsertProfile(profile);
            log.info("画像提取完成: userId={}, memoryId={}", userId, memoryId);
        } catch (Exception e) {
            log.error("画像提取失败: userId={}, memoryId={}, error={}", userId, memoryId, e.getMessage());
        }
    }

    private ChatRequest buildRequest(List<dev.langchain4j.data.message.ChatMessage> messages) {
        return ChatRequest.builder()
                .messages(messages)
                .parameters(OpenAiChatRequestParameters.builder()
                        .modelName(modelName)
                        .build())
                .build();
    }

    private StudentProfileSaveReqDTO extractProfile(String userPrompt, String aiResponse) {
        String extractPrompt = buildExtractPrompt(userPrompt, aiResponse);

        ChatRequest request = buildRequest(List.of(
                SystemMessage.from("你是一名教育心理学专家，擅长从学生的对话中分析其学习特征。只输出JSON。"),
                UserMessage.from(extractPrompt)
        ));

        ChatResponse response = openAiChatModel.doChat(request);
        String json = cleanJson(response.aiMessage().text());
        log.info("画像提取原始JSON: {}", json);

        return parseProfileJson(json);
    }

    private String buildExtractPrompt(String userPrompt, String aiResponse) {
        return """
            请分析以下学生与AI教师的对话，提取学生的学习特征画像。输出格式为严格的JSON，字段只使用英文key。

            学生提问:
            %s

            AI回复摘要:
            %s

            JSON格式要求 (所有字段可选，无法判断时填null，不填默认值):
            {
              "knowledgeLevel": "知识水平评级: novice/intermediate/advanced/expert",
              "knowledgeSummary": "知识基础描述，如'已掌握Python基础语法'",
              "masteredTags": ["已掌握知识点标签1", "标签2"],
              "cognitiveStyle": "认知风格: field_dependent/field_independent/mixed",
              "cognitiveStyleDesc": "认知风格描述",
              "learningStyle": "学习风格: visual/auditory/kinesthetic/reading_writing/mixed",
              "learningStyleDesc": "学习风格描述",
              "errorPreferenceSummary": "易错偏好概述",
              "errorTags": ["易错类型1", "易错类型2"],
              "attentionLevel": "注意力水平: high/medium/low",
              "bestStudyTime": "最佳学习时段如'上午9-11点'",
              "attentionSpanMinutes": "单次专注时长(分钟,整数)",
              "learningPace": "学习节奏: fast/medium/slow",
              "weeklyStudyMinutes": "周均学习时长(分钟,整数)",
              "preferredSessionMinutes": "偏好每次学习时长(分钟,整数)",
              "interestTags": ["兴趣标签1", "兴趣标签2"],
              "interestSummary": "兴趣方向描述",
              "weakPointTags": ["薄弱知识点1", "薄弱点2"],
              "weakPointDetail": "薄弱点详细描述"
            }

            分析要求:
            1. 从学生的提问内容和问题深度推断知识水平、认知风格
            2. 从提问频率、问题复杂度推断学习节奏、注意力特征
            3. 从问题领域的分布推断兴趣方向和薄弱点
            4. 只输出有依据的内容，无法判断的字段设为null
            5. knowledgeLevel 必须从提供的4个选项中选择一个
            """.formatted(
                truncate(userPrompt, 2000),
                truncate(aiResponse, 1500)
        );
    }

    private String generateChangeSummary(String userPrompt, String aiResponse,
                                          StudentProfileSaveReqDTO profile) {
        String summaryPrompt = """
            基于以下信息，用一两句话概括这次对话反映了学生什么样的学习特征变化。
            
            学生提问: %s
            AI回复主题: %s
            本次提取的画像特征:
            - 知识水平: %s
            - 学习风格: %s
            - 兴趣标签: %s
            - 薄弱点: %s
            
            直接输出中文摘要，不要JSON。
            """.formatted(
                truncate(userPrompt, 500),
                truncate(aiResponse, 300),
                profile.getKnowledgeLevel(),
                profile.getLearningStyle(),
                profile.getInterestTags(),
                profile.getWeakPointTags()
        );

        try {
            ChatRequest request = buildRequest(Collections.singletonList(UserMessage.from(summaryPrompt)));
            ChatResponse response = openAiChatModel.doChat(request);
            return response.aiMessage().text().trim();
        } catch (Exception e) {
            log.warn("生成变化摘要失败: {}", e.getMessage());
            return "从对话中更新了学习特征";
        }
    }

    private StudentProfileSaveReqDTO parseProfileJson(String json) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
            StudentProfileSaveReqDTO dto = new StudentProfileSaveReqDTO();

            dto.setKnowledgeLevel(getString(map, "knowledgeLevel"));
            dto.setKnowledgeSummary(getString(map, "knowledgeSummary"));
            dto.setMasteredTags(toJson(getList(map, "masteredTags")));
            dto.setCognitiveStyle(getString(map, "cognitiveStyle"));
            dto.setCognitiveStyleDesc(getString(map, "cognitiveStyleDesc"));
            dto.setLearningStyle(getString(map, "learningStyle"));
            dto.setLearningStyleDesc(getString(map, "learningStyleDesc"));
            dto.setErrorPreferenceSummary(getString(map, "errorPreferenceSummary"));
            dto.setErrorTags(toJson(getList(map, "errorTags")));
            dto.setAttentionLevel(getString(map, "attentionLevel"));
            dto.setBestStudyTime(getString(map, "bestStudyTime"));
            dto.setAttentionSpanMinutes(getInt(map, "attentionSpanMinutes"));
            dto.setLearningPace(getString(map, "learningPace"));
            dto.setWeeklyStudyMinutes(getInt(map, "weeklyStudyMinutes"));
            dto.setPreferredSessionMinutes(getInt(map, "preferredSessionMinutes"));
            dto.setInterestTags(toJson(getList(map, "interestTags")));
            dto.setInterestSummary(getString(map, "interestSummary"));
            dto.setWeakPointTags(toJson(getList(map, "weakPointTags")));
            dto.setWeakPointDetail(getString(map, "weakPointDetail"));

            return dto;
        } catch (Exception e) {
            log.warn("解析画像JSON失败: {}", json, e);
            return new StudentProfileSaveReqDTO();
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) {
            try { return Integer.parseInt((String) v); } catch (Exception ignored) {}
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getList(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof List) return (List<String>) v;
        if (v instanceof String && ((String) v).startsWith("[")) {
            try { return objectMapper.readValue((String) v, new TypeReference<>() {}); } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try { return objectMapper.writeValueAsString(list); } catch (Exception e) { return null; }
    }

    private String cleanJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        if (s.startsWith("```")) s = s.substring(3);
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
