package cn.iocoder.teach-ai.module.clientChat.service.learningpath;

import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.LearningPathApi;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathGenerateReqDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 学习路径规划 Agent（DAG 增强版）。
 * <p>
 * LLM 生成 DAG 结构的个性化学习路径，dependsOnOrder 的实体解析由 LearningPathApiImpl 完成。
 */
@Slf4j
@Service
public class PlannerAgent {

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Resource
    private ChatModel openAiChatModel;

    @Resource
    private StudentProfileApi studentProfileApi;

    @Resource
    private LearningPathApi learningPathApi;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成并保存个性化学习路径（DAG 结构，dependsOnOrder 由 ApiImpl 解析为实体 ID）。
     */
    public LearningPathRespDTO generateAndSave(Long userId, Long repoCategoryId, String repoCategoryName) {
        StudentProfileRespDTO profile = null;
        try {
            var r = studentProfileApi.getProfileByUserId(userId);
            profile = r != null ? r.getCheckedData() : null;
        } catch (Exception e) {
            log.warn("获取画像失败: {}", e.getMessage());
        }

        String prompt = buildPathPrompt(profile, repoCategoryName);
        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        SystemMessage.from("你是课程设计专家，为学生设计个性化学习路径。只输出 JSON。"),
                        UserMessage.from(prompt)
                ))
                .parameters(OpenAiChatRequestParameters.builder().modelName(modelName).build())
                .build();

        ChatResponse response = openAiChatModel.doChat(request);
        String json = cleanJson(response.aiMessage().text());
        log.info("路径生成JSON: {}", json);

        PathResult pr = parsePathJson(json);

        List<LearningPathGenerateReqDTO.PathNodeDTO> nodeDTOs = new ArrayList<>();
        if (pr.nodes != null) {
            for (int i = 0; i < pr.nodes.size(); i++) {
                PathNodeResult nr = pr.nodes.get(i);
                nodeDTOs.add(LearningPathGenerateReqDTO.PathNodeDTO.builder()
                        .orderIndex(i + 1)
                        .title(nr.title)
                        .description(nr.description)
                        .resourceType(nr.resourceType)
                        .estimatedMinutes(nr.estimatedMinutes)
                        .dependsOnOrder(nr.dependsOnOrder)
                        .build());
            }
        }

        LearningPathGenerateReqDTO req = LearningPathGenerateReqDTO.builder()
                .userId(userId)
                .repoCategoryId(repoCategoryId)
                .repoCategoryName(repoCategoryName)
                .title(pr.title)
                .description(pr.description)
                .nodes(nodeDTOs)
                .build();

        var result = learningPathApi.generate(req);
        LearningPathRespDTO saved = result != null ? result.getCheckedData() : null;

        log.info("学习路径已保存: userId={}, category={}, pathId={}",
                userId, repoCategoryName, saved != null ? saved.getId() : "null");
        return saved;
    }

    private String buildPathPrompt(StudentProfileRespDTO profile, String subject) {
        String profileInfo = "未知";
        if (profile != null) {
            profileInfo = String.format(
                    "知识水平: %s, 认知风格: %s, 学习风格: %s, 薄弱点: %s, 兴趣: %s",
                    profile.getKnowledgeLevel(), profile.getCognitiveStyle(),
                    profile.getLearningStyle(), profile.getWeakPointDetail(),
                    profile.getInterestSummary()
            );
        }
        return """
            为以下学生设计「%s」课程的个性化学习路径（DAG 结构）。

            学生画像: %s

            要求:
            1. 输出严格 JSON，不含 Markdown 包裹
            2. 路径包含 5-8 个学习节点，构成有向无环图
            3. 每个节点字段: title, description, resourceType(doc/video/exercise/reading/ppt), estimatedMinutes(整数), dependsOnOrder(整数或null, 指依赖的前置节点序号从1开始)
            4. 节点拓扑:
               - 根节点（基础概念）: dependsOnOrder=null
               - 中间节点（深化理解）: dependsOnOrder 指向前置基础节点
               - 综合应用节点（项目/实验/检测）: dependsOnOrder 指向主要前置节点
               - 薄弱点强化节点: dependsOnOrder 指向对应基础节点
            5. 至少有2个节点设置了 dependsOnOrder，使DAG体现真实前置依赖关系
            6. 结合学生薄弱点和学习风格推荐资源类型

            JSON示例:
            {"title":"...","description":"...","nodes":[
              {"title":"...","description":"...","resourceType":"doc","estimatedMinutes":30,"dependsOnOrder":null},
              {"title":"...","description":"...","resourceType":"exercise","estimatedMinutes":25,"dependsOnOrder":1}
            ]}
            """.formatted(subject, profileInfo);
    }

    private PathResult parsePathJson(String json) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
            PathResult r = new PathResult();
            r.title = str(map, "title");
            r.description = str(map, "description");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> raw = (List<Map<String, Object>>) map.get("nodes");
            if (raw != null) {
                r.nodes = new ArrayList<>();
                for (var n : raw) {
                    PathNodeResult nr = new PathNodeResult();
                    nr.title = str(n, "title");
                    nr.description = str(n, "description");
                    nr.resourceType = str(n, "resourceType");
                    nr.estimatedMinutes = integer(n, "estimatedMinutes");
                    nr.dependsOnOrder = integer(n, "dependsOnOrder");
                    r.nodes.add(nr);
                }
            }
            return r;
        } catch (Exception e) {
            log.warn("解析路径JSON失败: {}", json, e);
            PathResult fb = new PathResult();
            fb.title = "个性化学习路径";
            fb.description = "自动生成";
            fb.nodes = List.of();
            return fb;
        }
    }

    private String str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? v.toString() : null;
    }

    private Integer integer(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v instanceof Number) return ((Number) v).intValue();
        return null;
    }

    private String cleanJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        if (s.startsWith("```")) s = s.substring(3);
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    private static class PathResult {
        String title, description;
        List<PathNodeResult> nodes;
    }

    private static class PathNodeResult {
        String title, description, resourceType;
        Integer estimatedMinutes;
        Integer dependsOnOrder;
    }
}
