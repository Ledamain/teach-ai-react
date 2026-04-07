/**
 * 学生工作台相关 API 接口
 * 所有接口均为预留，需要根据实际后端实现进行对接
 */

import {
  AssignmentSubmitRecord,
  StudentAnalyticsData,
  StudentAssignment,
  StudentAssignmentDetail, StudentAssignmentParams,
  StudentSubmitData,
  SubmitResult,
} from '@/types/studentWorkspace/StudentWorkspaceType';
import {request} from "@/utils/request";
import {Course} from "@/types/workspace/WorkspaceType";
import {Question} from "@/types/assignment/AssignmentType";

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
 * 调用真实接口: GET /exercise/get-student?id={assignmentId}&studentUserId={studentId}
 * 返回的数据包含答案和解析，前端根据 completed 状态控制是否显示
 * @param assignmentId 作业ID
 * @param studentId 学生ID
 * @returns 作业详情
 */
export const getStudentAssignmentDetail = async (
    assignmentId: string,
    studentId: string
): Promise<StudentAssignmentDetail> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/exercise/get-student?id=${assignmentId}&studentUserId=${studentId}`);
  // const data: ExerciseApiResponse = await response.json();

  // 模拟后端返回的数据（格式与用户提供的一致）
  const mockApiResponse: ExerciseApiResponse = {
    id: parseInt(assignmentId) || 6,
    classesId: '_2C,_1C',
    classesIdsList: null,
    repoCategoryName: null,
    nickname: null,
    repoCategoryId: 25,
    teacherUserId: 4,
    exerciseName: '数学建模第二节练习题',
    content: JSON.stringify({
      title: '数学建模第二节练习题',
      description: '包含单选、多选、判断题，无简答题，适合前端直接渲染',
      totalScore: 100,
      questions: [
        {
          id: 1,
          type: 'single',
          title: '在数学建模中，以下哪项通常用于描述连续变化的动态系统？',
          options: ['差分方程', '微分方程', '线性规划', '整数规划'],
          answer: '微分方程',
          score: 10,
          analysis: '微分方程适用于描述状态随时间连续变化的动态系统，如人口增长、传染病传播等。'
        },
        {
          id: 2,
          type: 'single',
          title: 'Logistic 模型主要用于描述哪种现象？',
          options: ['指数增长', '资源无限增长', '受资源限制的增长', '周期性波动'],
          answer: '受资源限制的增长',
          score: 10,
          analysis: 'Logistic 模型引入环境承载力，描述在有限资源下种群或事物的增长过程。'
        },
        {
          id: 3,
          type: 'single',
          title: '在建立数学模型时，"模型假设"的主要作用是？',
          options: ['使问题更复杂', '忽略所有现实因素', '简化问题并明确适用范围', '直接得出最终答案'],
          answer: '简化问题并明确适用范围',
          score: 10,
          analysis: '合理假设可简化现实问题，同时界定模型的适用条件和边界。'
        },
        {
          id: 4,
          type: 'multiple',
          title: '以下哪些是数学建模的基本步骤？',
          options: ['模型准备', '模型假设', '求解模型', '模型检验'],
          answer: ['模型准备', '模型假设', '求解模型', '模型检验'],
          score: 20,
          analysis: '数学建模通常包括准备、假设、构建、求解、分析、检验和应用等步骤。'
        },
        {
          id: 5,
          type: 'multiple',
          title: '关于微分方程模型，下列说法正确的有？',
          options: ['可用于描述瞬时变化率', '平衡点分析有助于理解系统长期行为', '只能用于物理问题', '可通过数值方法求解'],
          answer: ['可用于描述瞬时变化率', '平衡点分析有助于理解系统长期行为', '可通过数值方法求解'],
          score: 20,
          analysis: '微分方程广泛应用于生物、经济等领域，不仅限于物理；当解析解难求时，常用数值方法（如欧拉法）求解。'
        },
        {
          id: 6,
          type: 'judge',
          title: '数学模型越复杂，其预测效果一定越好。',
          options: ['正确', '错误'],
          answer: '错误',
          score: 10,
          analysis: '过于复杂的模型可能导致过拟合、计算困难或参数难以估计，反而降低实用性和泛化能力。'
        },
        {
          id: 7,
          type: 'judge',
          title: '在Logistic增长模型中，当种群数量达到环境容纳量时，增长速率为零。',
          options: ['正确', '错误'],
          answer: '正确',
          score: 10,
          analysis: 'Logistic模型中，增长率与(1 - N/K)成正比，当N=K时，增长率为0，种群数量稳定。'
        }
      ]
    }),
    completed: 0, // 0: 未提交, 1: 已提交
    questionCount: 7,
    totalScore: 100,
    startTime: 1775318400000,
    endTime: 1775577601000,
    status: 1, // 1: 进行中, 0: 已结束
    submissionCount: 0,
    totalStudents: 7,
    createTime: 1775391827000
  };

  // 使用模拟数据（实际项目中应使用上面注释的API调用）
  const data = mockApiResponse;

  // 解析 content JSON 字符串
  const contentData: AssignmentContent = JSON.parse(data.content);

  // 转换为前端使用的格式
  const formatTimestamp = (timestamp: number): string => {
    const date = new Date(timestamp);
    return date.toISOString().replace('T', ' ').slice(0, 16);
  };

  return {
    id: String(data.id),
    title: data.exerciseName,
    description: contentData.description,
    totalScore: data.totalScore,
    status: data.status === 1 ? 'published' : 'closed',
    startDate: formatTimestamp(data.startTime),
    dueDate: formatTimestamp(data.endTime),
    submitStatus: data.completed === 1 ? 'completed' : 'not_started',
    questions: contentData.questions,
  };
};

/**
 * 解析后端返回的作业数据
 * 将 ExerciseApiResponse 转换为 StudentAssignmentDetail
 */
export const parseExerciseResponse = (data: ExerciseApiResponse): StudentAssignmentDetail => {
  const contentData: AssignmentContent = JSON.parse(data.content);

  const formatTimestamp = (timestamp: number): string => {
    const date = new Date(timestamp);
    return date.toISOString().replace('T', ' ').slice(0, 16);
  };

  return {
    id: String(data.id),
    title: data.exerciseName,
    description: contentData.description,
    totalScore: data.totalScore,
    status: data.status === 1 ? 'published' : 'closed',
    startDate: formatTimestamp(data.startTime),
    dueDate: formatTimestamp(data.endTime),
    submitStatus: data.completed === 1 ? 'completed' : 'not_started',
    questions: contentData.questions,
  };
};

/**
 * 提交学生作业答案（前端评分版本）
 * 注意：此方法不再在后端评分，评分逻辑移到前端
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @param submitRecord 完整的提交记录（包含评分结果）
 * @returns 提交结果
 */
export const submitStudentAssignment = async (
    courseId: string,
    studentId: string,
    submitRecord: AssignmentSubmitRecord
): Promise<{ success: boolean; message: string }> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignments/${submitRecord.examId}/submit`, {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(submitRecord),
  // });
  // return response.json();

  console.log('保存作业提交记录:', { courseId, studentId, submitRecord });

  // 模拟保存成功
  return {
    success: true,
    message: '作业提交成功',
  };
};

/**
 * 保存作业提交记录到后端
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @param submitRecord 完整的提交记录
 * @returns 保存结果
 */
export const saveAssignmentSubmitRecord = async (
    courseId: string,
    studentId: string,
    submitRecord: AssignmentSubmitRecord
): Promise<{ success: boolean; recordId?: string; message: string }> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignment-records`, {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(submitRecord),
  // });
  // return response.json();

  console.log('保存提交记录到后端:', { courseId, studentId, submitRecord });

  // 模拟保存成功
  return {
    success: true,
    recordId: `record-${Date.now()}`,
    message: '提交记录保存成功',
  };
};

/**
 * 获取学生作业提交记录列表
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @returns 提交记录列表
 */
export const getStudentSubmitRecords = async (
    courseId: string,
    studentId: string
): Promise<AssignmentSubmitRecord[]> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignment-records`);
  // return response.json();

  // 模拟数据
  return [];
};

/**
 * 获取单个作业提交记录详情
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @param examId 作业/考试ID
 * @returns 提交记录详情
 */
export const getSubmitRecordDetail = async (
    courseId: string,
    studentId: string,
    examId: string
): Promise<AssignmentSubmitRecord | null> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/student/${studentId}/courses/${courseId}/assignment-records/${examId}`);
  // return response.json();

  // 模拟数据
  return null;
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