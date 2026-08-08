package cn.iocoder.teach-ai.module.clientSystem.enums.admin;

import cn.iocoder.teach-ai.framework.common.exception.ErrorCode;

/**
 * Client System 错误码枚举类
 *
 * Client system 系统，使用 9-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 用户管理 模块 9-002-000-000 ==========
    ErrorCode USER_NOT_EXISTS = new ErrorCode(9-002-000-001, "客户端用户不存在");

    // ========== 系统提示词
    ErrorCode SYSTEM_MESSAGE_NOT_EXISTS = new ErrorCode(9-002-000-002, "系统提示词不存在");

    // ========== 系统提示词
    ErrorCode SYSTEM_MESSAGE_TITLE_NOT_NULL = new ErrorCode(9-002-000-003, "系统提示词标题不可为空");

    // ========== 系统提示词
    ErrorCode SYSTEM_MESSAGE_CONTENT_ONLY_ONE_TYPE = new ErrorCode(9-002-000-004, "系统提示词内容类型只可有一种");

    // ========== 系统提示词
    ErrorCode SYSTEM_MESSAGE_CONTENT_NOT_NULL = new ErrorCode(9-002-000-004, "系统提示词内容不能为空");

    // ========== 知识库切分失败
    ErrorCode KNOWLEDGE_SEGMENTATION_FAILURE = new ErrorCode(9-002-000-005, "知识库切分失败");
}

