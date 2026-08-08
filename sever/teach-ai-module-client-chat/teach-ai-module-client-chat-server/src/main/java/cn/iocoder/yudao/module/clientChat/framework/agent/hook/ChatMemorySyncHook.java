package cn.iocoder.teach-ai.module.clientChat.framework.agent.hook;

import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;

import java.util.function.BiConsumer;

/**
 * 会话记忆同步钩子。
 * 图执行完成后调用，将 State 中的消息同步到外部存储（如 MongoDB）。
 *
 * 在 teach-ai 项目中通过 @Bean 注入实现，例如：
 * {@code
 *   @Bean
 *   public BiConsumer<String, State> chatMemorySyncHook(ChatMemoryRepository repo) {
 *       return (memoryId, state) -> {
 *           var doc = repo.findById(memoryId).orElseGet(() -> ...);
 *           doc.setMessagesJson(serialize(state.messages()));
 *           repo.save(doc);
 *       };
 *   }
 * }
 */
public interface ChatMemorySyncHook extends BiConsumer<String, State> {
}
