package cn.iocoder.teach-ai.module.clientChat.service.chathistory;

import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import cn.iocoder.teach-ai.module.clientChat.repository.ChatMemoryRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ClientChatHistoryServiceImpl implements ClientChatHistoryService{

    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @Override
    public void saveFileUrls(String memoryId, String fileUrlsJson) {
        ChatMemoryDO doc = chatMemoryRepository.findById(memoryId).orElse(new ChatMemoryDO());
        doc.setMemoryId(memoryId);
        doc.setFileUrlsJson(fileUrlsJson);
        doc.prePersist();
        chatMemoryRepository.save(doc);
    }

    @Override
    public String getFileUrls(String memoryId) {
        return chatMemoryRepository.findById(memoryId)
                .map(ChatMemoryDO::getFileUrlsJson).orElse(null);
    }
}
