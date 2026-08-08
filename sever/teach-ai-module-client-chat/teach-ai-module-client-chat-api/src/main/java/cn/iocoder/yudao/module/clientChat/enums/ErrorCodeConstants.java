package cn.iocoder.teach-ai.module.clientChat.enums;

import cn.iocoder.teach-ai.framework.common.exception.ErrorCode;

/**
 * Client Chat 错误码枚举类
 *
 * Client Chat 系统，使用 11-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 用户聊天 模块 11-002-000-000 ==========
    ErrorCode CHAT_NOT_EXCEPTION = new ErrorCode(11-002-000-001, "AI聊天异常");

    ErrorCode CHATMEMORYID_NOT_NULL = new ErrorCode(11-002-000-002, "聊天记录ID不能为空");

    ErrorCode CHATMESSAGE_IS_NULL = new ErrorCode(11-002-000-003, "聊天记录为空");

    ErrorCode EXERCISES_EXCEPTION = new ErrorCode(11-002-000-004, "练习题生成异常");

    ErrorCode PPT_EXPORT_EXCEPTION = new ErrorCode(11-002-000-005, "ppt记录生成异常");

    ErrorCode IMAGE_EXPORT_EXCEPTION = new ErrorCode(11-002-000-006, "图片记录生成异常");

    ErrorCode VIDEO_EXPORT_EXCEPTION = new ErrorCode(11-002-000-007, "视频记录生成异常");

}
