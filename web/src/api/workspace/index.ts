// 工作台 API 接口
// 预留 API 接口位置，实际使用时替换为真实的后端接口

import {
  Course,
  CourseGroup,
  CreateCourseParams,
  UpdateCourseParams,
  ApiResponse,
  PaginationParams,
  PaginationResponse,
} from '@/types/workspace/WorkspaceType';
import {request} from "@/utils/request";
import {
  BindPptArtifactResp, ExportPptArtifactResp,
  GetPptArtifactExportResultResp,
  InitiatePptCreationResp,
  PageResponseBO,
  TemplateBO
} from "@/types/ppt/PptType";

// TODO: 替换为实际的 API 基础路径
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || '/api';


/**
 * 获取课程组列表
 * @returns 课程组列表
 */
export const getCourseGroupList = async (): Promise<ApiResponse<CourseGroup[]>> => {
  // TODO: 替换为实际的 API 调用
  // const response = await fetch(`${API_BASE_URL}/course-groups`);
  // return response.json();

  // 模拟数据 - 实际使用时删除
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: [
          { id: '1', name: '数学基础课程' },
          { id: '2', name: '语言类课程' },
          { id: '3', name: '计算机专业课' },
          { id: '4', name: '通识教育课程' },
        ],
      });
    }, 300);
  });
};


/**
 * 删除课程
 * @param id 课程 ID
 * @returns 删除结果
 */
export const deleteCourse = async (id: string): Promise<ApiResponse<null>> => {
  // TODO: 替换为实际的 API 调用
  // const response = await fetch(`${API_BASE_URL}/courses/${id}`, {
  //   method: 'DELETE',
  // });
  // return response.json();

  // 模拟数据 - 实际使用时删除
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: null,
      });
    }, 300);
  });
};
export default {

  /**
   * 获取课程列表
   * @param params 分页参数
   * @returns 课程列表
   */
  getCourseList(teacherUserId: number | null) {
    return request.get<Course[]>('/repo-category/list',{
      params: {
        teacherUserId: teacherUserId
      }
    });
  },
};
