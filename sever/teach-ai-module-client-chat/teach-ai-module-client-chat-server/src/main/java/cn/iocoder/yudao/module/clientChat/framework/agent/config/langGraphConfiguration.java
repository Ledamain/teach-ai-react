package cn.iocoder.teach-ai.module.clientChat.framework.agent.config;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.*;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.hook.ChatMemorySyncHook;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag.RagAgent;
import cn.iocoder.teach-ai.module.clientChat.repository.ChatMemoryRepository;
import cn.iocoder.teach-ai.module.clientChat.service.chatmemory.aiService.MemoryTitleAiService;
import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.ConversionApi;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.CountRecordApi;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.dto.CountRecordDTO;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.utils.EdgeMappings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * Supervisor 父图拓扑:
 * START → supervisor → coder → reviewer → END (coder 终结)
 *                   → rag → supervisor  (检索回环)
 *                   → researcher → supervisor
 *                   → FINISH
 */
@Configuration
@Slf4j
public class langGraphConfiguration {

    @Bean
    public StateGraph<State> buildGraphNoStatic(
            CoderAgent coderAgent,
            ReSearchAgent reSearchAgent,
            SupervisorAgent supervisorAgent,
            RagAgent ragAgent,
            ChatReviewerAgent reviewerAgent) {

        try {
            return new StateGraph<>(State.SCHEMA, State.serializer())

                    .addNode("supervisor",  node_async(supervisorAgent))
                    .addNode("coder",       coderAgent)
                    .addNode("reviewer",    node_async(reviewerAgent))
                    .addSubgraph("rag",      ragAgent.getStateGraph())
                    .addNode("researcher",  node_async(reSearchAgent))

                    .addEdge(START, "supervisor")

                    .addConditionalEdges("supervisor",
                            edge_async(state -> state.next().orElse("FINISH")),
                            EdgeMappings.builder()
                                    .to("coder")
                                    .to("rag")
                                    .to("researcher")
                                    .toEND("FINISH")
                                    .build())

                    .addConditionalEdges("coder",
                            edge_async(state ->
                                    state.codeGenMode() ? "FINISH" : "reviewer"
                            ),
                            EdgeMappings.builder()
                                    .to("reviewer")
                                    .toEND("FINISH")
                                    .build())

                    .addConditionalEdges("reviewer",
                            edge_async(state ->
                                    state.isClarification() ? "FINISH"
                                    : state.reviewFeedback().filter(f -> !f.isBlank()).isPresent() ? "coder"
                                    : "supervisor"
                            ),
                            EdgeMappings.builder()
                                    .to("coder")
                                    .to("supervisor")
                                    .toEND("FINISH")
                                    .build())

                    .addEdge("rag",        "supervisor")
                    .addEdge("researcher", "supervisor");

        } catch (GraphStateException e) {
            log.error("Error building graph: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CompiledGraph<State> compiledGraph(StateGraph<State> graph, CompileConfig config)
            throws GraphStateException {
        return graph.compile(config);
    }

    @Bean
    public ChatMemorySyncHook chatMemorySyncHook(ChatMemoryRepository repo,
                                                  MemoryTitleAiService memoryTitleAiService,
                                                  CountRecordApi countRecordApi,
                                                  ConversionApi conversionApi) {
        return (memoryId, state) -> {
            try {
                log.info("开始同步 MongoDB, memoryId: {}, 消息数: {}", memoryId, state.messages().size());
                var doc = repo.findById(memoryId).orElseGet(ChatMemoryDO::new);
                doc.setMemoryId(memoryId);
                doc.setMessagesJson(ChatMessageSerializer.messagesToJson(state.messages()));

                // ── 标题 ──
                var title = doc.getMessageTitle();
                if (title == null || title.isEmpty() || "未命名会话".equals(title)) {
                    title = state.messages().stream()
                            .filter(m -> m.type() == ChatMessageType.USER)
                            .filter(m -> m instanceof UserMessage)
                            .map(m -> ((UserMessage) m).singleText())
                            .findFirst()
                            .map(firstMsg -> {
                                try {
                                    var t = memoryTitleAiService.generateTitle(firstMsg);
                                    if (t != null) {
                                        t = t.replace("\"", "").replace("'", "")
                                                .replace("。", "").replace("，", "").trim();
                                        if (t.length() > 30) t = t.substring(0, 30);
                                    }
                                    return t != null && !t.isEmpty() ? t : "新对话";
                                } catch (Exception e) {
                                    log.warn("标题生成失败, 降级截断: {}", e.getMessage());
                                    return truncateTitle(firstMsg);
                                }
                            })
                            .orElse("新对话");
                    doc.setMessageTitle(title);
                }

                doc.prePersist();
                repo.save(doc);
                log.info("MongoDB 同步成功, memoryId: {}, title: {}", memoryId, doc.getMessageTitle());

                // ── 使用次数统计 ──
                try {
                    var userIdStr = memoryId.split("_")[0].replace("用户", "");
                    var userId = Long.valueOf(userIdStr);
                    var recordDto = new CountRecordDTO().setUserId(userId);
                    var recordId = countRecordApi.createCountRecord(recordDto).getData();
                    log.debug("创建使用次数记录: {}, userId: {}", recordId, userId);
                    if (recordId != null) {
                        var result = countRecordApi.getCountRecordById(recordId).getData();
                        if (result != null) {
                            result.setRecordCount(result.getRecordCount() + 1L);
                            countRecordApi.updateCountRecord(result);
                            log.info("使用次数更新成功, userId: {}, count: {}", userId, result.getRecordCount());
                        }
                    }
                } catch (Exception e) {
                    log.warn("使用次数统计失败 (非致命): {}", e.getMessage());
                }

                // ── MySQL 会话记录同步 ──
                try {
                    var parts = memoryId.split("_");
                    var user = parts[0];
                    var userIdStr = user.replace("用户", "");
                    var conversionId = parts[1];
                    var userId = Long.valueOf(userIdStr);

                    var conversion = conversionApi.getConversionByConversionId(Long.valueOf(conversionId));
                    if (conversion.getCheckedData() == null && conversion.getCode() != 401) {
                        var dto = new ConversionDTO();
                        dto.setConversionId(conversionId);
                        dto.setCreator(user);
                        dto.setTitle(doc.getMessageTitle());
                        dto.setCreateTime(doc.getCreateTime());
                        dto.setClientUserId(userId);
                        conversionApi.createConversion(dto);
                        log.info("MySQL 会话记录创建成功, conversionId: {}", conversionId);
                    } else if (conversion.getCheckedData() != null
                            && conversion.getCheckedData().getTitle() == null) {
                        var updateDto = new ConversionDTO();
                        updateDto.setId(conversion.getCheckedData().getId());
                        updateDto.setConversionId(conversionId);
                        updateDto.setTitle(doc.getMessageTitle());
                        updateDto.setClientUserId(userId);
                        conversionApi.updateConversion(updateDto);
                        log.info("MySQL 会话标题补充更新, conversionId: {}", conversionId);
                    }
                } catch (Exception e) {
                    log.warn("MySQL 会话同步失败 (非致命): {}", e.getMessage());
                }

            } catch (Exception e) {
                log.error("MongoDB 同步失败, memoryId: {}, error: {}", memoryId, e.getMessage(), e);
            }
        };
    }

    private static String truncateTitle(String content) {
        if (content == null || content.isBlank()) return "未命名会话";
        var cleaned = content.replaceAll("\\n+", " ").replaceAll("\\s+", " ").trim();
        return cleaned.length() <= 30 ? cleaned : cleaned.substring(0, 30);
    }
}
