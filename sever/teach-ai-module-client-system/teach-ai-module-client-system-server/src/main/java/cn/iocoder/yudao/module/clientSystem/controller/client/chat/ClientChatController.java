package cn.iocoder.teach-ai.module.clientSystem.controller.client.chat;

import cn.iocoder.teach-ai.framework.common.pojo.ChatParam;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.MemoryFileSummaryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.webClientApi.chat.ClientChatApi;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatAllMessageListVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.chat.vo.ChatMemoryVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.service.chat.ClientChatHistoryService;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ClientUserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Tag(name = "客户端接口 - 聊天")
@RestController
@RequestMapping("/client-api/client-system")
@Validated
public class ClientChatController {

    @Resource
    private ClientChatApi clientChatApi;

    @Resource
    private ClientChatHistoryService clientChatHistoryService;

    @Resource
    private FileIngestionApi fileIngestionApi;

    @Resource
    private ChatHistoryApi chatHistoryApi;

    @PostMapping(value = "/stream-post", produces = "text/html;charset=utf-8")
    @Operation(summary = "流式生成聊天内容")
    public Flux<String> streamPost(@RequestBody ChatParam chatType) {
        // 从安全上下文中注入当前用户ID
        String userIdStr = ClientUserContext.getCurrentUserId();
        if (userIdStr != null) {
            try {
                chatType.setUserId(Long.parseLong(userIdStr));
            } catch (NumberFormatException ignored) {}
        }
        return clientChatApi.streamChat(chatType);
    }

    @PostMapping(value = "/agent-post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式生成聊天内容")
    public Flux<ServerSentEvent<String>> agentPost(@RequestBody ChatParam chatType) {
        // 从安全上下文中注入当前用户ID
        String userIdStr = ClientUserContext.getCurrentUserId();
        if (userIdStr != null) {
            try {
                chatType.setUserId(Long.parseLong(userIdStr));
            } catch (NumberFormatException ignored) {}
        }
        return clientChatApi.agentStream(chatType);
    }

    @GetMapping(value = "/get-chat-history")
    @Operation(summary = "获取聊天记录")
    public CommonResult<ChatMemoryVO> getChatHistory(@Valid String memoryId) {
        ChatMemoryVO chatHistory = clientChatHistoryService.getChatHistory(memoryId);
        return CommonResult.success(BeanUtils.toBean(chatHistory, ChatMemoryVO.class));
    }

    @GetMapping(value = "/get-chat-history-list")
    @Operation(summary = "获取全部聊天记录集合")
    public CommonResult<List<ChatAllMessageListVO>> getChatHistoryList(@Valid String userId) {
        List<ChatMemoryDTO> chatHistoryList = clientChatHistoryService.getChatHistoryList(userId);
        return CommonResult.success(BeanUtils.toBean(chatHistoryList, ChatAllMessageListVO.class));
    }

    @GetMapping("/file-summary")
    CommonResult<MemoryFileSummaryRespDTO> getFileSummary(@RequestParam String memoryId){
        return fileIngestionApi.getFileSummary(memoryId);
    }

    @PostMapping("/save-file-urls")
    public CommonResult<Boolean> saveFileUrls(@RequestBody Map<String, String> body) {
        return chatHistoryApi.saveFileUrls(body);
    }

    @GetMapping("/get-file-urls")
    public CommonResult<String> getFileUrls(@RequestParam String memoryId) {
        return chatHistoryApi.getFileUrls(memoryId);
    }

}
