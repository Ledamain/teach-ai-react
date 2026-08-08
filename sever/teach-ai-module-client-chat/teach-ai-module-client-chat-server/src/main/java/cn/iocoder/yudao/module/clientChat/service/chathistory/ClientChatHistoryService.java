package cn.iocoder.teach-ai.module.clientChat.service.chathistory;

import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.history.ChatHistoryDO;

import java.util.List;

public interface ClientChatHistoryService {

    void saveFileUrls(String memoryId, String fileUrlsJson);

    String getFileUrls(String memoryId);



}
