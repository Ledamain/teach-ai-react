package cn.iocoder.teach-ai.module.clientChat.api.chat;

import cn.iocoder.teach-ai.framework.common.pojo.ChatParam;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.TokenStreamContext;import cn.iocoder.teach-ai.module.clientChat.framework.agent.hook.ChatMemorySyncHook;
import cn.iocoder.teach-ai.module.clientChat.service.aiService.ChatStreamEvent;
import cn.iocoder.teach-ai.module.clientChat.service.aiService.ConsultantService;
import cn.iocoder.teach-ai.module.clientChat.service.aiService.MessageTypeClassifier;
import cn.iocoder.teach-ai.module.clientChat.mcp.tool.ToolCallInterceptor;
import cn.iocoder.teach-ai.module.clientChat.mcp.tool.ToolCallEvent;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.CHAT_NOT_EXCEPTION;

import cn.iocoder.teach-ai.module.clientChat.service.fileIngestion.FileIngestionService;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.MemoryFileSummaryRespDTO;
import cn.iocoder.teach-ai.module.clientChat.service.profile.ProfileAgent;
import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.SystemMessageApi;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.dto.SystemMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.RunnableConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

/**
 * Chat 模块内部 RPC 接口。
 */
@Slf4j
@RestController
@RequestMapping("/rpc-api/client-chat/consultant")
public class ClientChatApi {

    private static final String META_DELIMITER = "<<<META>>>";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${consultant.system-message}")
    private String defaultSystemMessage;

    @Resource
    private ConsultantService consultantService;

    @Resource
    private SystemMessageApi systemMessageApi;

    @Resource
    private MessageTypeClassifier messageTypeClassifier;

    @Resource
    private FileIngestionService fileIngestionService;

    @Resource
    private ProfileAgent profileAgent;

    @Resource
    private CompiledGraph<State> agent;

    @Autowired
    private java.util.Optional<ChatMemorySyncHook> chatMemorySyncHook;

    @PostMapping(value = "/stream-post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamPost(@RequestBody ChatParam chatType) {
        try {
            ClientUserContext.setCurrentMemoryId(chatType.getMemoryId());
            ClientUserContext.setCurrentKids(chatType.getKnowledgeIds());
            ClientUserContext.setCurrentUserId(String.valueOf(chatType.getUserId()));

            String systemMessage = "";
            SystemMessageDTO messageDTO = new SystemMessageDTO().setStatus("0");
            CommonResult<List<SystemMessageDTO>> result =
                    systemMessageApi.getSystemMessageList(messageDTO);
            for (SystemMessageDTO dto : result.getCheckedData()) {
                systemMessage += dto.getSystemMessageText();
            }
            if (systemMessage.isEmpty()) {
                systemMessage = defaultSystemMessage;
            }

            String userPrompt = chatType.getPrompt();
            Long userId = chatType.getUserId();

            ToolCallInterceptor.beginRequest(chatType.getMemoryId());
            Flux<String> rawStream = consultantService.stream(
                    chatType.getMemoryId(), userPrompt, systemMessage);

            StringBuilder fullResponse = new StringBuilder();
            Flux<String> passThrough = rawStream.doOnNext(fullResponse::append);

            Mono<String> metaMono = Mono.fromCallable(() -> {
                String text = fullResponse.toString();
                log.info("完整响应长度: {}, 开始分类...", text.length());
                ChatStreamEvent classifyResult = messageTypeClassifier.classify(text, userPrompt);
                List<ChatStreamEvent.RagChunk> ragChunks = ClientUserContext.getRagChunks(chatType.getMemoryId());
                List<ToolCallEvent> toolCalls = ToolCallInterceptor.endRequest(chatType.getMemoryId());

                if (toolCalls != null && !toolCalls.isEmpty()) {
                    classifyResult.setToolCalls(toolCalls);
                    log.info("注入工具调用事件: {} 个", toolCalls.size());
                }
                if (ragChunks != null && !ragChunks.isEmpty()) {
                    classifyResult.setRagChunks(ragChunks);
                    log.info("注入 RAG 检索块: {} 个", ragChunks.size());
                }
                String metaJson = objectMapper.writeValueAsString(classifyResult);
                log.info("分类完成: type={}", classifyResult.getType());

                if (userId != null && userId > 0) {
                    final Long uid = userId;
                    final String mid = chatType.getMemoryId();
                    final String up = userPrompt;
                    final String resp = text;
                    Mono.fromRunnable(() -> {
                        try {
                            profileAgent.extractAndSave(uid, mid, up, resp);
                        } catch (Exception e) {
                            log.warn("画像提取异常(非致命): userId={}, error={}", uid, e.getMessage());
                        }
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();
                }
                return META_DELIMITER + metaJson;
            });

            return passThrough
                    .concatWith(Flux.defer(() -> Flux.from(metaMono)))
                    .doFinally(signalType -> ClientUserContext.clear());

        } catch (Exception e) {
            ClientUserContext.clear();
            log.error("咨询聊天流式生成异常:{}", e.getMessage());
            throw exception(CHAT_NOT_EXCEPTION);
        }
    }

    @GetMapping("/file-summary")
    public CommonResult<MemoryFileSummaryRespDTO> getFileSummary(@RequestParam String memoryId) {
        return CommonResult.success(fileIngestionService.getSummaryByMemoryId(memoryId));
    }

    /**
     * 多智能体 SSE 端点。
     * 事件类型:
     *   node_enter  — Agent 节点切换 (data=节点名: supervisor/coder/reviewer/rag/researcher/__START__/__END__)
     *   message     — Agent 产出的 AI 消息 (data=文本)
     *   meta        — 消息分类 & 工具调用 & RAG 检索元数据 (JSON)
     *   done        — 执行完成
     */
    @PostMapping(value = "/agent-post", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> agentStream(@RequestBody ChatParam chatType) throws GraphStateException {
        // ── 1. 上下文初始化 ──
        ClientUserContext.setCurrentMemoryId(chatType.getMemoryId());
        ClientUserContext.setCurrentKids(chatType.getKnowledgeIds());
        ClientUserContext.setCurrentUserId(String.valueOf(chatType.getUserId()));
        ToolCallInterceptor.beginRequest(chatType.getMemoryId());

        var config = RunnableConfig.builder()
                .threadId(chatType.getMemoryId())
                .build();
        UserMessage userMessage = UserMessage.from(chatType.getPrompt());
        AsyncGenerator.Cancellable<NodeOutput<State>> messages = agent.stream(
                Map.of("messages", userMessage), config);

        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();

        // token 流 sink：CoderAgent onPartialResponse 逐字推入，前端打字机渲染
        Sinks.Many<String> tokenSink = Sinks.many().unicast().onBackpressureBuffer();
        TokenStreamContext.register(chatType.getMemoryId(), tokenSink);
        var thread = new Thread(() -> {
            try {
                final String[] lastText = {""};
                String prevNode = null;
                State finalState = null;

                for (NodeOutput<State> output : messages) {
                    var nodeName = output.node();

                    if (!nodeName.equals(prevNode)) {
                        sink.tryEmitNext(ServerSentEvent.<String>builder()
                                .event("node_enter").data(nodeName).build());
                        prevNode = nodeName;
                    }

                    var state = output.state();
                    finalState = state;
                    state.lastMessage().ifPresent(msg -> {
                        var text = switch (msg.type()) {
                            case AI -> ((AiMessage) msg).text();
                            default -> "";
                        };
                        if (!text.isEmpty() && !text.equals(lastText[0])) {
                            sink.tryEmitNext(ServerSentEvent.<String>builder()
                                    .event("message").data(text).build());
                            lastText[0] = text;
                        }
                    });
                }

                // ── 2. 元数据：取最后一轮 AI 回答做分类 ──
                var finalAnswer = lastText[0];
                if (finalAnswer != null && !finalAnswer.isEmpty()) {
                    var userPrompt = chatType.getPrompt();
                    ChatStreamEvent classifyResult = messageTypeClassifier.classify(finalAnswer, userPrompt);

                    List<ChatStreamEvent.RagChunk> ragChunks = ClientUserContext.getRagChunks(chatType.getMemoryId());
                    List<ToolCallEvent> toolCalls = ToolCallInterceptor.endRequest(chatType.getMemoryId());

                    if (toolCalls != null && !toolCalls.isEmpty()) {
                        classifyResult.setToolCalls(toolCalls);
                    }
                    if (ragChunks != null && !ragChunks.isEmpty()) {
                        classifyResult.setRagChunks(ragChunks);
                    }

                    var metaJson = objectMapper.writeValueAsString(classifyResult);
                    sink.tryEmitNext(ServerSentEvent.<String>builder()
                            .event("meta").data(metaJson).build());
                    log.info("Agent meta: type={}", classifyResult.getType());

                    // ── 3. 画像提取 fire-and-forget ──
                    Long userId = chatType.getUserId();
                    if (userId != null && userId > 0) {
                        final Long uid = userId;
                        final String mid = chatType.getMemoryId();
                        final String up = userPrompt;
                        final String resp = finalAnswer;
                        Mono.fromRunnable(() -> {
                            try {
                                profileAgent.extractAndSave(uid, mid, up, resp);
                            } catch (Exception e) {
                                log.warn("画像提取异常: userId={}, error={}", uid, e.getMessage());
                            }
                        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
                    }
                }

                // ── 4. MongoDB 同步 ──
                State finalState1 = finalState;
                chatMemorySyncHook.ifPresent(hook -> hook.accept(chatType.getMemoryId(), finalState1));

                sink.tryEmitNext(ServerSentEvent.<String>builder()
                        .event("done").data("complete").build());
                sink.tryEmitComplete();
                tokenSink.tryEmitComplete();            } catch (Exception e) {
                log.error("Agent 流异常: {}", e.getMessage(), e);
                sink.tryEmitError(e);
            }
        });
        thread.setDaemon(true);
        thread.start();

        var tokenFlux = tokenSink.asFlux()
                .map(token -> ServerSentEvent.<String>builder().event("token").data(token).build());
        return Flux.merge(sink.asFlux(), tokenFlux)
                .doFinally(signalType -> {
                    TokenStreamContext.remove(chatType.getMemoryId());
                    ClientUserContext.clear();
                });    }
}
