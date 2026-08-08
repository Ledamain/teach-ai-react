package cn.iocoder.teach-ai.module.clientSystem.enums.client;

import cn.iocoder.teach-ai.framework.common.exception.ErrorCode;

/**
 * Client System 错误码枚举类
 *
 * Client system 系统，使用 10-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 用户管理 模块 10-002-000-000 ==========
    ErrorCode USER_NOT_EXISTS = new ErrorCode(10-002-000-001, "客户端用户不存在");

    ErrorCode USERNAME_NOT_NULL = new ErrorCode(10-002-000-002, "客户端用户未填写用户名");

    ErrorCode PASSWORD_NOT_NULL = new ErrorCode(10-002-000-003, "客户端用户未填写密码");

    ErrorCode NICKNAME_NOT_NULL = new ErrorCode(10-002-000-004, "客户端用户未填写昵称");

    ErrorCode USERNAME_HAS_EXIST = new ErrorCode(10-002-000-005, "客户端用户名已存在");

    ErrorCode NICKNAME_HAS_EXIST = new ErrorCode(10-002-000-006, "客户端昵称已存在");

    // ========== 会话历史 模块 10-003-000-000 ==========
    ErrorCode CONVERSION_NOT_EXISTS = new ErrorCode(10-003-000-001, "会话历史不存在");

    // ========== 使用记录 模块 10-004-000-000 ==========
    ErrorCode COUNT_RECORD_NOT_EXISTS = new ErrorCode(10-004-000-001, "使用次数记录不存在");

    // ========== 知识库 模块 10-005-000-000 ==========
    ErrorCode REPO_NOT_EXISTS = new ErrorCode(10-005-000-001, "知识库不存在");

    // ========== 知识库类别 模块 10-006-000-000 ==========
    ErrorCode REPO_CATEGORY_NOT_EXISTS = new ErrorCode(10-006-000-001, "知识库类别不存在");

    // ========== 班级 模块 10-007-000-000 ==========
    ErrorCode CLASSES_NOT_EXISTS = new ErrorCode(10-007-000-001, "班级不存在");
    ErrorCode CLASSES_STUDENTS_NOT_EXISTS = new ErrorCode(10-007-001-001, "班级学生不存在");

    // ========== PPT历史记录 模块 11-001-000-000 ==========
    ErrorCode PPT_HISTORY_NOT_EXISTS = new ErrorCode(11-001-000-001, "PPT历史记录不存在");

    // ========== 课程文件夹 模块 11-002-000-000 ==========
    ErrorCode REPO_GROUP_NOT_EXISTS = new ErrorCode(11-002-000-001, "课程文件夹不存在");

    // ========== 课程组 模块 11-003-000-000 ==========
    ErrorCode COURSE_GROUP_NOT_EXISTS = new ErrorCode(11-003-000-001, "课程组不存在");

    // ========== 练习题 模块 11-004-000-000 ==========
    ErrorCode EXERCISE_INFO_NOT_EXISTS = new ErrorCode(11-004-000-001, "练习题不存在");

    // ========== 评判结果 模块 11-005-000-000 ==========
    ErrorCode EXERCISE_RESULT_NOT_EXISTS = new ErrorCode(11-005-000-001, "评判结果不存在");

    // ========== 客户端登录日志 模块 11-006-000-000 ==========
    ErrorCode LOGIN_LOG_NOT_EXISTS = new ErrorCode(11-006-000-001, "客户端登录日志不存在");
    // ========== 学生画像 模块 11-007-000-000 ==========
    ErrorCode STUDENT_PROFILE_NOT_EXISTS = new ErrorCode(11-007-000-001, "学生画像不存在");
}
