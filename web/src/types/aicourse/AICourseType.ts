// AI通识课类型定义

/**
 * AI通识课章节
 */
export interface AICourseChapter {
  id: string;
  title: string;
  description: string;
  order: number;
  lessons: AICourseLesson[];
}

/**
 * AI通识课课程
 */
export interface AICourseLesson {
  id: string;
  chapterId: string;
  title: string;
  description: string;
  order: number;
  examples: AICourseExample[];
}

/**
 * AI通识课示例
 */
export interface AICourseExample {
  id: string;
  lessonId: string;
  title: string;
  description: string;
  prompt: string;
  expectedOutput?: string;
  order: number;
}

/**
 * 聊天消息
 */
export interface AIChatMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: number;
}

/**
 * 聊天会话
 */
export interface AIChatSession {
  id: string;
  courseId: string;
  title: string;
  messages: AIChatMessage[];
  createdAt: number;
  updatedAt: number;
}

/**
 * AI通识课API响应
 */
export interface AICourseResponse {
  chapters: AICourseChapter[];
}

/**
 * 发送消息参数
 */
export interface SendMessageParams {
  sessionId: string;
  content: string;
}

/**
 * 发送消息响应
 */
export interface SendMessageResponse {
  message: AIChatMessage;
}
