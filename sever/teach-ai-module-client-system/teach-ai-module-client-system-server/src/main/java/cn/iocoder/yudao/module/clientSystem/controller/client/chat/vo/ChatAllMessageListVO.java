package cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatAllMessageListVO {


    /**
     * 会话ID（对应 MongoDB 的 _id 主键）
     */
    private String memoryId;


    /**
     * 会话标题
     */
    private String messageTitle;

    /**
     * 会话消息列表（以 JSON 字符串形式存储）
     * 使用 ChatMessageSerializer/ChatMessageDeserializer 进行序列化/反序列化
     */
    private String messagesJson;

    /** 上传的文件 URL 列表（JSON 数组字符串） */
    private String fileUrlsJson;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
