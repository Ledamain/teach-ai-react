package cn.iocoder.teach-ai.module.clientChat.repository.store;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import cn.iocoder.teach-ai.module.clientChat.repository.ChatMemoryRepository;
import cn.iocoder.teach-ai.module.clientChat.service.chatmemory.aiService.MemoryTitleAiService;
import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.ConversionApi;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.CountRecordApi;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.dto.CountRecordDTO;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * MongoDB 实现的会话记忆存储（替换原 RedisChatMemoryStore）
 * 使用 JSON 字符串存储消息列表，避免 MongoDB 对 ChatMessage 接口的序列化问题
 */
@Slf4j
@Component
public class MongoChatMemoryStore implements ChatMemoryStore {

    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @Resource
    private ConversionApi conversionApi;

    @Resource
    private ChatHistoryApi chatHistoryApi;

    @Resource
    private CountRecordApi countRecordApi;

    @Autowired
    private View error;

    @Resource
    private MemoryTitleAiService memoryTitleAiService;


    /**
     * 根据会话ID查询消息列表
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 边界条件：memoryId 为空直接返回空列表
        if (memoryId == null) {
            log.warn("memoryId is null, return empty messages");
            return List.of();
        }
        String memoryIdStr = memoryId.toString().trim();
        if (memoryIdStr.isEmpty()) {
            log.warn("memoryId is empty, return empty messages");
            return List.of();
        }

        // 查询 MongoDB 获取 JSON 字符串
        Optional<ChatMemoryDO> chatMemoryOpt = chatMemoryRepository.findById(memoryIdStr);
        if (chatMemoryOpt.isEmpty()) {
            return List.of();
        }

        String messagesJson = chatMemoryOpt.get().getMessagesJson();
        if (messagesJson == null || messagesJson.isEmpty()) {
            return List.of();
        }

        // 反序列化 JSON 为 ChatMessage 列表
        List<ChatMessage> messages = ChatMessageDeserializer.messagesFromJson(messagesJson);
        log.info("MongoDB 对话读取成功, memoryId: {}, 消息数: {}", memoryIdStr, messages.size());
        return messages;
    }

    /**
     * 更新/新增会话消息（Upsert）
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 边界条件校验
        if (memoryId == null) {
            log.error("memoryId is null, cannot update messages");
            throw new IllegalArgumentException("memoryId cannot be null");
        }
        String memoryIdStr = memoryId.toString().trim();
        if (memoryIdStr.isEmpty()) {
            log.error("memoryId is empty, cannot update messages");
            throw new IllegalArgumentException("memoryId cannot be empty");
        }

        // 1. 查询已有记录（无则新建）
        ChatMemoryDO chatMemory = chatMemoryRepository.findById(memoryIdStr)
                .orElse(new ChatMemoryDO());

        // 2. 优先使用 LLM 生成标题，失败则降级为截断
        String messageTitle = chatMemory.getMessageTitle();
        if (messageTitle == null || messageTitle.isEmpty() || "未命名会话".equals(messageTitle)) {

            List<UserMessage> userMessages = messages.stream()
                    .filter(message -> message.type() == ChatMessageType.USER)
                    .map(message -> (UserMessage) message)
                    .collect(Collectors.toList());

            if (!userMessages.isEmpty()) {
                String firstUserMessage = userMessages.get(0).singleText();
                messageTitle = generateTitleByLLM(firstUserMessage);
                if (messageTitle == null || messageTitle.isEmpty()) {
                    // LLM 调用失败，降级为截断兜底
                    messageTitle = truncateTitle(firstUserMessage);
                    log.info("LLM 标题生成失败，使用截断兜底, memoryId: {}, 标题: {}", memoryIdStr, messageTitle);
                } else {
                    log.info("LLM 生成标题成功, memoryId: {}, 标题: {}", memoryIdStr, messageTitle);
                }
                chatMemory.setMessageTitle(messageTitle);
            } else {
                // 兜底：本次没有用户消息，暂不设置，等待下一次调用
                log.debug("本次 messages 中无用户消息，暂不设置标题, memoryId: {}", memoryIdStr);
            }
        }

        // 3. 将消息列表序列化为 JSON 字符串
        String messagesJson = ChatMessageSerializer.messagesToJson(messages);

        // 4. 设置属性并保存到 MongoDB
        chatMemory.setMemoryId(memoryIdStr);
        chatMemory.setMessagesJson(messagesJson);
        chatMemory.prePersist(); // 自动填充时间
        messageRecordCalc(memoryIdStr);
        chatMemoryRepository.save(chatMemory);

        // 5. 同步mysql中的历史数据（此时标题已确保生成）
        String conversionId = memoryIdStr.split("_")[1];
        String user = memoryIdStr.split("_")[0];
        String userId = memoryIdStr.split("_")[0].replace("用户", "");
        log.info("会话id：{},用户：{}",conversionId,user);
        CommonResult<ConversionDTO> conversion = conversionApi.getConversionByConversionId(Long.valueOf(conversionId));
        log.info("会话记录：{}",conversion);

        if (conversion.getCheckedData() == null && conversion.getCode() != 401){
            ConversionDTO conversionDTO = new ConversionDTO();
            conversionDTO.setConversionId(conversionId);
            conversionDTO.setCreator(user);
            conversionDTO.setTitle(chatMemory.getMessageTitle()); // 此时标题已非空
            conversionDTO.setCreateTime(chatMemory.getCreateTime());
            conversionDTO.setClientUserId(Long.valueOf(userId));
            log.info("创建会话记录：{}",conversionDTO);
            conversionApi.createConversion(conversionDTO);
        } else if (conversion.getCheckedData() != null && conversion.getCheckedData().getTitle() == null) {
            // 补充：更新已有但标题为空的会话记录
            ConversionDTO updateDTO = new ConversionDTO();
            updateDTO.setId(conversion.getCheckedData().getId());

            updateDTO.setConversionId(conversionId);
            updateDTO.setTitle(chatMemory.getMessageTitle());
            updateDTO.setClientUserId(Long.valueOf(userId));
            conversionApi.updateConversion(updateDTO);
            log.info("补充更新MySQL会话标题, conversionId: {}, title: {}", conversionId, chatMemory.getMessageTitle());
        }

        log.info("MongoDB 对话存储成功, memoryId: {}, 消息数: {}, 标题: {}",
                memoryIdStr, messages.size(), chatMemory.getMessageTitle());
    }

    /**
     * 删除指定会话的消息
     */
    @Override
    public void deleteMessages(Object memoryId) {
        if (memoryId == null) {
            log.warn("memoryId is null, skip delete");
            return;
        }
        String memoryIdStr = memoryId.toString().trim();
        if (memoryIdStr.isEmpty()) {
            log.warn("memoryId is empty, skip delete");
            return;
        }

        // 调用 Repository 自动生成的方法
        chatMemoryRepository.deleteById(memoryIdStr);
        log.debug("delete chat memory success, memoryId: {}", memoryIdStr);
    }

    // ========== 扩展方法（可选，满足复杂查询需求） ==========
    /**
     * 分页查询7天内的会话记忆（演示 Criteria 条件构造器）
     */
    public Page<ChatMemoryDO> listRecentChatMemory(int pageNum, int pageSize) {
        // 1. 构建分页参数（MongoDB 分页从 0 开始）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 2. 调用 Repository 方法名推导查询
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return chatMemoryRepository.findByCreateTimeAfter(sevenDaysAgo, pageable);
    }


    /**
     * 调用 LLM 生成会话标题（对用户第一条消息进行语义概括）
     *
     * @param userMessage 用户的第一条消息原文
     * @return LLM 生成的标题，失败时返回 null
     */
    private String generateTitleByLLM(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return null;
        }
        try {
            // 构造轻量 prompt：要求模型用不超过20个字概括用户问题
            String title = memoryTitleAiService.generateTitle(userMessage);
            if (title != null) {
                title = title.replace("\"", "").replace("'", "")
                        .replace("。", "").replace("，", "")
                        .replace("《", "").replace("》", "")
                        .trim();
                // 兜底长度限制：最多 30 个字符
                if (title.length() > 30) {
                    title = title.substring(0, 30);
                }
            }
            return title;
        } catch (Exception e) {
            log.warn("LLM 标题生成异常，将使用截断兜底: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 截断兜底：取前30个字符作为标题（LLM 调用失败时使用）
     */
    private String truncateTitle(String content) {
        if (content == null || content.isBlank()) {
            return "未命名会话";
        }
        // 去除换行和多余空格
        String cleaned = content.replaceAll("\\n+", " ").replaceAll("\\s+", " ").trim();
        if (cleaned.length() <= 30) {
            return cleaned;
        }
        return cleaned.substring(0, 30);
    }

    private void messageRecordCalc(String memoryIdStr) {
        memoryIdStr = memoryIdStr.split("_")[0].substring(2);
        Long userId = Long.valueOf(memoryIdStr);
        log.info("用户id：{}",userId);
        CountRecordDTO countRecordDTO = new CountRecordDTO().setUserId(userId);
        Long recordId = countRecordApi.createCountRecord(countRecordDTO).getData();
        log.info("创建使用次数记录：{}",recordId);
        CountRecordDTO result = countRecordApi.getCountRecordById(recordId).getData();
        Long newRecordCount = result.getRecordCount() + 1L;
        result.setRecordCount(newRecordCount);
        countRecordApi.updateCountRecord(result);
    }

}
