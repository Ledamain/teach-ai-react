package cn.iocoder.teach-ai.module.clientChat.service.agent.impl;

import cn.iocoder.teach-ai.module.clientChat.service.agent.*;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.LearningPathApi;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathNodeRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.StudentProfileApi;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习效果评估 Agent（评估→路径反馈闭环）。
 * <p>
 * 流程:
 * 1. 汇总画像 + 学习路径进度 + 行为数据 → LLM 多维度评估
 * 2. 识别薄弱点 → 匹配路径中未完成节点
 * 3. 若匹配到薄弱点对应的节点 → 触发路径动态调整：
 *    - 在该薄弱节点前插入一个"巩固基础"节点
 *    - 在该薄弱节点后插入一个"强化练习"节点
 * 4. 无匹配时仅生产评估报告
 */
@Component
@Order(70)
public class AssessmentAgent implements Agent {

    private static final Logger log = LoggerFactory.getLogger(AssessmentAgent.class);

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Resource
    private ChatModel openAiChatModel;

    @Resource
    private StudentProfileApi studentProfileApi;

    @Resource
    private LearningPathApi learningPathApi;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AgentRole getRole() { return AgentRole.ASSESSMENT; }

    @Override
    public int getOrder() { return 70; }

    @Override
    public AgentContext process(AgentContext ctx, AgentStatusEmitter emitter) {
        String taskId = (String) ctx.getAttributes().get("taskId");
        emitter.emit(taskId, AgentStatus.processing(AgentRole.ASSESSMENT, "正在分析学习数据...", 10));

        try {
            AssessmentResult result = generateAssessment(ctx);
            ctx.getAttributes().put("assessmentResult", result);

            emitter.emit(taskId, AgentStatus.processing(AgentRole.ASSESSMENT,
                    "评估完成: 综合评分 " + result.overallScore, 50));

            // 评估→路径反馈闭环
            PathAdjustment adjustment = applyPathFeedback(ctx, result, emitter, taskId);
            ctx.getAttributes().put("pathAdjustment", adjustment);

            emitter.emit(taskId, AgentStatus.processing(AgentRole.ASSESSMENT,
                    adjustment.summary(), 90));
            ctx.logStatus(AgentStatus.completed(AgentRole.ASSESSMENT,
                    "评估完成: 综合评分 " + result.overallScore + ", " + adjustment.summary()));
        } catch (Exception e) {
            log.warn("AssessmentAgent 异常: {}", e.getMessage());
            emitter.emit(taskId, AgentStatus.error(AgentRole.ASSESSMENT, "评估异常", e.getMessage()));
        }
        return ctx;
    }

    /**
     * 多维度评估：LLM 综合分析画像+路径+行为数据。
     */
    public AssessmentResult generateAssessment(AgentContext ctx) {
        Long userId = ctx.getUserId();
        String repoCategoryName = ctx.getRepoCategoryName();

        StudentProfileRespDTO profile = null;
        try {
            var r = studentProfileApi.getProfileByUserId(userId);
            profile = r != null ? r.getCheckedData() : null;
        } catch (Exception ignored) {}

        LearningPathRespDTO path = null;
        try {
            var r = learningPathApi.getActive(userId, ctx.getRepoCategoryId());
            path = r != null ? r.getCheckedData() : null;
        } catch (Exception ignored) {}

        String prompt = buildAssessmentPrompt(profile, path, repoCategoryName);

        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        SystemMessage.from("你是学习效果评估专家。只输出JSON。"),
                        UserMessage.from(prompt)
                ))
                .parameters(OpenAiChatRequestParameters.builder().modelName(modelName).build())
                .build();

        ChatResponse response = openAiChatModel.doChat(request);
        String json = cleanJson(response.aiMessage().text());
        log.info("评估结果: {}", json);

        return parseAssessmentJson(json);
    }

    /**
     * 评估→路径反馈闭环。
     * <p>
     * 对每个薄弱点在活跃路径中查找匹配的未完成节点。
     * 匹配到则在该节点前后各插入一个补救节点：
     *   前置："巩固基础：{nodeTitle}"（doc 类型，重学基础）
     *   后置："强化练习：{nodeTitle}"（exercise 类型，针对性练习）
     */
    public PathAdjustment applyPathFeedback(AgentContext ctx, AssessmentResult result,
                                              AgentStatusEmitter emitter, String taskId) {
        PathAdjustment adjustment = new PathAdjustment();

        if (result == null || result.weaknesses == null) {
            return adjustment;
        }

        var pathResp = learningPathApi.getActive(ctx.getUserId(), ctx.getRepoCategoryId());
        var path = pathResp != null ? pathResp.getCheckedData() : null;
        if (path == null) return adjustment;

        var nodeResp = learningPathApi.getNodes(path.getId());
        var nodeList = nodeResp != null ? nodeResp.getCheckedData() : null;
        if (nodeList == null || nodeList.isEmpty()) return adjustment;

        try {
            List<String> weaknessList = mapper.readValue(result.weaknesses,
                    new TypeReference<List<String>>() {});
            if (weaknessList == null || weaknessList.isEmpty()) return adjustment;

            // 收集未完成节点
            List<LearningPathNodeRespDTO> pendingNodes = nodeList.stream()
                    .filter(n -> !"completed".equals(n.getStatus()))
                    .collect(Collectors.toList());

            for (String weakness : weaknessList) {
                for (LearningPathNodeRespDTO node : pendingNodes) {
                    if (node.getTitle() != null && containsKeyword(node.getTitle(), weakness)) {
                        // 前置巩固节点
                        adjustment.remediationBefore.add(new RemediationNode(
                                node.getId(), node.getOrderIndex(),
                                "巩固基础：" + node.getTitle(),
                                weakness + " 知识点回顾与基础巩固",
                                "doc"));
                        // 后置强化节点
                        adjustment.remediationAfter.add(new RemediationNode(
                                node.getId(), node.getOrderIndex(),
                                "强化练习：" + node.getTitle(),
                                weakness + " 专项练习与进阶训练",
                                "exercise"));
                        adjustment.matchCount++;
                        break; // 每个薄弱点只匹配一次
                    }
                }
            }

            if (adjustment.matchCount > 0) {
                emitter.emit(taskId, AgentStatus.processing(AgentRole.ASSESSMENT,
                        "发现 " + adjustment.matchCount + " 个薄弱点 → 建议增加 "
                                + (adjustment.matchCount * 2) + " 个补救节点", 70));
                log.info("评估反馈: 路径 {} 匹配 {} 个薄弱点 → 建议增加 {} 个补救节点",
                        path.getId(), adjustment.matchCount, adjustment.matchCount * 2);
            }
        } catch (Exception e) {
            log.warn("补救分析失败（非致命）: {}", e.getMessage());
        }

        return adjustment;
    }

    private boolean containsKeyword(String title, String weakness) {
        if (title == null || weakness == null) return false;
        // 双向子串匹配
        for (int len = Math.min(4, weakness.length()); len >= 2; len--) {
            for (int i = 0; i <= weakness.length() - len; i++) {
                if (title.contains(weakness.substring(i, i + len))) return true;
            }
        }
        // 反向匹配
        for (int len = Math.min(4, title.length()); len >= 2; len--) {
            for (int i = 0; i <= title.length() - len; i++) {
                if (weakness.contains(title.substring(i, i + len))) return true;
            }
        }
        return false;
    }

    private String buildAssessmentPrompt(StudentProfileRespDTO profile,
                                          LearningPathRespDTO path, String subject) {
        String profileInfo = profile != null
                ? "知识水平: " + profile.getKnowledgeLevel()
                + ", 学习风格: " + profile.getLearningStyle()
                + ", 薄弱点: " + profile.getWeakPointDetail()
                : "未建立画像";

        String pathInfo = path != null
                ? "路径「" + path.getTitle() + "」: " + path.getCompletedNodes() + "/" + path.getTotalNodes() + " 完成"
                : "未生成学习路径";

        return String.format("""
            请评估学生关于「%s」课程的学习效果。

            学生画像: %s
            学习进度: %s

            评估维度 (每项0-100分):
            - knowledge: 知识掌握程度
            - engagement: 学习投入度
            - weaknessProgress: 薄弱点改进进展
            - exerciseAccuracy: 练习正确率
            - resourceUsage: 资源利用率
            - progressRate: 学习进度完成率

            输出JSON:
            {
              "overallScore": 85,
              "dimensions": {"knowledge":85,"engagement":70,"weaknessProgress":60,"exerciseAccuracy":80,"resourceUsage":65,"progressRate":75},
              "strengths": ["...", "..."],
              "weaknesses": ["...", "..."],
              "suggestions": ["...", "...", "..."],
              "summary": "..."
            }

            原则:
            1. 基于已有数据合理推断，无法判断的维度给中间值60
            2. weaknesses 使用简短关键词短语（如"递归理解"、"二次函数应用"），方便系统匹配路径节点
            3. suggestions 要具体可执行，结合学生薄弱点
            """, subject, profileInfo, pathInfo);
    }

    private AssessmentResult parseAssessmentJson(String json) {
        try {
            Map<String, Object> map = mapper.readValue(json, new TypeReference<>() {});
            AssessmentResult r = new AssessmentResult();
            r.overallScore = intVal(map, "overallScore");
            r.dimensions = mapper.writeValueAsString(map.get("dimensions"));
            r.strengths = mapper.writeValueAsString(map.get("strengths"));
            r.weaknesses = mapper.writeValueAsString(map.get("weaknesses"));
            r.suggestions = mapper.writeValueAsString(map.get("suggestions"));
            r.summary = strVal(map, "summary");
            return r;
        } catch (Exception e) {
            log.warn("解析评估JSON失败: {}", json, e);
            AssessmentResult fb = new AssessmentResult();
            fb.overallScore = 60;
            fb.dimensions = "{}";
            fb.strengths = "[]";
            fb.weaknesses = "[]";
            fb.suggestions = "[]";
            fb.summary = "评估数据不足";
            return fb;
        }
    }

    private int intVal(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v instanceof Number) return ((Number) v).intValue();
        return 60;
    }

    private String strVal(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? v.toString() : "";
    }

    private String cleanJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        if (s.startsWith("```")) s = s.substring(3);
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    // ===== 内部类 =====

    public static class AssessmentResult {
        public int overallScore;
        public String dimensions;
        public String strengths;
        public String weaknesses;
        public String suggestions;
        public String summary;
    }

    /** 补救节点 */
    public static class RemediationNode {
        public Long targetNodeId;
        public Integer targetOrder;
        public String title;
        public String description;
        public String resourceType;

        public RemediationNode() {}
        public RemediationNode(Long targetNodeId, Integer targetOrder, String title,
                               String description, String resourceType) {
            this.targetNodeId = targetNodeId;
            this.targetOrder = targetOrder;
            this.title = title;
            this.description = description;
            this.resourceType = resourceType;
        }
    }

    /** 路径调整结果 */
    public static class PathAdjustment {
        public int matchCount;
        public List<RemediationNode> remediationBefore = new ArrayList<>();
        public List<RemediationNode> remediationAfter = new ArrayList<>();

        public String summary() {
            if (matchCount == 0) return "无薄弱点命中，路径无需调整";
            return "匹配 " + matchCount + " 个薄弱点，建议增加 "
                    + (remediationBefore.size() + remediationAfter.size())
                    + " 个补救节点（" + remediationBefore.size() + " 个巩固 + "
                    + remediationAfter.size() + " 个强化）";
        }
    }
}
