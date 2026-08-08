package cn.iocoder.teach-ai.module.clientChat.dal.dataobject.history;

import lombok.Data;

/**
* 客户端对话历史 DO
*/

@Data
public class ChatHistoryDO {

    /**
     * 对话历史记忆id
     */
    private String memoryId;
    /**
     * 对话历史标题
     */
    private String chatTitle;
}
