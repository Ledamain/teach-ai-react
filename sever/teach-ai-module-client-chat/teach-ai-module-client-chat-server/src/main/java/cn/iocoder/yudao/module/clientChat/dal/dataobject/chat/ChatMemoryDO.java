package cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 会话记忆实体（对应 MongoDB 集合：chat_memory）
 * 使用 JSON 字符串存储消息列表，避免 MongoDB 对 ChatMessage 接口的序列化问题
 */
@Data
@Document(collection = "chat_memory") // 指定 MongoDB 集合名
public class ChatMemoryDO {

    /**
     * 会话ID（对应 MongoDB 的 _id 主键）
     */
    @Id
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

    // 自动填充创建/更新时间（可选，简化业务代码）
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
        }
        this.updateTime = now;
    }
}
