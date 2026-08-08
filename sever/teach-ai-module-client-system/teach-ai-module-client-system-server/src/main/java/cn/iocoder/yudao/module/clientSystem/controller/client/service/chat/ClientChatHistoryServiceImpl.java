package cn.iocoder.teach-ai.module.clientSystem.controller.client.service.chat;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatAllMessageListVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatMemoryVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatMessageVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ChatHistoryConverter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClientChatHistoryServiceImpl implements ClientChatHistoryService {

    @Resource
    private ChatHistoryApi chatHistoryApi;

    @Override
    public ChatMemoryVO getChatHistory(String memoryId) {
        CommonResult<ChatMemoryDTO> chatHistory = chatHistoryApi.getChatHistory(memoryId);

        List<ChatMessageVO> messageVOS = ChatHistoryConverter.convertToFrontendMessages(chatHistory.getData().getMessagesJson());

        if (messageVOS == null){
            return new ChatMemoryVO();
        }

        ChatMemoryVO chatMemoryVO = new ChatMemoryVO();
        chatMemoryVO.setMessages(messageVOS);
        chatMemoryVO.setCreateTime(chatHistory.getData().getCreateTime());
        chatMemoryVO.setUpdateTime(chatHistory.getData().getUpdateTime());
        return chatMemoryVO;
    }

    @Override
    public List<ChatMemoryDTO> getChatHistoryList(String userId) {
        return chatHistoryApi.getChatHistoryList(userId).getCheckedData();
    }
}
