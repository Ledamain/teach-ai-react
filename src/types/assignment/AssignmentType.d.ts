
// 作业练习类型
export interface Assignment {
    id: string;
    title: string;
    description?: string;
    startDate?: string;
    dueDate: string;
    status: 'draft' | 'published' | 'closed';
    submissionCount: number;
    totalStudents: number;
    questions?: Question[]; // 试卷题目
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
    studentId: string;
    studentName: string;
    className: string;
    submitStatus: 'completed' | 'not_submitted';
    submitTime?: string;
    score?: number;
}

