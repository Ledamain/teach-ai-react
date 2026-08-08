package cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天消息 VO (前端直接展示用)
 * 对应前端的 ChatMessage 接口
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVO {

    /**
     * 消息唯一ID (前端 React key 需要)
     */
    private String id;

    /**
     * 角色: 'user' 或 'assistant'
     * 注意：不要返回 'SYSTEM'，前端不需要展示系统提示词
     */
    private String role;

    /**
     * 消息内容 (包含 Markdown/代码块)
     */
    private String content;

    /**
     * 是否正在思考 (历史记录通常为 false)
     */
    private Boolean thinking;

}
