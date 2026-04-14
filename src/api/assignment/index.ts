// ==================== 作业练习相关 API ====================
import {
    Assignment,
    AssignmentDetail,
    PublishData, StudentAnswerInit,
    StudentSubmission,
    SubmissionListItem
} from "@/types/assignment/AssignmentType";
import {request} from "@/utils/request";
import {
    AssignmentContent,
    ExerciseApiResponse,
    StudentAssignmentDetail,
} from "@/types/studentWorkspace/StudentWorkspaceType";
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
        return request.get<ExerciseApiResponse[]>('/exercise/student/list',{
            params: {
                repoCategoryId: repoCategoryId,
                studentUserId: studentUserId
            }
        })
    },
    getStudentAssignment(assignmentId: number, studentUserId: number) {
        return request.get<ExerciseApiResponse>('/exercise/get-student', {
            params: {
                id: assignmentId,
                studentUserId: studentUserId
            }
        })
    },
    /**
     * 获取学生作业详情
     * @param courseId 课程ID
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @returns 作业详情
     */
    async getStudentAssignmentDetail(
        assignmentId: string,
        studentId: string
    ): Promise<StudentAssignmentDetail> {
        try {
            const data = await this.getStudentAssignment(
                Number(assignmentId),
                Number(studentId)
            );

            // 安全解析 JSON
            const contentData: AssignmentContent = data.content
                ? JSON.parse(data.content)
                : { description: '', questions: [] };

            // 普通函数
            function formatTimestamp(timestamp?: number): string {
                if (!timestamp) return '';
                const date = new Date(timestamp);
                if (isNaN(date.getTime())) return '';
                return date.toISOString().replace('T', ' ').slice(0, 16);
            }

            return {
                id: data.id,
                title: data.exerciseName,
                description: contentData.description,
                totalScore: data.totalScore,
                status: data.status === 1 ? 'published' : 'closed',
                startDate: formatTimestamp(data.startTime),
                dueDate: formatTimestamp(data.endTime),
                submitStatus: data.completed === 1 ? 'completed' : 'not_started',
                questions: contentData.questions || [],
                userScore: data.userScore,
            };
        } catch (err) {
            console.error('获取学生作业详情失败：', err);
            throw err;
        }
    },
    /**
     * 保存作业提交记录到后端
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param submitRecord 完整的提交记录
     * @returns 保存结果
     */
    saveAssignmentSubmitRecord(exerciseId: number, studentUserId: number, submitRecord: string){
        return request.put<boolean>('/exercise/student/update-result',{
            id: Number(exerciseId),
            studentUserId: studentUserId,
            transcript: submitRecord
        });
    },
    /**
     * 获取学生作答详情
     * @param courseId 课程ID
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     */
    async getStudentSubmission(assignmentResultId: number){
        const init: StudentAnswerInit = await request.get<StudentAnswerInit>('/exercise/get-result', {
            params: {
                id: assignmentResultId
            }
        });
        const submit: StudentSubmission = JSON.parse(init.transcript);
        return submit;
    }
}