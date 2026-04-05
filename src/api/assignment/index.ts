
// ==================== 作业练习相关 API ====================

import {Assignment, AssignmentDetail, StudentSubmission, SubmissionListItem} from "@/types/assignment/AssignmentType";
import {request} from "@/utils/request";
import {CLassesType} from "@/types/classes/ClassesType";

/**
 * 获取作业列表
 * @param courseId 课程ID
 */
export const getAssignmentList = async (courseId: string): Promise<Assignment[]> => {
    // TODO: 替换为实际 API 调用
    // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/assignments`);
    // return response.json();

    // 模拟数据
    return [
        {
            id: '1',
            title: '高等数学期中测试',
            description: '涵盖导数、积分基础内容',
            startDate: '2024-04-01 09:00',
            dueDate: '2024-04-01 11:00',
            status: 'closed',
            submissionCount: 42,
            totalStudents: 45,
        },
        {
            id: '2',
            title: '函数与极限练习',
            description: '第一章课后练习题',
            startDate: '2024-04-05 14:00',
            dueDate: '2024-04-07 23:59',
            status: 'published',
            submissionCount: 28,
            totalStudents: 45,
        },
        {
            id: '3',
            title: '微分方程专项训练',
            description: undefined,
            startDate: '2024-04-10 08:00',
            dueDate: '2024-04-15 23:59',
            status: 'draft',
            submissionCount: 0,
            totalStudents: 45,
        },
    ];
};

/**
 * 创建作业
 * @param courseId 课程ID
 * @param assignmentData 作业数据
 */
export const createAssignment = async (
    courseId: string,
    assignmentData: Omit<Assignment, 'id' | 'submissionCount'>
): Promise<Assignment> => {
    // TODO: 替换为实际 API 调用
    // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/assignments`, {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(assignmentData),
    // });
    // return response.json();

    return {
        ...assignmentData,
        id: Date.now().toString(),
        submissionCount: 0,
    };
};

/**
 * 更新作业
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 * @param assignmentData 作业数据
 */
export const updateAssignment = async (
    courseId: string,
    assignmentId: string,
    assignmentData: Partial<Assignment>
): Promise<Assignment> => {
    // TODO: 替换为实际 API 调用
    // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/assignments/${assignmentId}`, {
    //   method: 'PATCH',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(assignmentData),
    // });
    // return response.json();

    console.log('更新作业:', courseId, assignmentId, assignmentData);
    return { ...assignmentData, id: assignmentId } as Assignment;
};

/**
 * 删除作业
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 */
export const deleteAssignment = async (courseId: string, assignmentId: string): Promise<void> => {
    // TODO: 替换为实际 API 调用
    // await fetch(`${API_BASE_URL}/courses/${courseId}/assignments/${assignmentId}`, {
    //   method: 'DELETE',
    // });

    console.log('删除作业:', courseId, assignmentId);
};

// ==================== 作业详情与作答相关 API ====================

/**
 * 获取作业详情（含题目）
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 */
export const getAssignmentDetail = async (
    courseId: string,
    assignmentId: string
): Promise<AssignmentDetail> => {
    // TODO: 替换为实际 API 调用
    // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/assignments/${assignmentId}/detail`);
    // return response.json();

    // 模拟数据 - 使用用户提供的题目格式
    return {
        title: '前端基础综合测验',
        description: '包含单选、多选、判断题，无简答题，适合前端直接渲染',
        totalScore: 100,
        questions: [
            {
                id: 1,
                type: 'single',
                title: '下列哪个是 JavaScript 的原始数据类型？',
                options: ['Array', 'Object', 'String', 'Function'],
                answer: 'String',
                score: 10,
                analysis: 'String 是基本数据类型，其余均为引用类型。'
            },
            {
                id: 2,
                type: 'single',
                title: 'HTML 中用于定义段落的标签是？',
                options: ['<div>', '<p>', '<span>', '<section>'],
                answer: '<p>',
                score: 10,
                analysis: '<p> 标签专门用于表示文本段落。'
            },
            {
                id: 3,
                type: 'single',
                title: 'CSS 中哪个属性可以改变文字颜色？',
                options: ['text-color', 'font-color', 'color', 'background'],
                answer: 'color',
                score: 10,
                analysis: 'CSS 使用 color 属性设置文字颜色。'
            },
            {
                id: 4,
                type: 'multiple',
                title: '下列属于 CSS 盒模型组成部分的有？',
                options: ['margin', 'padding', 'border', 'content'],
                answer: ['margin', 'padding', 'border', 'content'],
                score: 20,
                analysis: '标准盒模型包含内容、内边距、边框、外边距。'
            },
            {
                id: 5,
                type: 'multiple',
                title: '下列哪些是 HTTP 请求方法？',
                options: ['GET', 'POST', 'FETCH', 'DELETE'],
                answer: ['GET', 'POST', 'DELETE'],
                score: 20,
                analysis: 'FETCH 是浏览器 API，并非 HTTP 请求方法。'
            },
            {
                id: 6,
                type: 'judge',
                title: '在 JavaScript 中，null 和 undefined 是完全相同的。',
                options: ['正确', '错误'],
                answer: '错误',
                score: 10,
                analysis: 'null 表示空对象，undefined 表示未定义，二者类型不同。'
            },
            {
                id: 7,
                type: 'judge',
                title: 'CSS 中 display: none 会使元素脱离文档流。',
                options: ['正确', '错误'],
                answer: '正确',
                score: 10,
                analysis: 'display: none 不占据空间，visibility: hidden 仍占据空间。'
            }
        ]
    };
};

/**
 * 发布作业
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 * @param publishData 发布参数
 */
export const publishAssignment = async (
    courseId: string,
    assignmentId: string,
    publishData: {
        classIds: string[];
        startTime: string;
        endTime: string;
    }
): Promise<void> => {
    // TODO: 替换为实际 API 调用
    // await fetch(`${API_BASE_URL}/courses/${courseId}/assignments/${assignmentId}/publish`, {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(publishData),
    // });

    console.log('发布作业:', courseId, assignmentId, publishData);
};

/**
 * 获取作业提交情况列表
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 */
export const getSubmissionList = async (
    courseId: string,
    assignmentId: string
): Promise<SubmissionListItem[]> => {
    // TODO: 替换为实际 API 调用
    // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/assignments/${assignmentId}/submissions`);
    // return response.json();

    // 模拟数据
    return [
        { studentId: '2024001', studentName: '张三', className: '高三(1)班', submitStatus: 'completed', submitTime: '2024-04-04 15:30:22', score: 80 },
        { studentId: '2024002', studentName: '李四', className: '高三(1)班', submitStatus: 'completed', submitTime: '2024-04-04 14:45:10', score: 95 },
        { studentId: '2024003', studentName: '王五', className: '高三(2)班', submitStatus: 'not_submitted' },
        { studentId: '2024004', studentName: '赵六', className: '高三(2)班', submitStatus: 'completed', submitTime: '2024-04-04 16:20:33', score: 70 },
        { studentId: '2024005', studentName: '钱七', className: '高三(3)班', submitStatus: 'not_submitted' },
        { studentId: '2024006', studentName: '孙八', className: '高三(3)班', submitStatus: 'completed', submitTime: '2024-04-04 15:55:18', score: 85 },
    ];
};

/**
 * 获取学生作答详情
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 * @param studentId 学生ID
 */
export const getStudentSubmission = async (
    courseId: string,
    assignmentId: string,
    studentId: string
): Promise<StudentSubmission> => {
    // TODO: 替换为实际 API 调用
    // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/assignments/${assignmentId}/submissions/${studentId}`);
    // return response.json();

    // 模拟数据 - 使用用户提供的作答格式
    return {
        examId: 'frontend-basic-test-001',
        title: '前端基础综合测验',
        totalScore: 100,
        userScore: 80,
        submitStatus: 'completed',
        submitTime: '2026-04-04 15:30:22',
        answers: [
            { questionId: 1, type: 'single', userAnswer: 'String', correctAnswer: 'String', score: 10, userScore: 10, isCorrect: true },
            { questionId: 2, type: 'single', userAnswer: '<div>', correctAnswer: '<p>', score: 10, userScore: 0, isCorrect: false },
            { questionId: 3, type: 'single', userAnswer: 'color', correctAnswer: 'color', score: 10, userScore: 10, isCorrect: true },
            { questionId: 4, type: 'multiple', userAnswer: ['margin', 'padding', 'border'], correctAnswer: ['margin', 'padding', 'border', 'content'], score: 20, userScore: 15, isCorrect: false },
            { questionId: 5, type: 'multiple', userAnswer: ['GET', 'POST', 'DELETE'], correctAnswer: ['GET', 'POST', 'DELETE'], score: 20, userScore: 20, isCorrect: true },
            { questionId: 6, type: 'judge', userAnswer: '错误', correctAnswer: '错误', score: 10, userScore: 10, isCorrect: true },
            { questionId: 7, type: 'judge', userAnswer: '错误', correctAnswer: '正确', score: 10, userScore: 0, isCorrect: false },
        ]
    };
};

export default {
    /**
     * 创建作业
     * @param courseId 课程ID
     * @param assignmentData 作业数据
     */
    createAssignment(data: Omit<Assignment, 'id' | 'submissionCount'>) {
        return request.post<CLassesType[]>('/classes/list-by-repo-category',data);
    },
}