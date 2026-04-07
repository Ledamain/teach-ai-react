
// ==================== 作业练习相关 API ====================

import {
    Assignment,
    AssignmentDetail,
    PublishData,
    StudentSubmission,
    SubmissionListItem
} from "@/types/assignment/AssignmentType";
import {request} from "@/utils/request";
import {CLassesType} from "@/types/classes/ClassesType";
import {
    StudentAssignment,
    StudentAssignmentDetail,
    StudentAssignmentParams,
    StudentSubmitData,
    SubmitResult
} from "@/types/studentWorkspace/StudentWorkspaceType";
import {getStudentAssignmentDetail} from "@/api/studentWorkspace";
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
}
export default {
    /**
     * 创建作业
     * @param courseId 课程ID
     * @param assignmentData 作业数据
     */
    createAssignment(data: Omit<Assignment, 'id' | 'submissionCount'>) {
        return request.post<number>('/exercise/create',data);
    },
    /**
     * 获取作业列表
     * @param courseId 课程ID
     */
    getAssignmentList(repoCategoryId: number,teacherUserId: number){
        return request.get<Assignment[]>('/exercise/list',{
            params: {
                repoCategoryId: repoCategoryId,
                teacherUserId: teacherUserId
            }
        });
    },
    /**
     * 发布作业
     * @param courseId 课程ID
     * @param assignmentId 作业ID
     * @param publishData 发布参数
     */
    publishAssignment(data: PublishData) {
        return request.post<number>('/exercise/post',data);
    },
    /**
     * 获取作业详情
     * @param courseId 课程ID
     */
    getAssignmentDetail(id: number){
        return request.get<Assignment>('/exercise/get',{
            params: {
                id: id
            }
        });
    },

    /**
     * 获取作业提交情况列表
     * @param courseId 课程ID
     * @param assignmentId 作业ID
     */
    getSubmissionList(exerciseId: number){
        return request.get<SubmissionListItem[]>('/exercise/list-result',{
            params: {
                exerciseId: exerciseId,
            }
        });
    },
    /**
     * 获取学生作业列表（只包含进行中和已结束的作业）
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @returns 作业列表
     */
    getStudentAssignmentList(repoCategoryId: number,studentUserId:number){
        return request.get<Assignment[]>('/exercise/student/list',{
            params: {
                repoCategoryId: repoCategoryId,
                studentUserId: studentUserId
            }
        })
    },
    getStudentAssignmentDetail(assignmentId: number, studentUserId: number){
        if (assignmentId === 1 || assignmentId === 4) {

        }else {
            return request.get<StudentAssignment>('/exercise/get-student',{
                params: {
                    id: assignmentId,
                    studentUserId: studentUserId
                }
            });
        }
    }

}