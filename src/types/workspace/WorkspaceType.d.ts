// 工作台类型定义

/**
 * 课程信息
 */
export interface Course {
    id: number;
    repoCategoryName: string;
    courseGroupName: string;
    studentCount: number;
    nickname: string
    createTime: number;
    updateTime?: number;
}

/**
 * 课程组信息
 */
export interface CourseGroup {
    id: string;
    name: string;
}

/**
 * 更新课程参数
 */
export interface UpdateCourseParams {
    id: string;
    name: string;
    groupId: string;
}

/**
 * API 响应通用结构
 */
export interface ApiResponse<T> {
    code: number;
    message: string;
    data: T;
}

/**
 * 分页参数
 */
export interface PaginationParams {
    page: number;
    pageSize: number;
}

/**
 * 分页响应
 */
export interface PaginationResponse<T> {
    list: T[];
    total: number;
    page: number;
    pageSize: number;
}
