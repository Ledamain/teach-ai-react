package cn.iocoder.teach-ai.module.clientChat.mcp.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 工具调用事件，用于流式传输工具调用过程到前端展示。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallEvent {

    /** 事件类型: tool_call_start / tool_call_end */
    private String type;

    /** 工具名称，如 xiaodu_take_photo */
    private String toolName;

    /** 调用参数 JSON */
    private String arguments;

    /** 工具返回結果摘要（仅 type=tool_call_end 时有值） */
    private String result;

    /** 调用耗时（毫秒），仅 type=tool_call_end 时有值 */
    private Long durationMs;
}
