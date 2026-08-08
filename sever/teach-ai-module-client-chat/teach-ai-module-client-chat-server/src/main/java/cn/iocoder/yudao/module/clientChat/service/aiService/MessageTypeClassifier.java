package cn.iocoder.teach-ai.module.clientChat.service.aiService;

import cn.iocoder.teach-ai.module.clientChat.service.aiService.ChatStreamEvent.ClarificationQuestion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息类型分类器。
 * 优先用关键词规则（零延迟），规则兜底时再调 LLM。
 */
@Slf4j
@Service
public class MessageTypeClassifier {

    @Resource
    private ChatModel openAiChatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分类：先用关键词快速判断，命中直接返回；
     * 未命中时用 LLM 精细分类。
     */
    public ChatStreamEvent classify(String fullResponse, String userPrompt) {
        // 第一步：关键词快速判断（零延迟，覆盖 90% 场景）
        ChatStreamEvent keywordResult = classifyWithKeywords(fullResponse);
        if (!"end".equals(keywordResult.getType())) {
            log.info("关键词分类命中: type={}", keywordResult.getType());
            return keywordResult;
        }

        // 第二步：LLM 精细分类（关键词兜不住时）
        try {
            return classifyWithLLM(fullResponse, userPrompt);
        } catch (Exception e) {
            log.warn("LLM 分类失败: {}", e.getMessage());
            return ChatStreamEvent.end();
        }
    }

    private ChatStreamEvent classifyWithLLM(String fullResponse, String userPrompt) {
        String classifyPrompt = buildClassifyPrompt(fullResponse, userPrompt);

        long start = System.currentTimeMillis();
        ChatRequest request = ChatRequest.builder()
                .messages(Collections.singletonList(UserMessage.from(classifyPrompt)))
                .build();
        ChatResponse response = openAiChatModel.doChat(request);
        String jsonResult = response.aiMessage().text();
        log.info("LLM 分类耗时: {}ms, 结果: {}", System.currentTimeMillis() - start, jsonResult);

        return parseClassificationResult(jsonResult, fullResponse);
    }

    private String buildClassifyPrompt(String aiResponse, String userPrompt) {
        return """
            分析以下 AI 回复，判断类型并提取结构化信息。只输出 JSON。

            ## 用户提问:
            %s

            ## AI 回复:
            %s

            ## 类型定义:
            - clarification: AI 在追问用户，要求补充信息（教学目标、课时、年级等）。提取每个追问内容。
            - confirmation: AI 确认已理解需求，列出确认项。
            - normal: 其他（普通解答、闲聊等）。

            ## JSON 格式:
            clarification: {"type":"clarification","questions":[{"id":"q1","text":"问题原文","options":[]}]}
            confirmation: {"type":"confirmation","summary":"摘要","requirements":{"目标":"xxx"}}
            normal: {"type":"normal"}
            """.formatted(userPrompt, truncate(aiResponse, 1500));
    }

    private ChatStreamEvent parseClassificationResult(String json, String fullResponse) {
        try {
            String cleaned = json.trim();
            if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
            if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
            if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
            cleaned = cleaned.trim();

            Map<String, Object> map = objectMapper.readValue(cleaned, new TypeReference<>() {});
            String type = (String) map.get("type");

            if ("clarification".equals(type)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> raw = (List<Map<String, Object>>) map.get("questions");
                List<ClarificationQuestion> questions = new ArrayList<>();
                if (raw != null) {
                    for (int i = 0; i < raw.size(); i++) {
                        Map<String, Object> q = raw.get(i);
                        questions.add(ClarificationQuestion.builder()
                                .id((String) q.getOrDefault("id", "q" + (i + 1)))
                                .text((String) q.get("text"))
                                .options((List<String>) q.getOrDefault("options", Collections.emptyList()))
                                .build());
                    }
                }
                return ChatStreamEvent.clarification(questions);
            } else if ("confirmation".equals(type)) {
                @SuppressWarnings("unchecked")
                Map<String, String> reqs = (Map<String, String>) map.getOrDefault("requirements", Collections.emptyMap());
                return ChatStreamEvent.confirmation(
                        (String) map.getOrDefault("summary", ""), reqs);
            }
            return ChatStreamEvent.end();
        } catch (Exception e) {
            log.warn("解析分类 JSON 失败: {}", e.getMessage());
            return classifyWithKeywords(fullResponse);
        }
    }

    /**
     * 关键词规则（优先使用）。
     */
    private ChatStreamEvent classifyWithKeywords(String resp) {
        // 教学教案检测：如果已生成结构化教案内容，不追问
        if (isLessonPlan(resp)) {
            log.info("检测到教案内容，跳过追问");
            ChatStreamEvent.TeachingIntent intent = extractTeachingIntent(resp);
            log.info("提取教学意图: 知识点{}个, 重点{}个, 难点{}个, 环节{}个",
                    intent.getKnowledgePoints() != null ? intent.getKnowledgePoints().size() : 0,
                    intent.getKeyPoints() != null ? intent.getKeyPoints().size() : 0,
                    intent.getDifficultPoints() != null ? intent.getDifficultPoints().size() : 0,
                    intent.getTeachingSequence() != null ? intent.getTeachingSequence().size() : 0);
            return ChatStreamEvent.teachingIntent(intent);
        }

        // 确认模式
        if (containsAny(resp, "确认以下", "已明确", "您的需求", "为您总结", "整理如下", "梳理如下")
                && containsAny(resp, "教学目标", "知识点", "课时", "年级", "学段", "核心知识")) {
            Map<String, String> reqs = extractRequirements(resp);
            if (!reqs.isEmpty()) {
                return ChatStreamEvent.confirmation("已理解您的教学需求", reqs);
            }
            // 兜底：关键词命中但未提取到结构化字段，仍按确认处理
            if (reqs.isEmpty() && containsAny(resp, "已明确", "为您总结", "确认以下")) {
                Map<String, String> fallbackReqs = new LinkedHashMap<>();
                fallbackReqs.put("摘要", resp.length() > 200 ? resp.substring(0, 200) + "..." : resp);
                return ChatStreamEvent.confirmation("已理解您的教学需求", fallbackReqs);
            }
        }

        // 追问模式：有编号 + 问号
        String noFullWidth = resp.replace("？", "?").replace("！", "!");
        int qmCount = countOccurrences(noFullWidth, "?");
        boolean hasNumbers = Pattern.compile("\\d+[.、)]").matcher(resp).find();

        if (qmCount >= 2 || (qmCount >= 1 && hasNumbers)) {
            List<ClarificationQuestion> questions = extractQuestions(resp);
            if (!questions.isEmpty()) {
                return ChatStreamEvent.clarification(questions);
            }
        }

        return ChatStreamEvent.end();
    }

    private List<ClarificationQuestion> extractQuestions(String text) {
        List<ClarificationQuestion> list = new ArrayList<>();
        // 匹配 "1. xxx？" 或 "1、xxx？" 或 "1）xxx？"
        Pattern p = Pattern.compile("(?:\\d+[.、）)])[^?？]*[?？]");
        Matcher m = p.matcher(text);
        int idx = 0;
        while (m.find()) {
            idx++;
            String qText = m.group().replaceFirst("^\\d+[.、）)]\\s*", "").trim();
            if (qText.length() > 2) {
                list.add(ClarificationQuestion.builder()
                        .id("q" + idx).text(qText).options(Collections.emptyList()).build());
            }
        }
        // 如果没有匹配到编号问题，回退到按行匹配（每个 ?/？ 结尾的行作为一个问题）
        if (list.isEmpty()) {
            String[] lines = text.split("\\n");
            idx = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.endsWith("?") || line.endsWith("？")) {
                    idx++;
                    String qText = line.replaceAll("[?？]$", "").trim();
                    if (qText.length() > 2) {
                        list.add(ClarificationQuestion.builder()
                                .id("q" + idx).text(qText).options(Collections.emptyList()).build());
                    }
                }
            }
        }
        return list;
    }

    private Map<String, String> extractRequirements(String text) {
        Map<String, String> reqs = new LinkedHashMap<>();
        // 1) 带 - 前缀的列表格式：- 学科：高中物理
        Pattern bulletPattern = Pattern.compile("[-•*]\s*([^：:]{1,20})[：:]\s*(.+?)(?=(?:\n[-•*]|$))", Pattern.DOTALL);
        Matcher m = bulletPattern.matcher(text);
        while (m.find()) {
            String key = m.group(1).trim();
            String val = m.group(2).trim().replaceAll("\n", " ");
            if (key.length() < 20 && val.length() < 200) reqs.put(key, val);
        }
        // 2) 无前缀纯文本格式：学科：高中物理（每行一个字段）
        if (reqs.isEmpty()) {
            Pattern plainPattern = Pattern.compile("(?:^|\n)([^：:\n]{1,20})[：:]\s*(.+?)(?=\n[^：:\n]{1,20}[：:]|\n*请确认|\n*$)");
            Matcher pm = plainPattern.matcher(text);
            while (pm.find()) {
                String key = pm.group(1).trim();
                String val = pm.group(2).trim().replaceAll("\n", " ");
                if (key.length() < 20 && val.length() < 200
                        && !key.contains("确认") && !key.contains("以上")) {
                    reqs.put(key, val);
                }
            }
        }
        return reqs;
    }

    /**
     * 检测是否已生成结构化的教案内容（包含教学关键词 + markdown/粗体 标题结构）。
     * 若已生成教案，则不应再追问用户。
     */
    private boolean isLessonPlan(String resp) {
        // 需要有 markdown 标题 或 粗体教案标题
        boolean hasMarkdownTitle = resp.contains("# ") || resp.contains("\n# ");
        // 粗体教案格式：**课题**、**教学目标**、**教学重难点** 等
        String[] boldPlanHeaders = {"**课题**", "**教学目标**", "**教学重难点**", "**教学重点**",
                "**年级**", "**课时**", "**教学过程**", "**教学方法**"};
        int boldHeaderCount = 0;
        for (String h : boldPlanHeaders) {
            if (resp.contains(h)) boldHeaderCount++;
        }
        boolean hasBoldPlanTitle = boldHeaderCount >= 3;
        if (!hasMarkdownTitle && !hasBoldPlanTitle) return false;
        // 需要有一定篇幅
        if (resp.length() < 200) return false;
        // 教学相关关键词（教案结构特征）
        String[] planKeywords = {
            "教学目标", "教学重点", "教学难点", "教学过程", "教学方法",
            "课堂活动", "课后作业", "板书设计", "教学反思", "导入环节",
            "讲授环节", "练习环节", "小结", "课时安排", "适用年级",
            "课程名称", "授课时长", "学情分析", "教学内容"
        };
        int matchCount = 0;
        for (String kw : planKeywords) {
            if (resp.contains(kw)) matchCount++;
        }
        // 命中 3 个以上教学关键词且包含标题结构，判定为教案
        return matchCount >= 3;
    }

    /**
     * 从教案 Markdown 文本中提取结构化教学意图。
     * 先用正则按标题段落拆分并提取关键字段，不够时回退到 LLM。
     */
    private ChatStreamEvent.TeachingIntent extractTeachingIntent(String planText) {
        var builder = ChatStreamEvent.TeachingIntent.builder();

        // --- 正则快速提取 ---
        builder.subject(extractField(planText, "学科|课程", 20));
        builder.grade(extractField(planText, "年级|学段|适用|班级", 20));
        builder.topic(extractField(planText, "课题|主题|标题", 40));
        builder.knowledgePoints(extractList(planText, "知识点|知识目标|知识与技能|教学内容"));
        builder.keyPoints(extractList(planText, "重点|教学重点|关键"));
        builder.difficultPoints(extractList(planText, "难点|教学难点|困难"));
        builder.teachingSequence(extractSteps(planText));

        ChatStreamEvent.TeachingIntent result = builder.build();

        // 如果关键词提取结果太稀疏，用 LLM 补全
        boolean sparse = (result.getKnowledgePoints() == null || result.getKnowledgePoints().size() < 2)
                && (result.getKeyPoints() == null || result.getKeyPoints().size() < 1)
                && (result.getTeachingSequence() == null || result.getTeachingSequence().size() < 2);

        if (sparse) {
            try {
                return extractTeachingIntentWithLLM(planText);
            } catch (Exception e) {
                log.warn("LLM 教学意图提取失败: {}", e.getMessage());
            }
        }
        return result;
    }

    /** 从标题段落中提取单个字段 */
    private String extractField(String text, String keyPattern, int maxLen) {
        Pattern p = Pattern.compile("(?:^|\\n)#{1,3}\\s*[^\\n]*(" + keyPattern + ")[^\\n]*[：:]\\s*(.+?)(?:\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            String val = m.group(2).trim();
            return val.length() > maxLen ? val.substring(0, maxLen) + "..." : val;
        }
        return null;
    }

    /** 提取标题下的列表项 */
    private List<String> extractList(String text, String keyPattern) {
        List<String> items = new ArrayList<>();
        // 找到对应标题的位置
        Pattern headingP = Pattern.compile("(?:^|\\n)#{1,3}\\s*[^\\n]*(" + keyPattern + ")[^\\n]*", Pattern.CASE_INSENSITIVE);
        Matcher headingM = headingP.matcher(text);
        if (!headingM.find()) return items;

        // 查找下一标题或文末，截取区间
        int start = headingM.end();
        Pattern nextHeading = Pattern.compile("^#{1,3}\\s", Pattern.MULTILINE);
        Matcher nh = nextHeading.matcher(text);
        int end = text.length();
        if (nh.find(start)) end = nh.start();

        String section = text.substring(start, end);
        // 提取列表项
        Pattern itemP = Pattern.compile("(?:^|\\n)\\s*(?:\\d+[.、)]|[-•*])\\s*(.+?)(?:\\n|$)");
        Matcher itemM = itemP.matcher(section);
        while (itemM.find()) {
            String item = itemM.group(1).trim();
            if (item.length() > 2 && item.length() < 120) items.add(item);
        }
        return items;
    }

    /** 提取教学环节步骤 */
    private List<ChatStreamEvent.TeachingStep> extractSteps(String text) {
        List<ChatStreamEvent.TeachingStep> steps = new ArrayList<>();
        // 匹配 "一、导入（5分钟）" 或 "1. 导入环节 (5 min)"
        Pattern p = Pattern.compile("(?:^|\\n)(?:[一二三四五六七八九十]+[、.]|\\d+[、.)])\\s*([^（(\\n]+)(?:[（(]([^）)]+)[）)])?");
        Matcher m = p.matcher(text);
        int count = 0;
        while (m.find() && count < 8) {
            String name = m.group(1).trim();
            String duration = m.group(2) != null ? m.group(2).trim() : null;
            // 过滤非教学环节的行
            if (name.length() > 30 || name.contains("http")) continue;
            // 检查下一行的简短描述
            String desc = "";
            int lineEnd = text.indexOf("\\n", m.end());
            if (lineEnd == -1) lineEnd = Math.min(text.length(), m.end() + 80);
            String nextLine = text.substring(m.end(), Math.min(lineEnd + 60, text.length())).trim();
            if (nextLine.length() > 5 && nextLine.length() < 100 && !nextLine.startsWith("#")) {
                desc = nextLine.length() > 80 ? nextLine.substring(0, 80) + "..." : nextLine;
            }
            steps.add(ChatStreamEvent.TeachingStep.builder()
                    .name(name).duration(duration).description(desc).build());
            count++;
        }
        return steps;
    }

    /** LLM 辅助提取教学意图 —— 仅关键词提取不足时使用 */
    private ChatStreamEvent.TeachingIntent extractTeachingIntentWithLLM(String planText) {
        String prompt = """
            分析以下教案文本，提取结构化教学要素。只输出 JSON。
            JSON 格式：{"subject":"学科","grade":"年级","topic":"课题","knowledgePoints":["知识点"],"keyPoints":["重点"],"difficultPoints":["难点"],"teachingSequence":[{"name":"环节","duration":"时长","description":"描述"}]}
            教案：""" + truncate(planText, 2000);

        try {
            ChatRequest request = ChatRequest.builder()
                    .messages(Collections.singletonList(UserMessage.from(prompt)))
                    .build();
            ChatResponse response = openAiChatModel.doChat(request);
            String json = response.aiMessage().text();
            // 清理 markdown 包裹
            json = json.replaceAll("```json|```", "").trim();
            return objectMapper.readValue(json, ChatStreamEvent.TeachingIntent.class);
        } catch (Exception e) {
            log.warn("LLM 教学意图提取异常: {}", e.getMessage());
            return ChatStreamEvent.TeachingIntent.builder().build();
        }
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) if (text.contains(kw)) return true;
        return false;
    }

    private int countOccurrences(String text, String sub) {
        int count = 0, idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) { count++; idx += sub.length(); }
        return count;
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
