package cn.iocoder.teach-ai.module.clientSystem.controller.client.utils;

/**
 * 客户端用户上下文处理器
 *
 * @author weijiayu
 * @date 2025/4/22 23:57
 */
public class ClientUserContext {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    private static final ThreadLocal<String> currentType = new ThreadLocal<>();

    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        return currentUser.get();
    }

    /**
     * 获取当前TypeID
     */
    public static String getCurrentTypeId() {
        return currentType.get();
    }

    /**
     * 设置当前TypeID
     */
    public static void setCurrentTypeId(String typeId) {
        currentType.set(typeId);
    }

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(String userId) {
        currentUser.set(userId);
    }

    /**
     * 获取当前uRole
     */
    public static String getCurrentRole() {
        return currentRole.get();
    }

    /**
     * 设置当前uRole
     */
    public static void setCurrentRole(String uRole) {
        currentRole.set(uRole);
    }

    /**
     * 清除当前用户 ID
     */
    public static void clear() {
        currentUser.remove();
        currentType.remove();
        currentRole.remove();
    }
}
