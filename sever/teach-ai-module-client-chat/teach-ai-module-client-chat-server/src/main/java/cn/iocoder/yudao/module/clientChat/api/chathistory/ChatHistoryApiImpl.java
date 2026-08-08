package cn.iocoder.teach-ai.module.clientChat.api.chathistory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import cn.iocoder.teach-ai.module.clientChat.repository.ChatMemoryRepository;
import cn.iocoder.teach-ai.module.clientChat.service.chathistory.ClientChatHistoryService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.CHATMEMORYID_NOT_NULL;
import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.CHATMESSAGE_IS_NULL;

@Slf4j
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class ChatHistoryApiImpl implements ChatHistoryApi {

    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @Resource
    private ClientChatHistoryService clientChatHistoryService;

    @Override
    public CommonResult<ChatMemoryDTO> getChatHistory(String memoryId) {
        // 边界条件：memoryId 为空直接返回空列表
        if (memoryId == null) {
            return CommonResult.error(CHATMEMORYID_NOT_NULL);
        }

        // 查询 MongoDB 获取 JSON 字符串
        Optional<ChatMemoryDO> chatMemoryOpt = chatMemoryRepository.findById(memoryId);
        if (chatMemoryOpt.isEmpty()) {
            return CommonResult.error(CHATMESSAGE_IS_NULL);
        }

        ChatMemoryDO chatMemoryDO = chatMemoryOpt.get();

        return CommonResult.success(BeanUtils.toBean(chatMemoryDO,ChatMemoryDTO.class));
    }

    @Override
    public CommonResult<List<ChatMemoryDTO>> getChatHistoryList(String userId) {

        List<ChatMemoryDO> messageList = chatMemoryRepository.findByMemoryIdStartingWith("用户" + userId);
//        log.info("getChatHistoryList messageList={}", messageList);

        return CommonResult.success(BeanUtils.toBean(messageList, ChatMemoryDTO.class));
    }

    @Override
    public CommonResult<Boolean> saveFileUrls(Map<String, String> body) {
        clientChatHistoryService.saveFileUrls(
                body.get("memoryId"), body.get("fileUrlsJson"));
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<String> getFileUrls(String memoryId) {
        return CommonResult.success(
                clientChatHistoryService.getFileUrls(memoryId));
    }
}
