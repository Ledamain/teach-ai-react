/**
 * 学生工作台相关 API 接口
 * 所有接口均为预留，需要根据实际后端实现进行对接
 */

import {
  AssignmentContent,
  AssignmentSubmitRecord, ExerciseApiResponse,
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