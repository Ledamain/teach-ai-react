package cn.iocoder.teach-ai.module.clientSystem.controller.client.utils;

import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatMessageVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ChatHistoryConverter {


    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 定义系统提示词的特征前缀，用于从 User 消息中清洗掉
    // 根据你的日志，特征串是 "Answer using the following information:"
    private static final String SYSTEM_INSTRUCTION_PREFIX = "Answer using the following information:";

    // 也可以加一个正则，匹配从该前缀开始到末尾的所有内容
    private static final Pattern SYSTEM_INSTRUCTION_PATTERN = Pattern.compile("\\s*Answer using the following information:[\\s\\S]*$");

    /**
     * 将后端的 messagesJson 字符串转换为前端可用的 VO 列表
     * 核心优化：
     * 1. 彻底过滤 SYSTEM 类型消息
     * 2. 清洗 USER 消息中混入的系统指令
     */
    public static List<ChatMessageVO> convertToFrontendMessages(String messagesJson) {
        if (!StringUtils.hasText(messagesJson)) {
            return new ArrayList<>();
        }

        List<ChatMessageVO> result = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(messagesJson);

            if (!rootNode.isArray()) {
                log.warn("messagesJson 不是数组格式");
                return result;
            }

            int index = 0;
            for (JsonNode msgNode : rootNode) {
                String type = msgNode.has("type") ? msgNode.get("type").asText() : "";

                // 【关键步骤 1】彻底跳过 SYSTEM 类型的消息
                // 这样就不会把“你的名字叫做牛牛...”这种消息发给前端
                if ("SYSTEM".equalsIgnoreCase(type)) {
                    continue;
                }

                // 提取原始内容
                String rawContent = extractContent(msgNode);

                if (!StringUtils.hasText(rawContent)) {
                    // 如果内容为空，可以选择跳过，或者保留空消息
                    // 这里选择跳过空消息，保持界面整洁
                    continue;
                }

                // 【关键步骤 2】清洗 User 消息
                // 有时候 System Prompt 会被错误地拼接到 User 消息后面，需要切除
                String cleanContent = rawContent;
                if ("USER".equalsIgnoreCase(type)) {
                    cleanContent = cleanUserMessage(rawContent);
                }

                // 如果清洗后内容为空（说明整条都是系统指令），则跳过
                if (!StringUtils.hasText(cleanContent.trim())) {
                    continue;
                }

                // 映射角色
                String role = "assistant";
                if ("USER".equalsIgnoreCase(type)) {
                    role = "user";
                } else if ("AI".equalsIgnoreCase(type)) {
                    role = "assistant";
                } else {
                    // 未知类型默认跳过，防止脏数据
                    continue;
                }

                // 构建 VO
                ChatMessageVO vo = new ChatMessageVO();
                vo.setId("msg-" + index + "-" + UUID.randomUUID().toString().substring(0, 8));
                vo.setRole(role);
                vo.setContent(cleanContent);
                vo.setThinking(false);

                result.add(vo);
                index++;
            }

        } catch (Exception e) {
            log.error("解析 messagesJson 失败", e);
            return new ArrayList<>();
        }

        return result;
    }

    /**
     * 兼容提取文本内容
     */
    private static String extractContent(JsonNode msgNode) {
        if (msgNode.has("contents") && msgNode.get("contents").isArray()) {
            JsonNode contentsArray = msgNode.get("contents");
            if (contentsArray.size() > 0) {
                JsonNode firstContent = contentsArray.get(0);
                if (firstContent.has("text")) {
                    return firstContent.get("text").asText();
                }
            }
        }
        if (msgNode.has("text")) {
            return msgNode.get("text").asText();
        }
        return null;
    }

    /**
     * 【核心清洗逻辑】
     * 移除 User 消息中混入的系统提示词
     * 例如：移除 "\n\nAnswer using the following information: ..." 及其之后的所有内容
     */
    private static String cleanUserMessage(String content) {
        if (content == null) return null;

        // 方法 A: 使用正则替换（推荐，更健壮）
        // 匹配从 "Answer using..." 开始直到字符串末尾的内容，并替换为空
        Matcher matcher = SYSTEM_INSTRUCTION_PATTERN.matcher(content);
        if (matcher.find()) {
            String cleaned = matcher.replaceAll("");
            return cleaned.trim();
        }

        // 方法 B: 简单的字符串截断（作为备用）
        int index = content.indexOf(SYSTEM_INSTRUCTION_PREFIX);
        if (index != -1) {
            return content.substring(0, index).trim();
        }

        return content;
    }

}
