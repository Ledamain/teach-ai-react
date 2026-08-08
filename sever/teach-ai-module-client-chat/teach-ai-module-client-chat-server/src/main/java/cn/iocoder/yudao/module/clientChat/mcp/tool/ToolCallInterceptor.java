package cn.iocoder.teach-ai.module.clientChat.mcp.tool;

import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 工具调用拦截器，包装 ToolExecutor 以捕获调用事件。
 * <p>
 * 使用 ConcurrentHashMap（key=memoryId）替代 ThreadLocal，
 * 因为工具执行发生在 LangChain4j OkHttp 线程池，
 * 而非请求线程/daemon 线程，ThreadLocal 跨线程不可见。
 * <p>
 * 使用方式：在 McpToolProvider.Builder 中设置 toolWrapper（拦截 MCP 工具）。
 * 每次请求前调用 {@link #beginRequest(String)} 开始收集，
 * 请求结束后调用 {@link #endRequest(String)} 获取事件列表。
 */
public class ToolCallInterceptor implements Function<ToolExecutor, ToolExecutor> {

    private static final Logger log = LoggerFactory.getLogger(ToolCallInterceptor.class);

    /** memoryId → 工具调用事件列表 */
    private static final ConcurrentHashMap<String, List<ToolCallEvent>> EVENT_MAP = new ConcurrentHashMap<>();

    private ToolCallInterceptor() {}

    public static ToolCallInterceptor create() {
        return new ToolCallInterceptor();
    }

    /** 开始收集当前请求的工具调用事件 */
    public static void beginRequest(String memoryId) {
        EVENT_MAP.put(memoryId, new ArrayList<>());
    }

    /**
     * 获取并清除当前请求的所有工具调用事件。
     * 优先从 business memoryId 获取，fallback 到 "default"（LangChain4j 内部 memoryId）。
     */
    public static List<ToolCallEvent> endRequest(String memoryId) {
        // 先检查 business key
        List<ToolCallEvent> events = EVENT_MAP.remove(memoryId);
        // fallback: LangChain4j 内部 memoryId（工具在 OkHttp 线程执行，ThreadLocal 不可用，统一用 "default"）
        List<ToolCallEvent> fallback = EVENT_MAP.remove("default");

        List<ToolCallEvent> result = new ArrayList<>();
        if (events != null) result.addAll(events);
        if (fallback != null) result.addAll(fallback);
        return result;
    }

    /**
     * 智能截断：图片 data URL 完整保留（前端需渲染），超长文本截断到 5000 字符。
     */
    private static String truncateResult(String result) {
        if (result == null) return "(无返回)";
        // 图片 data URL：完整保留，不截断（前端需要渲染图片）
        if (result.startsWith("[📷") || result.contains("data:image/")) {
            return result;
        }
        // 超长文本（如大段 Markdown）：截断到 5000 字符
        if (result.length() > 5000) {
            return result.substring(0, 5000) + "... (truncated, " + result.length() + " chars total)";
        }
        return result;
    }

    @Override
    public ToolExecutor apply(ToolExecutor delegate) {
        return (request, memoryId) -> {
            long startTime = System.currentTimeMillis();
            String toolName = request.name();
            String arguments = request.arguments();

            // 优先使用 business memoryId（InheritableThreadLocal，daemon 线程可用），
            // fallback 到 LangChain4j 内部 memoryId（OkHttp 线程不可用时用 "default"）
            String key = ClientUserContext.getCurrentMemoryId();
            if (key == null || key.isEmpty()) {
                key = String.valueOf(memoryId);
            }

            List<ToolCallEvent> events = EVENT_MAP.computeIfAbsent(key, k -> new ArrayList<>());

            ToolCallEvent startEvent = ToolCallEvent.builder()
                    .type("tool_call_start")
                    .toolName(toolName)
                    .arguments(arguments)
                    .build();
            events.add(startEvent);
            log.info("🔧 工具调用开始: {} args={} key={}", toolName, arguments, key);

            String result;
            try {
                result = delegate.execute(request, memoryId);
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                ToolCallEvent errorEvent = ToolCallEvent.builder()
                        .type("tool_call_end")
                        .toolName(toolName)
                        .arguments(arguments)
                        .result("❌ 调用失败: " + e.getMessage())
                        .durationMs(duration)
                        .build();
                events.add(errorEvent);
                log.warn("🔧 工具调用失败: {} error={}", toolName, e.getMessage());
                throw e;
            }

            long duration = System.currentTimeMillis() - startTime;
            String summary = truncateResult(result);

            ToolCallEvent endEvent = ToolCallEvent.builder()
                    .type("tool_call_end")
                    .toolName(toolName)
                    .arguments(arguments)
                    .result(summary)
                    .durationMs(duration)
                    .build();
            events.add(endEvent);
            log.info("🔧 工具调用完成: {} duration={}ms resultLength={}", toolName, duration,
                    result != null ? result.length() : 0);

            return result;
        };
    }
}
