
// 作业练习类型
import dayjs, {Dayjs} from "dayjs";

export interface Assignment {
    id?: number;
    repoCategoryId: number;
    teacherUserId: number;
    classesId?: string;
    exerciseName: string;
    description?: string;
    startTime?: string;
    endTime: string;
    status?: number;
    submissionCount?: number;
    totalStudents?: number;
    content?: string; // 试卷题目
    userScore?: number;
}

// 题目类型
export interface Question {
    id: number;
    type: 'single' | 'multiple' | 'judge'; // 单选/多选/判断
    title: string;
    options: string[];
    answer: string | string[];
    score: number;
    analysis?: string; // 解析
}

// 作业详情（包含题目）
export interface AssignmentDetail {
    title: string;
    description?: string;
    totalScore: number;
    questions: Question[];
}

// 学生作答记录
export interface StudentAnswer {
    questionId: number;
    type: 'single' | 'multiple' | 'judge';
    userAnswer: string | string[];
    correctAnswer: string | string[];
    score: number;
    userScore: number;
    isCorrect: boolean;
}

// 学生作答初始数据
export interface StudentAnswerInit {
    id: number;
    exerciseId: number;
    studentUserId: number;
    transcript: string;
    completed: number;
    createTime: number;
}

// 学生作答详情
export interface StudentSubmission {
    examId: string;
    title: string;
    totalScore: number;
    userScore: number;
    submitStatus: 'completed' | 'not_submitted';
    submitTime?: string;
    answers: StudentAnswer[];
}

// 作业提交情况列表项
export interface SubmissionListItem {
    id: number;
    studentUserId: string;
    clientNum: string;
    exerciseId: number;
    studentUserName: string;
    classesName: string;
    completed: number;
    updateTime?: string;
    score?: number;
}

// 作业发布
export interface PublishData {
    repoCategoryId: number;
    id: number;
    teacherUserId: number;
    classesId: string;
    startTime: number;
    endTime: number;
    exerciseName: string;
}
