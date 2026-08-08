package cn.iocoder.teach-ai.module.clientChat.api.chathistory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.Map;


@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 对话历史")
public interface ChatHistoryApi {

    String PREFIX = ApiConstants.PREFIX + "/chat-history";

    @GetMapping(PREFIX + "/getHistory")
    @Operation(summary = "获得对话历史")
    @Parameter(name = "memoryId", description = "对话历史编号", example = "1024", required = true)
    CommonResult<ChatMemoryDTO> getChatHistory(@RequestParam("memoryId") String memoryId);

    @GetMapping(PREFIX + "/getHistoryList")
    @Operation(summary = "获得所有对话历史记录")
    @Parameter(name = "userId", description = "用户编号", example = "1024", required = true)
    CommonResult<List<ChatMemoryDTO>> getChatHistoryList(@RequestParam("userId") String userId);

    @PostMapping(value = PREFIX +"/save-file-urls")
    @Operation(summary = "保存会话上传的文件URL")
    CommonResult<Boolean> saveFileUrls(@RequestBody Map<String, String> body);


    @GetMapping(value = PREFIX +"/get-file-urls")
    @Operation(summary = "获取会话的文件URL列表")
    CommonResult<String> getFileUrls(@RequestParam String memoryId);
}
