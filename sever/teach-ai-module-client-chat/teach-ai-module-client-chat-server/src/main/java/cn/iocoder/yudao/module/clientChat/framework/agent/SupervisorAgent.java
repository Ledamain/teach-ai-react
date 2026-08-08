package cn.iocoder.teach-ai.module.clientChat.framework.agent;

import cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag.RagAgent;
import cn.iocoder.teach-ai.module.clientChat.service.aiService.ConsultantService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SupervisorAgent implements NodeAction<State> {

    @Resource
    private ConsultantService consultantService;

    public final String[] members = {"researcher", "coder", "rag", "FINISH"};

    @Override
    public Map<String, Object> apply(State state) throws Exception {
        var newQuestion = extractLastUserMessage(state);

        // ── 硬规则: 根据最后 AI 消息决定路由 ──
        var lastAi = lastAiMessage(state);
        if (lastAi != null) {
            // 如果最后一条消息是用户消息（有新的未回复问题），跳过硬规则让 LLM 路由
            var lastMsg = lastMessage(state);
            var hasNewUserMsg = lastMsg != null
                && lastMsg.type() == ChatMessageType.USER
                && getUserMessageCount(state) > 0;
            if (hasNewUserMsg) {
                log.info("Supervisor: 检测到新用户消息，跳过已有回答检查，交由 LLM 决策");
            } else {
            var aiText = lastAi.text();
            if (aiText != null) {
                // 规则0: 代码生成模式 → reviewer 已通过，直接 FINISH
                if (state.codeGenMode()) {
                    log.info("Supervisor: codeGenMode, reviewer 已通过 → FINISH");
                    return Map.of(
                        "next", "FINISH",
                        "loopCount", state.loopCount() + 1,
                        "summary", buildSummary(state, newQuestion),
                        "codeGenMode", false
                    );
                }
                // 规则1a: coder 追问信息 → 用户回复 → 路由 coder 处理信息完整性
                if (previousCoderAskedForInfo(state)
                        && getUserMessageCount(state) >= 2) {
                    log.info("Supervisor: 上一轮 coder 追问，用户已回复 → 路由 coder");
                    return Map.of(
                        "next", "coder",
                        "loopCount", state.loopCount() + 1,
                        "summary", buildSummary(state, newQuestion)
                    );
                }
                // 规则1b: coder 展示确认清单 → 用户确认 → 路由 rag 检索+生成教案
                if (previousCoderShowedChecklist(state)
                        && isUserConfirming(newQuestion)) {
                    log.info("Supervisor: 用户确认 → 路由 rag 生成教案");
                    return Map.of(
                        "next", "rag",
                        "loopCount", state.loopCount() + 1,
                        "summary", buildSummary(state, newQuestion)
                    );
                }
                // 规则2: rag 产出固定标记 → 路由 coder 兜底
                if (aiText.contains(RagAgent.NO_RESULT_MARKER)) {
                    log.info("Supervisor: rag 无有效结果 → 路由 coder");
                    return Map.of(
                        "next", "coder",
                        "loopCount", state.loopCount() + 1,
                        "summary", buildSummary(state, newQuestion)
                    );
                }
                // 规则3: 有效内容（非标记、非追问）→ FINISH
                if (aiText.length() > 20 && !isClarificationText(aiText)) {
                    log.info("Supervisor: 已有有效回答(len={}) → FINISH", aiText.length());
                    return Map.of(
                        "next", "FINISH",
                        "loopCount", state.loopCount() + 1,
                        "summary", buildSummary(state, newQuestion)
                    );
                }
            }
            } // end of hasNewUserMsg check
        }

        // ── coder 追问回复快速通道 ──
        if (previousCoderAskedForInfo(state) && newQuestion.length() < 80
                && getUserMessageCount(state) >= 2) {
            log.info("Supervisor: 检测到用户回复追问，路由 coder");
            var newSummary = buildSummary(state, newQuestion);
            return Map.of(
                "next", "coder",
                "loopCount", state.loopCount() + 1,
                "summary", newSummary
            );
        }

        // ── 确认生成快速通道 ──
        if (previousCoderShowedChecklist(state) && isUserConfirming(newQuestion)) {
            log.info("Supervisor: 用户确认生成，直接路由 rag");
            var newSummary = buildSummary(state, newQuestion);
            return Map.of(
                "next", "rag",
                "loopCount", state.loopCount() + 1,
                "summary", newSummary
            );
        }

        // ── 代码生成快速通道 ──
        if (isCodeGenerationRequest(newQuestion) && state.loopCount() == 0) {
            log.info("Supervisor: 检测到代码生成请求 → 路由 coder (codeGenMode)");
            var newSummary = buildSummary(state, newQuestion);
            return Map.of(
                "next", "coder",
                "loopCount", state.loopCount() + 1,
                "summary", newSummary,
                "codeGenMode", true
            );
        }

        var prompt = """
        【累计上下文摘要】%s
        
        【用户最新问题】%s
        """.formatted(buildSummary(state, newQuestion), newQuestion);

        var string = String.join(",", members);
        var next = supervisorChatWithFallback(string, prompt, state.loopCount());

        // ── 硬规则 3: 首轮禁止 FINISH（LLM 可能忽略 prompt 规则）
        if (state.loopCount() == 0 && "FINISH".equals(next)) {
            log.info("Supervisor: 首轮 LLM 返回 FINISH → 降级为 coder");
            next = "coder";
        }

        // ── 硬规则 4: LLM 返回 FINISH 但有 MCP 工具调用证据且无用户可见回复 → 强制回 coder
        if ("FINISH".equals(next) && hasMcpToolEvidence(state)) {
            var lastText = lastAiMessage(state);
            String text = lastText != null ? lastText.text() : null;
            if (text == null || text.length() <= 20) {
                log.info("Supervisor: MCP 证据存在但无有效回复 → 强制回 coder");
                next = "coder";
            }
        }
        var newSummary = buildSummary(state, newQuestion);
        return Map.of(
                "next", next,
                "loopCount", state.loopCount() + 1,
                "summary", newSummary
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Stream<ChatMessage> safeMsgStream(State state) {
        Stream rawStream = state.messages().stream();
        return rawStream
                .filter(o -> o instanceof ChatMessage)
                .map(o -> (ChatMessage) o);
    }

    private static String userText(ChatMessage m) {
        if (m instanceof UserMessage u) return u.singleText();
        return "";
    }

    private static String aiText(ChatMessage m) {
        if (m instanceof AiMessage a) return a.text();
        return "";
    }

    private String extractLastUserMessage(State state) {
        return safeMsgStream(state)
                .filter(m -> m.type() == ChatMessageType.USER)
                .map(SupervisorAgent::userText)
                .reduce((first, second) -> second)
                .orElse("");
    }

    private static AiMessage lastAiMessage(State state) {
        return safeMsgStream(state)
            .filter(m -> m.type() == ChatMessageType.AI && m instanceof AiMessage)
            .map(m -> (AiMessage) m)
            .reduce((first, second) -> second)
            .orElse(null);
    }

    private String buildSummary(State state, String latestQuestion) {
        var context = safeMsgStream(state)
                .map(m -> switch (m.type()) {
                    case USER -> "用户: " + userText(m);
                    case AI   -> "助手: " + summarize(aiText(m), 500);
                    default   -> "";
                })
                .collect(Collectors.joining("\n"));
        if (context.length() < 2000) return context;
        var result = consultantService.summarizeContext(context);
        return result != null ? result : "对话进行中";
    }

    private static String summarize(String s, int maxChars) {
        if (s == null || s.length() <= maxChars) return s;
        int cut = s.substring(0, maxChars).lastIndexOf('。');
        if (cut < 0) cut = s.substring(0, maxChars).lastIndexOf('\n');
        if (cut < 0) cut = maxChars - 1;
        return s.substring(0, cut + 1) + "…";
    }

    /** coder 是否在追问信息（请告诉我/请提供/补充以下 等） */
    private static boolean previousCoderAskedForInfo(State state) {
        var lastAi = lastAiMessage(state);
        if (lastAi == null) return false;
        var text = lastAi.text();
        if (text == null || text.isBlank()) return false;
        return text.contains("请告诉") || text.contains("请提供")
            || text.contains("补充以下") || text.contains("需要了解")
            || text.contains("请补充");
    }

    /** coder 是否展示了确认清单（已明确您的需求 + 请确认） */
    private static boolean previousCoderShowedChecklist(State state) {
        var lastAi = lastAiMessage(state);
        if (lastAi == null) return false;
        var text = lastAi.text();
        if (text == null || text.isBlank()) return false;
        return text.contains("已明确") || text.contains("请确认以上")
            || (text.contains("确认清单") || text.contains("请确认"));
    }

    /** 用户是否表达了确认意图（短词精确匹配 + 长消息包含关键词） */
    private static boolean isUserConfirming(String text) {
        if (text == null || text.isBlank()) return false;
        var stripped = text.trim().replaceAll("[，。！？,.!?\\s]+", "");
        // 短消息：精确匹配常见确认词
        if (stripped.length() <= 4) {
            return stripped.equals("确认") || stripped.equals("是")
                || stripped.equals("对") || stripped.equals("好的")
                || stripped.equals("可以") || stripped.equals("行")
                || stripped.equals("好") || stripped.equalsIgnoreCase("ok")
                || stripped.equalsIgnoreCase("yes");
        }
        // 长消息：包含确认或生成关键词
        return stripped.contains("确认") || stripped.contains("生成")
            || stripped.contains("好的") || stripped.contains("可以");
    }

    /** 检测用户是否在要求生成代码 */
    private static boolean isCodeGenerationRequest(String text) {
        if (text == null || text.isBlank()) return false;
        return text.contains("生成代码") || text.contains("写代码")
            || text.contains("编写代码") || text.contains("写个代码")
            || text.contains("帮我写") || text.contains("帮我生成")
            || text.contains("实现这个") || text.contains("实现一个")
            || text.contains("写一个") || text.contains("写个")
            || text.contains("编程") || text.contains("代码实现")
            || text.contains("coding") || text.contains("write code");
    }

    /** 检测文本是否是追问/澄清而不是完整回答 */
    private static boolean isClarificationText(String text) {
        if (text == null || text.isBlank()) return true;
        return text.contains("请告诉") || text.contains("请提供")
            || text.contains("补充以下") || text.contains("需要了解")
            || text.contains("请补充") || text.contains("不太明确")
            || text.contains("?") || text.contains("？");
    }


    /** 获取最后一条消息（任意类型），没有消息返回 null */
    private static ChatMessage lastMessage(State state) {
        return safeMsgStream(state)
                .reduce((first, second) -> second)
                .orElse(null);
    }
    private static long getUserMessageCount(State state) {
        return safeMsgStream(state)
                .filter(m -> m.type() == ChatMessageType.USER)
                .count();
    }

    /** LLM 路由调用，带降级处理 */
    private String supervisorChatWithFallback(String members, String prompt, int loopCount) {
        try {
            var result = consultantService.SupervisorChat(members, prompt);
            log.info("Supervisor Result: {}", result);
            return result.getNext();
        } catch (Exception e) {
            log.warn("Supervisor LLM 解析失败 (loop={}), 降级 FINISH: {}", loopCount, e.getMessage());
            return "FINISH";
        }
    }

    /** 检测对话中是否存在 MCP 工具调用证据 */
    private static boolean hasMcpToolEvidence(State state) {
        return safeMsgStream(state)
                .filter(m -> m.type() == ChatMessageType.AI)
                .map(m -> ((AiMessage) m).text())
                .anyMatch(text -> text != null && (
                        text.contains("📸") || text.contains("📷")
                                || text.contains("拍照成功")
                                || text.contains("小度智能屏") || text.contains("小度设备")
                                || text.contains("在线设备") || text.contains("推送通知")
                                || text.contains("视频录制") || text.contains("拍照功能")
                ));
    }
}
