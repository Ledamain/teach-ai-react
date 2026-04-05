// 学生工作台相关类型定义

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
}
