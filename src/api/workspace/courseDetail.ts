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
    participantStudents: 87,
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
      { subject: '深度学习', count: 380, percentage: 30.3 },
      { subject: '人工智能基础', count: 280, percentage: 22.3 },
      { subject: '数据分析与挖掘', count: 220, percentage: 17.5 },
      { subject: '卷积神经网络', count: 180, percentage: 14.3 },
      { subject: 'JAVA EE', count: 120, percentage: 9.6 },
      { subject: 'Python基础', count: 76, percentage: 6.0 },
    ],
    hotKeywords: [
      { keyword: '卷积', count: 89, trend: 'up' },
      { keyword: '向量', count: 76, trend: 'up' },
      { keyword: '池化', count: 65, trend: 'stable' },
      { keyword: '样式', count: 58, trend: 'down' },
      { keyword: '贪心算法', count: 52, trend: 'up' },
      { keyword: '抽象特征', count: 45, trend: 'stable' },
      { keyword: '边缘检测', count: 38, trend: 'up' },
      { keyword: '过滤器', count: 32, trend: 'down' },
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
    name: '秦浩轩',
    className: '高三(1)班',
    totalQuestions: 28,
    mainSubject: '深度学习',
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
      '先吃透基础数学与核心原理，不要急于堆项目',
      '动手复现经典模型，用小数据集练手',
      '坚持读论文 + 做笔记，形成自己的知识体系',
    ],
  };
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

