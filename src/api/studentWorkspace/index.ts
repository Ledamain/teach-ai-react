/**
 * 学生工作台相关 API 接口
 * 所有接口均为预留，需要根据实际后端实现进行对接
 */

import type {
  StudentAnalyticsData,
  StudentAssignment,
  StudentAssignmentDetail,
  StudentSubmitData,
  SubmitResult,
} from '@/types/workspace/StudentWorkspaceType';
import type { KnowledgeFolder, KnowledgeFolderDetail } from '@/types/workspace/CourseDetailType';
import {request} from "@/utils/request";
import {Course} from "@/types/workspace/WorkspaceType";

// API 基础路径配置
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '/api';

// ==================== 学生数据分析相关 API ====================

/**
 * 获取学生个人学习分析数据
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @returns 学习分析数据
 */
export const getStudentAnalytics = async (
  courseId: number,
  studentId: string
): Promise<StudentAnalyticsData> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/analytics`);
  // return response.json();

  // 模拟数据
  return {
    studentId,
    studentName: '张三',
    className: '高三(1)班',
    totalQuestions: 56,
    mainSubject: '数学',
    lastActive: '2小时前',
    averageScore: 85.5,
    rankInClass: 8,
    totalStudents: 45,
    complexityDistribution: [
      { level: '简单', count: 18, percentage: 32.1 },
      { level: '中等', count: 22, percentage: 39.3 },
      { level: '困难', count: 12, percentage: 21.4 },
      { level: '复杂', count: 4, percentage: 7.2 },
    ],
    learningTrend: [
      { date: '03-25', score: 72 },
      { date: '03-26', score: 75 },
      { date: '03-27', score: 78 },
      { date: '03-28', score: 74 },
      { date: '03-29', score: 82 },
      { date: '03-30', score: 85 },
      { date: '03-31', score: 88 },
    ],
    subjectDistribution: [
      { subject: '数学', count: 28, percentage: 50.0 },
      { subject: '物理', count: 12, percentage: 21.4 },
      { subject: '化学', count: 10, percentage: 17.9 },
      { subject: '英语', count: 6, percentage: 10.7 },
    ],
    recentAssignments: [
      { id: '1', title: '高等数学期中测试', score: 92, totalScore: 100, submitTime: '2024-04-01' },
      { id: '2', title: '函数与极限练习', score: 88, totalScore: 100, submitTime: '2024-03-28' },
      { id: '3', title: '导数应用练习', score: 78, totalScore: 100, submitTime: '2024-03-25' },
    ],
    learningAdvice: [
      '建议加强导数应用题的练习，特别是最值问题和切线问题',
      '可以尝试挑战更多综合性题目，提升解题思维的灵活性',
      '保持良好的提问习惯，注意总结同类型问题的解题方法',
    ],
  };
};

// ==================== 学生知识库相关 API ====================


/**
 * 获取知识库文件夹详情（学生视角）
 * @param courseId 课程ID
 * @param folderId 文件夹ID
 * @param studentId 学生ID
 * @returns 文件夹详情
 */
export const getStudentKnowledgeFolderDetail = async (
  courseId: string,
  folderId: string,
  studentId: string
): Promise<KnowledgeFolderDetail> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/knowledge/folders/${folderId}`);
  // return response.json();

  // 模拟数据
  return {
    id: folderId,
    name: '数学基础',
    description: '本知识库包含高等数学基础知识点、公式汇总及练习题',
    fileCount: 6,
    enabled: true,
    createdAt: '2024-03-01',
    updatedAt: '2024-03-30',
    files: [
      { id: '1', folderId, name: '第一章-导数基础.docx', type: 'docx', size: 1024000, uploadedAt: '2024-03-25 10:30:00', url: '/files/1.docx' },
      { id: '2', folderId, name: '公式汇总.doc', type: 'doc', size: 512000, uploadedAt: '2024-03-26 14:20:00', url: '/files/2.doc' },
      { id: '3', folderId, name: '课程讲义.pptx', type: 'pptx', size: 2048000, uploadedAt: '2024-03-27 09:15:00', url: '/files/3.pptx' },
      { id: '4', folderId, name: '练习题.ppt', type: 'ppt', size: 1536000, uploadedAt: '2024-03-28 16:45:00', url: '/files/4.ppt' },
      { id: '5', folderId, name: '学习笔记.txt', type: 'txt', size: 10240, uploadedAt: '2024-03-29 11:00:00', url: '/files/5.txt' },
      { id: '6', folderId, name: '重点总结.md', type: 'md', size: 20480, uploadedAt: '2024-03-30 08:30:00', url: '/files/6.md' },
    ],
  };
};

// ==================== 学生作业相关 API ====================

/**
 * 获取学生作业列表（只包含进行中和已结束的作业）
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @returns 作业列表
 */
export const getStudentAssignmentList = async (
  courseId: string,
  studentId: string
): Promise<StudentAssignment[]> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignments`);
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
      totalScore: 100,
      questionCount: 7,
      submitStatus: 'completed',
      userScore: 92,
      submitTime: '2024-04-01 10:45',
    },
    {
      id: '2',
      title: '函数与极限练习',
      description: '第一章课后练习题',
      startDate: '2024-04-05 14:00',
      dueDate: '2024-04-07 23:59',
      status: 'published',
      totalScore: 100,
      questionCount: 7,
      submitStatus: 'not_started',
    },
    {
      id: '3',
      title: '微分方程专项训练',
      description: '第三章专项练习',
      startDate: '2024-04-02 08:00',
      dueDate: '2024-04-06 23:59',
      status: 'published',
      totalScore: 100,
      questionCount: 10,
      submitStatus: 'in_progress',
    },
    {
      id: '4',
      title: '积分基础练习',
      description: '第二章基础练习',
      startDate: '2024-03-28 08:00',
      dueDate: '2024-03-30 23:59',
      status: 'closed',
      totalScore: 100,
      questionCount: 8,
      submitStatus: 'completed',
      userScore: 85,
      submitTime: '2024-03-30 20:15',
    },
  ];
};

/**
 * 获取学生作业详情（用于答题或查看结果）
 * @param courseId 课程ID
 * @param assignmentId 作业ID
 * @param studentId 学生ID
 * @returns 作业详情
 */
export const getStudentAssignmentDetail = async (
  courseId: string,
  assignmentId: string,
  studentId: string
): Promise<StudentAssignmentDetail> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignments/${assignmentId}`);
  // return response.json();

  // 模拟数据 - 根据作业状态返回不同数据
  const baseQuestions = [
    {
      id: 1,
      type: 'single' as const,
      title: '下列哪个是 JavaScript 的原始数据类型？',
      options: ['Array', 'Object', 'String', 'Function'],
      score: 10,
    },
    {
      id: 2,
      type: 'single' as const,
      title: 'HTML 中用于定义段落的标签是？',
      options: ['<div>', '<p>', '<span>', '<section>'],
      score: 10,
    },
    {
      id: 3,
      type: 'single' as const,
      title: 'CSS 中哪个属性可以改变文字颜色？',
      options: ['text-color', 'font-color', 'color', 'background'],
      score: 10,
    },
    {
      id: 4,
      type: 'multiple' as const,
      title: '下列属于 CSS 盒模型组成部分的有？',
      options: ['margin', 'padding', 'border', 'content'],
      score: 20,
    },
    {
      id: 5,
      type: 'multiple' as const,
      title: '下列哪些是 HTTP 请求方法？',
      options: ['GET', 'POST', 'FETCH', 'DELETE'],
      score: 20,
    },
    {
      id: 6,
      type: 'judge' as const,
      title: '在 JavaScript 中，null 和 undefined 是完全相同的。',
      options: ['正确', '错误'],
      score: 10,
    },
    {
      id: 7,
      type: 'judge' as const,
      title: 'CSS 中 display: none 会使元素脱离文档流。',
      options: ['正确', '错误'],
      score: 10,
    },
  ];

  // 模拟已完成的作业（包含答案和用户作答）
  if (assignmentId === '1' || assignmentId === '4') {
    return {
      id: assignmentId,
      title: assignmentId === '1' ? '高等数学期中测试' : '积分基础练习',
      description: assignmentId === '1' ? '涵盖导数、积分基础内容' : '第二章基础练习',
      totalScore: 100,
      status: 'closed',
      startDate: '2024-04-01 09:00',
      dueDate: '2024-04-01 11:00',
      submitStatus: 'completed',
      userScore: assignmentId === '1' ? 92 : 85,
      submitTime: assignmentId === '1' ? '2024-04-01 10:45' : '2024-03-30 20:15',
      questions: baseQuestions.map((q, idx) => ({
        ...q,
        answer: idx === 0 ? 'String' :
               idx === 1 ? '<p>' :
               idx === 2 ? 'color' :
               idx === 3 ? ['margin', 'padding', 'border', 'content'] :
               idx === 4 ? ['GET', 'POST', 'DELETE'] :
               idx === 5 ? '错误' : '正确',
        analysis: idx === 0 ? 'String 是基本数据类型，其余均为引用类型。' :
                 idx === 1 ? '<p> 标签专门用于表示文本段落。' :
                 idx === 2 ? 'CSS 使用 color 属性设置文字颜色。' :
                 idx === 3 ? '标准盒模型包含内容、内边距、边框、外边距。' :
                 idx === 4 ? 'FETCH 是浏览器 API，并非 HTTP 请求方法。' :
                 idx === 5 ? 'null 表示空对象，undefined 表示未定义，二者类型不同。' :
                 'display: none 不占据空间，visibility: hidden 仍占据空间。',
        userAnswer: idx === 0 ? 'String' :
                   idx === 1 ? '<p>' :
                   idx === 2 ? 'color' :
                   idx === 3 ? ['margin', 'padding', 'border', 'content'] :
                   idx === 4 ? ['GET', 'POST', 'FETCH'] : // 故意答错
                   idx === 5 ? '错误' : '正确',
        isCorrect: idx !== 4, // 第5题答错
        userScore: idx === 4 ? 0 : q.score,
      })),
    };
  }

  // 进行中的作业（不包含答案）
  return {
    id: assignmentId,
    title: '函数与极限练习',
    description: '第一章课后练习题',
    totalScore: 100,
    status: 'published',
    startDate: '2024-04-05 14:00',
    dueDate: '2024-04-07 23:59',
    submitStatus: 'not_started',
    questions: baseQuestions,
  };
};

/**
 * 提交学生作业答案
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @param submitData 提交数据
 * @returns 提交结果
 */
export const submitStudentAssignment = async (
  courseId: string,
  studentId: string,
  submitData: StudentSubmitData
): Promise<SubmitResult> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignments/${submitData.assignmentId}/submit`, {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(submitData),
  // });
  // return response.json();

  console.log('提交作业:', { courseId, studentId, submitData });

  // 模拟评分结果
  const correctAnswers: Record<number, string | string[]> = {
    1: 'String',
    2: '<p>',
    3: 'color',
    4: ['margin', 'padding', 'border', 'content'],
    5: ['GET', 'POST', 'DELETE'],
    6: '错误',
    7: '正确',
  };

  const scores: Record<number, number> = {
    1: 10, 2: 10, 3: 10, 4: 20, 5: 20, 6: 10, 7: 10,
  };

  let userScore = 0;
  const questions = submitData.answers.map(a => {
    const correctAnswer = correctAnswers[a.questionId];
    const isCorrect = Array.isArray(correctAnswer)
      ? Array.isArray(a.answer) &&
        correctAnswer.length === a.answer.length &&
        correctAnswer.every(c => a.answer.includes(c))
      : a.answer === correctAnswer;
    
    const questionScore = isCorrect ? scores[a.questionId] : 0;
    userScore += questionScore;

    return {
      id: a.questionId,
      type: a.questionId <= 3 ? 'single' as const :
            a.questionId <= 5 ? 'multiple' as const : 'judge' as const,
      title: '',
      options: [],
      score: scores[a.questionId],
      answer: correctAnswer,
      analysis: '',
      userAnswer: a.answer,
      isCorrect,
      userScore: questionScore,
    };
  });

  return {
    success: true,
    totalScore: 100,
    userScore,
    submitTime: new Date().toISOString(),
    questions,
  };
};

export default {

  /**
   * 获取课程列表
   * @param params 分页参数
   * @returns 课程列表
   */
  getCourseList(studentUserId: number | null) {
    return request.get<Course[]>('/repo-category/list-by-student-id', {
      params: {
        studentUserId: studentUserId
      }
    });
  },
}