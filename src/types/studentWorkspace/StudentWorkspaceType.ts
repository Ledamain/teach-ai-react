// 学生工作台相关类型定义

// ==================== 题目与作业详情类型 ====================

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

// 作业内容（从content JSON字符串解析）
export interface AssignmentContent {
  title: string;
  description?: string;
  totalScore: number;
  questions: Question[];
}

// 后端返回的作业原始数据结构
export interface ExerciseApiResponse {
  id: number;
  classesId: string;
  classesIdsList: string[] | null;
  repoCategoryName: string | null;
  nickname: string | null;
  repoCategoryId: number;
  teacherUserId: number;
  exerciseName: string;
  content: string; // JSON字符串，解析后为 AssignmentContent
  completed: number; // 0: 未提交, 1: 已提交
  questionCount: number;
  totalScore: number;
  startTime: number; // 时间戳
  endTime: number; // 时间戳
  status: number; // 1: 进行中, 0: 已结束
  submissionCount: number;
  totalStudents: number;
  createTime: number;
}

// 单题作答记录
export interface AnswerRecord {
  questionId: number;
  type: 'single' | 'multiple' | 'judge';
  userAnswer: string | string[];
  correctAnswer: string | string[];
  score: number;
  userScore: number;
  isCorrect: boolean;
}

// 作业提交记录（用于存储到后端）
export interface AssignmentSubmitRecord {
  examId: string;
  title: string;
  totalScore: number;
  userScore: number;
  submitStatus: 'completed' | 'in_progress' | 'not_started';
  submitTime: string;
  answers: AnswerRecord[];
}

// ==================== 学生分析数据类型 ====================

// 学生个人学习分析数据
export interface StudentAnalyticsData {
  studentId: string;
  studentName: string;
  className: string;
  totalQuestions: number;
  mainSubject: string;
  lastActive: string;
  averageScore: number;
  rankInClass: number;
  totalStudents: number;
  complexityDistribution: {
    level: string;
    count: number;
    percentage: number;
  }[];
  learningTrend: {
    date: string;
    score: number;
  }[];
  subjectDistribution: {
    subject: string;
    count: number;
    percentage: number;
  }[];
  recentAssignments: {
    id: string;
    title: string;
    score: number;
    totalScore: number;
    submitTime: string;
  }[];
  learningAdvice: string[];
}

// 学生作业列表项（只显示进行中和已结束）
export interface StudentAssignment {
  id: string;
  title: string;
  description?: string;
  startDate: string;
  dueDate: string;
  status: 'published' | 'closed'; // 学生只能看到进行中和已结束的
  totalScore: number;
  questionCount: number;
  // 学生的作答状态
  submitStatus: 'not_started' | 'in_progress' | 'completed';
  userScore?: number;
  submitTime?: string;
}

// 学生答题时的题目
export interface StudentQuestion {
  id: number;
  type: 'single' | 'multiple' | 'judge';
  title: string;
  options: string[];
  score: number;
  // 以下字段在提交后才有
  answer?: string | string[];
  analysis?: string;
  userAnswer?: string | string[];
  isCorrect?: boolean;
  userScore?: number;
}

// 学生作业详情（答题用）
export interface StudentAssignmentDetail {
  id: string;
  title: string;
  description?: string;
  totalScore: number;
  status: 'published' | 'closed';
  startDate: string;
  dueDate: string;
  questions: StudentQuestion[];
  // 已提交的话有以下字段
  submitStatus: 'not_started' | 'in_progress' | 'completed';
  userScore?: number;
  submitTime?: string;
}

// 学生提交答案
export interface StudentSubmitData {
  assignmentId: string;
  answers: {
    questionId: number;
    answer: string | string[];
  }[];
}

// 提交结果
export interface SubmitResult {
  success: boolean;
  totalScore: number;
  userScore: number;
  submitTime: string;
  questions: StudentQuestion[];
  // 完整的提交记录，用于存储到后端
  submitRecord?: AssignmentSubmitRecord;
}
