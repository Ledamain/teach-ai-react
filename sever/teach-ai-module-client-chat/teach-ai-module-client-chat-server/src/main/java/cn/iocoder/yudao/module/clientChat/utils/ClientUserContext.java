package cn.iocoder.teach-ai.module.clientChat.utils;

import java.util.List;

// ========== RAG 检索结果暂存 ==========
import cn.iocoder.teach-ai.module.clientChat.service.aiService.ChatStreamEvent;
import java.util.List;


/**
 * 客户端用户上下文处理器
 *
 * @author weijiayu
 * @date 2025/4/22 23:57
 */
public class ClientUserContext {

    private static final ThreadLocal<String> currentUserId = new InheritableThreadLocal<>();  // 允许子进程继承父进程

    private static final ThreadLocal<String> currentMemoryId = new InheritableThreadLocal<>();

    private static final ThreadLocal<List<String>> currentKids = new InheritableThreadLocal<>();

    private static final ThreadLocal<String> currentFileName = new InheritableThreadLocal<>();

    private static final java.util.concurrent.ConcurrentHashMap<String, List<ChatStreamEvent.RagChunk>> RAG_CHUNKS_MAP = new java.util.concurrent.ConcurrentHashMap<>();

    public static void setRagChunks(String memoryId, List<ChatStreamEvent.RagChunk> chunks) {
        if (memoryId != null) RAG_CHUNKS_MAP.put(memoryId, chunks);
    }
    public static List<ChatStreamEvent.RagChunk> getRagChunks(String memoryId) {
        return memoryId != null ? RAG_CHUNKS_MAP.remove(memoryId) : null;
    }

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        return currentUserId.get();
    }

    /**
     * 获取当前TypeID
     */
    public static String getCurrentMemoryId() {
        return currentMemoryId.get();
    }

    /**
     * 设置当前TypeID
     */
    public static void setCurrentMemoryId(String memoryId) {
        currentMemoryId.set(memoryId);
    }

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(String userId) {
        currentUserId.set(userId);
    }

    /**
     * 获取当前kids
     */
    public static List<String> getCurrentKids() {
        return currentKids.get();
    }

    /**
     * 设置当前kids
     */
    public static void setCurrentKids(List<String> kids) {
            currentKids.set(kids);
    }

    /**
     * 获取当前文件名
     */
    public static String getCurrentFileName() {
        return currentFileName.get();
    }

    /**
     * 设置当前文件名
     */
    public static void setCurrentFileName(String fileName) {
        currentFileName.set(fileName);
    }

    /**
     * 清除当前用户 ID
     */
    public static void clear() {
        currentUserId.remove();
        currentMemoryId.remove();
        currentKids.remove();
        currentFileName.remove();
    }
}
