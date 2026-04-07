/**
 * 课程详情相关 API 接口
 * 所有接口均为预留，需要根据实际后端实现进行对接
 */

import type { 
  Student, 
  StudentDetail,
  AnalyticsData, 
  Assignment,
  ClassInfo 
} from '@/types/workspace/CourseDetailType';
import type {
  KnowledgeFile,
  KnowledgeFolder,
} from '@/types/repo/RepoType'
import {request} from "@/utils/request";
import {Course} from "@/types/workspace/WorkspaceType";
import {
  BindPptArtifactResp,
  ExportPptArtifactResp,
  GetPptArtifactExportResultResp,
  InitiatePptCreationResp
} from "@/types/ppt/PptType";

// API 基础路径配置
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '/api';

// ==================== 数据分析相关 API ====================

/**
 * 获取课程数据分析
 * @param courseId 课程ID
 * @returns 分析数据
 */
export const getCourseAnalytics = async (courseId: string): Promise<AnalyticsData> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/analytics`);
  // return response.json();
  
  // 模拟数据
  return {
    totalQuestions: 1256,
    participantStudents: 89,
    subjectCategories: 12,
    todayQuestions: 45,
    questionTypeDistribution: [
      { type: '概念理解', count: 420, percentage: 33.4 },
      { type: '解题方法', count: 356, percentage: 28.3 },
      { type: '知识点疑问', count: 280, percentage: 22.3 },
      { type: '拓展延伸', count: 200, percentage: 15.9 },
    ],
    questionTrend: [
      { date: '03-25', count: 38 },
      { date: '03-26', count: 52 },
      { date: '03-27', count: 41 },
      { date: '03-28', count: 67 },
      { date: '03-29', count: 55 },
      { date: '03-30', count: 48 },
      { date: '03-31', count: 45 },
    ],
    activeStudents: [
      { date: '03-25', count: 25 },
      { date: '03-26', count: 32 },
      { date: '03-27', count: 28 },
      { date: '03-28', count: 41 },
      { date: '03-29', count: 35 },
      { date: '03-30', count: 30 },
      { date: '03-31', count: 28 },
    ],
    complexityDistribution: [
      { level: '简单', count: 380, percentage: 30.3 },
      { level: '中等', count: 520, percentage: 41.4 },
      { level: '困难', count: 280, percentage: 22.3 },
      { level: '复杂', count: 76, percentage: 6.0 },
    ],
    subjectDistribution: [
      { subject: '数学', count: 380, percentage: 30.3 },
      { subject: '物理', count: 280, percentage: 22.3 },
      { subject: '化学', count: 220, percentage: 17.5 },
      { subject: '英语', count: 180, percentage: 14.3 },
      { subject: '语文', count: 120, percentage: 9.6 },
      { subject: '其他', count: 76, percentage: 6.0 },
    ],
    hotKeywords: [
      { keyword: '导数', count: 89, trend: 'up' },
      { keyword: '积分', count: 76, trend: 'up' },
      { keyword: '向量', count: 65, trend: 'stable' },
      { keyword: '概率', count: 58, trend: 'down' },
      { keyword: '三角函数', count: 52, trend: 'up' },
      { keyword: '数列', count: 45, trend: 'stable' },
      { keyword: '圆锥曲线', count: 38, trend: 'up' },
      { keyword: '立体几何', count: 32, trend: 'down' },
    ],
  };
};

// ==================== 学生管理相关 API ====================


/**
 * 获取学生详情
 * @param courseId 课程ID
 * @param studentId 学生ID
 * @returns 学生详情
 */
export const getStudentDetail = async (
  courseId: string,
  studentId: string
): Promise<StudentDetail> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/students/${studentId}`);
  // return response.json();
  
  // 模拟数据
  return {
    id: studentId,
    studentId: '2024001',
    name: '张三',
    className: '高三(1)班',
    totalQuestions: 28,
    mainSubject: '数学',
    lastActive: '2小时前',
    complexityDistribution: [
      { level: '简单', count: 8, percentage: 28.6 },
      { level: '中等', count: 12, percentage: 42.8 },
      { level: '困难', count: 6, percentage: 21.4 },
      { level: '复杂', count: 2, percentage: 7.2 },
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
    learningAdvice: [
      '建议加强导数应用题的练习，特别是最值问题和切线问题',
      '可以尝试挑战更多综合性题目，提升解题思维的灵活性',
      '保持良好的提问习惯，注意总结同类型问题的解题方法',
    ],
  };
};

/**
 * 获取班级列表
 * @param courseId 课程ID
 * @returns 班级列表
 */
export const getClassList = async (courseId: string): Promise<ClassInfo[]> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/classes`);
  // return response.json();
  
  return [
    { id: '1', name: '高三(1)班', studentCount: 45 },
    { id: '2', name: '高三(2)班', studentCount: 42 },
    { id: '3', name: '高三(3)班', studentCount: 38 },
  ];
};

/**
 * 添加学生
 * @param courseId 课程ID
 * @param studentData 学生数据
 */
export const addStudent = async (
  courseId: string,
  studentData: Omit<Student, 'id' | 'joinTime' | 'questionCount'>
): Promise<Student> => {
  // TODO: 替换为实际 API 调用
  // const response = await fetch(`${API_BASE_URL}/courses/${courseId}/students`, {
  //   method: 'POST',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify(studentData),
  // });
  // return response.json();
  
  return {
    ...studentData,
    id: Date.now().toString(),
    joinTime: new Date().toISOString().split('T')[0],
    questionCount: 0,
  };
};

/**
 * 删除学生
 * @param courseId 课程ID
 * @param studentIds 学生ID数组
 */
export const deleteStudents = async (courseId: string, studentIds: string[]): Promise<void> => {
  // TODO: 替换为实际 API 调用
  // await fetch(`${API_BASE_URL}/courses/${courseId}/students`, {
  //   method: 'DELETE',
  //   headers: { 'Content-Type': 'application/json' },
  //   body: JSON.stringify({ ids: studentIds }),
  // });
  
  console.log('删除学生:', courseId, studentIds);
};


// ==================== 作业练习相关 API ====================

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

export default {

  /**
   * 获取学生列表
   * @param params 分页参数
   * @returns 课程列表
   */
  async getStudentList(courseId: number | null) {
    return request.get<Student[]>('/user-student/list-by-course',{
      params: {
        courseId: courseId
      }
    });
  },

  async getStudentListForClient(
      courseId: number | null,
      params?: {
        keyword?: string;
        classId?: string;
        page?: number;
        pageSize?: number;
      }
  ): Promise<{ list: Student[]; total: number; }> {
    const students = await this.getStudentList(courseId);
    let filteredList = [...students];

    if (params?.keyword) {
      const keyword = params.keyword.toLowerCase();
      filteredList = filteredList.filter(
          s => s.nickname.toLowerCase().includes(keyword) || s.clientNum.includes(keyword)
      );
    }

    if (params?.classId) {
      filteredList = filteredList.filter(s => s.classesName === params.classId);
    }

    return {list: filteredList, total: filteredList.length};
  },
  // 创建PPT
  initiatePptCreation(data: { taskId: string; outline: string; templateId?: string }) {
    return request.post<InitiatePptCreationResp>('/ppt/initiatePptCreation', data);
  },

  bindPptArtifact(data: { taskId: string; artifactId: string }) {
    return request.post<BindPptArtifactResp>('/ppt/bindPptArtifact', data);
  },

  exportPptArtifact(data: { artifactId: string }) {
    return request.post<ExportPptArtifactResp>('/ppt/exportPptArtifact', data);
  },

  getPptArtifactExportResult(data: { exportTaskId: string }) {
    return request.post<GetPptArtifactExportResultResp>('/ppt/getPptArtifactExportResult', data);
  },
};

