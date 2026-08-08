package cn.iocoder.teach-ai.module.clientChat.framework.agent;

import reactor.core.publisher.Sinks;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求级 token 流上下文。CoderAgent 的 onPartialResponse 通过此上下文
 * 把逐 token 推入 SSE 流，实现消息内容流式渲染。
 * <p>
 * 内置 base64 图片数据过滤：当 LLM 在回复中输出 data:image/...;base64,... 时，
 * 自动抑制该段数据不发送到前端（图片走 event:meta.toolCalls 通道）。
 * <p>
 * 过滤策略：所有 token 逐字符经过状态机，不设快路径。
 * 因为 token 可能被切得很碎（如 "data", ":", "image", "/" 单独成 token），
 * 快路径的 contains 检查无法匹配完整模式 "data:image/"。
 */
public class TokenStreamContext {

    private static final ConcurrentHashMap<String, Sinks.Many<String>> TOKEN_SINKS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Base64Suppressor> SUPPRESSORS = new ConcurrentHashMap<>();

    /** base64 过滤状态机 */
    private static class Base64Suppressor {
        boolean active;
        int depth;
        boolean seenImage;

        void feed(StringBuilder safe, String token) {
            for (int i = 0; i < token.length(); i++) {
                char c = token.charAt(i);
                if (!active) {
                    safe.append(c);
                    // 检测 data:image/ —— 进入抑制模式
                    if (safe.length() >= 11 &&
                            safe.substring(safe.length() - 11).equals("data:image/")) {
                        int cut = safe.length() - 11;
                        while (cut > 0) {
                            if (cut >= 2 && safe.charAt(cut - 2) == ']' && safe.charAt(cut - 1) == '(') {
                                cut -= 2; break;
                            }
                            if (safe.charAt(cut - 1) == '[' && cut >= 2 && safe.charAt(cut - 2) == '!') {
                                cut -= 2; break;
                            }
                            cut--;
                        }
                        safe.setLength(cut);
                        active = true;
                        seenImage = true;
                        depth = 0;
                    }
                } else {
                    if (c == '(') depth++;
                    else if (c == ')') {
                        if (depth == 0) {
                            active = false;
                            seenImage = false;
                        } else {
                            depth--;
                        }
                    }
                }
            }
        }

        boolean hasDangling() { return active; }
    }

    public static void register(String memoryId, Sinks.Many<String> sink) {
        TOKEN_SINKS.put(memoryId, sink);
        SUPPRESSORS.put(memoryId, new Base64Suppressor());
    }

    /**
     * 过滤后发出 token。所有 token 走状态机慢路径，
     * 确保被切碎的 data URL 片段也能被正确拦截。
     */
    public static void emitFiltered(String memoryId, String token) {
        Sinks.Many<String> sink = TOKEN_SINKS.get(memoryId);
        Base64Suppressor sup = SUPPRESSORS.get(memoryId);
        if (sink == null || sup == null) return;

        StringBuilder safe = new StringBuilder();
        sup.feed(safe, token);
        if (safe.length() > 0) {
            sink.tryEmitNext(safe.toString());
        }
    }

    public static Sinks.Many<String> get(String memoryId) {
        return TOKEN_SINKS.get(memoryId);
    }

    public static void remove(String memoryId) {
        TOKEN_SINKS.remove(memoryId);
        SUPPRESSORS.remove(memoryId);
    }

    public static boolean has(String memoryId) {
        return TOKEN_SINKS.containsKey(memoryId);
    }
}
