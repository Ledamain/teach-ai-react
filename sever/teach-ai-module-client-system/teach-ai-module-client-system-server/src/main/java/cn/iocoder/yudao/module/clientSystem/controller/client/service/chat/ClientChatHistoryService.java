package cn.iocoder.teach-ai.module.clientSystem.controller.client.service.chat;

import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatAllMessageListVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatMemoryVO;

import java.util.List;

public interface ClientChatHistoryService {

    ChatMemoryVO getChatHistory(String memoryId);

    List<ChatMemoryDTO> getChatHistoryList(String userId);

}
