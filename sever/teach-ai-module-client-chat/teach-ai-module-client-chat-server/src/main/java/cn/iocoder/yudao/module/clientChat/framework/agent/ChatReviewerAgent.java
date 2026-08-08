package cn.iocoder.teach-ai.module.clientChat.framework.agent;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.spi.prompt.PromptTemplateFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 质量门禁节点。coder 每次产出后必经此节点。
 * 检查: 事实准确性、信息完整度、语气一致性、内容安全。
 * 不通过时退回 coder 重答，最多 1 次。
 */
@Service
@Slf4j
public class ChatReviewerAgent implements NodeAction<State> {

    private static final int MAX_RETRIES = 1;

    @Resource
    @Qualifier("openAiChatModel")
    private ChatModel chatModel;

    @Override
    public Map<String, Object> apply(State state) {
        var lastMsg = (AiMessage) state.lastMessage().orElseThrow();
        var answer = lastMsg.text();

        // 1. 内容安全兜底
        if (answer == null || answer.isBlank()) {
            return fail(state, "回答为空，请重新生成");
        }

        // 2. 追问检测: coder 在向用户提问而非给出最终答案 → 直接放行
        if (isClarification(answer)) {
            log.debug("reviewer: clarification detected, auto-pass");
            return Map.of("reviewRetryCount", 0, "reviewFeedback", "", "isClarification", true);
        }

        // 2.5. MCP 工具调用检测 — 双保险：state flag 优先，内容特征兜底
        // 注意：不设 isClarification=true，走正常路径 reviewer → supervisor → FINISH
        boolean toolCalled = hasMcpToolEvidence(answer);
        if (toolCalled) {
            if (containsIllegalContent(answer)) {
                return fail(state, "回答包含违规内容，请重新生成");
            }
            log.info("reviewer: MCP tool call detected, auto-pass");
            return Map.of("reviewRetryCount", 0, "reviewFeedback", "");
        }

        // 3. LLM 审查
        var feedback = review(state, answer);
        if (feedback == null) {
            log.debug("reviewer: pass");
            // 显式清除 reviewFeedback，防止 state 泄漏导致 reviewer→coder 死循环
            return Map.of("reviewRetryCount", 0, "reviewFeedback", "");
        }

        // 3. 重试上限
        var retries = state.reviewRetryCount();
        if (retries >= MAX_RETRIES) {
            log.warn("reviewer: 已达最大重试次数 {}, 强制通过", MAX_RETRIES);
            return Map.of("reviewRetryCount", 0);
        }

        log.info("reviewer: fail (第 {} 次), 反馈: {}", retries + 1, feedback);
        return Map.of(
                "reviewRetryCount", retries + 1,
                "reviewFeedback", feedback
        );
    }

    /**
     * 核心审查。返回 null 表示通过，否则返回修正意见。
     */
    private String review(State state, String answer) {
        var question = extractQuestion(state);
        if (question == null) return null;

        var prompt = """
            审查以下回答。判断标准:
            1. 是否直接回答了用户问题
            2. 内容是否自相矛盾或重复
            3. 是否有明显事实错误
            如果通过，只回复 "PASS"。
            如果不通过，用一句中文说明问题所在。
            
            用户问题: %s
            回答: %s
            """.formatted(question, answer);

        var text = chatModel.chat(prompt).trim();

        if ("PASS".equalsIgnoreCase(text) || text.startsWith("PASS")) {
            return null;
        }
        return text;
    }

    private String extractQuestion(State state) {
        return state.messages().stream()
                .filter(m -> m.type() == ChatMessageType.USER)
                .map(m -> ((UserMessage) m).singleText())
                .reduce((first, second) -> second)
                .orElse(null);
    }

    private Map<String, Object> fail(State state, String feedback) {

        var retries = state.reviewRetryCount();
        if (retries >= MAX_RETRIES) {
            log.warn("reviewer: 已达最大重试, 强制通过");
            return Map.of("reviewRetryCount", 0);
        }
        return Map.of(
                "reviewRetryCount", retries + 1,
                "reviewFeedback", feedback
        );
    }

    /** 检测回答是否为追问/澄清，而非最终答案 */
    private static boolean isClarification(String answer) {
        if (answer == null || answer.isBlank()) return false;
        // 问号占比 > 20% 的行数
        long lines = answer.lines().count();
        long questionLines = answer.lines()
                .filter(l -> l.contains("?") || l.contains("？"))
                .count();
        if (lines > 0 && (double) questionLines / lines > 0.2) return true;
        // 包含追问关键词
        String lower = answer.toLowerCase();
        return lower.contains("请告诉") || lower.contains("需要了解")
                || lower.contains("请提供") || lower.contains("请问")
                || lower.contains("补充以下") || lower.contains("以下信息");
    }

    /** 检测回答是否来自 MCP 工具调用结果。仅匹配高特异性工具特征词 */
    private static boolean hasMcpToolEvidence(String answer) {
        if (answer == null) return false;
        return answer.contains("📸") || answer.contains("📷")
                || answer.contains("拍照成功")
                || answer.contains("小度智能屏") || answer.contains("小度设备")
                || answer.contains("在线设备") || answer.contains("推送通知")
                || answer.contains("视频录制") || answer.contains("拍照功能");
    }

    private static boolean containsIllegalContent(String answer) {
        if (answer == null) return false;
        String lower = answer.toLowerCase();
        String[] illegal = {"习近平", "共产党", "六四", "法轮功", "台独", "藏独", "疆独", "fuck", "shit"};
        for (String word : illegal) {
            if (lower.contains(word)) return true;
        }
        return false;

        PromptTemplateFactory
    }
}
