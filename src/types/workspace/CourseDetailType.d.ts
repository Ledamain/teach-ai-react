// 学生类型
export interface Student {
  studentUserId: number;
  clientNum: string; // 学号
  nickname: string;
  clientAvator?: string;
  classesName: string; // 班级
  createTime: string;
  recordCount?: number; // 提问数
  clientTel?: number; // 手机号
}

// 学生详情类型
export interface StudentDetail {
  id: string;
  studentId: string;
  name: string;
  avatar?: string;
  className: string;
  totalQuestions: number;
  mainSubject: string;
  lastActive: string;
  complexityDistribution: {
    level: string;
    count: number;
    percentage: number;
  }[];
  learningTrend: {
    date: string;
    score: number;
  }[];
  learningAdvice: string[];
}


// 数据分析统计类型
export interface AnalyticsData {
  totalQuestions: number;
  participantStudents: number;
  subjectCategories: number;
  todayQuestions: number;
  questionTypeDistribution: {
    type: string;
    count: number;
    percentage: number;
  }[];
  questionTrend: {
    date: string;
    count: number;
  }[];
  activeStudents: {
    date: string;
    count: number;
  }[];
  complexityDistribution: {
    level: string;
    count: number;
    percentage: number;
  }[];
  subjectDistribution: {
    subject: string;
    count: number;
    percentage: number;
  }[];
  hotKeywords: {
    keyword: string;
    count: number;
    trend: 'up' | 'down' | 'stable';
  }[];
}

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
}

// 班级列表类型
export interface ClassInfo {
  id: string;
  name: string;
  studentCount: number;
}
