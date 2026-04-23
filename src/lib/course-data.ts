export interface Lesson {
  id: string
  title: string
  chapterId: string
}

export interface Chapter {
  id: string
  title: string
  description: string
  lessons: Lesson[]
}

export interface Course {
  id: string
  title: string
  category: string
  chapterCount: number
  description: string
  coverImage: string
  chapters: Chapter[]
}

export const courseData: Course = {
  id: 'ai-general-education',
  title: '人工智能通识教育实验教程',
  category: '人工智能',
  chapterCount: 4,
  description: '本课程旨在帮助学生和教师学习如何使用人工智能技术来提升学习和工作效率。课程涵盖AI应用实践入门、提示词工程、智能办公以及生成式人工智能等核心内容，通过理论与实践相结合的方式，让学习者能够快速掌握AI工具的使用方法，并将其应用于实际场景中。',
  coverImage: 'https://teach-ai.tos-cn-beijing.volces.com/%E6%96%B0%E5%AF%B9%E8%AF%9D.png',
  chapters: [
    {
      id: 'chapter-1',
      title: '人工智能应用实践入门',
      description: '学习如何利用AI知识库实现高效的问答交互，掌握通用模式和专属模式的应用场景。',
      lessons: [
        {
          id: 'lesson-1-1',
          title: '通用模式下利用对应知识库实现点对点问答',
          chapterId: 'chapter-1',
        },
        {
          id: 'lesson-1-2',
          title: '专属模式下利用专属知识库实现面对面问答',
          chapterId: 'chapter-1',
        },
      ],
    },
    {
      id: 'chapter-2',
      title: '提示词工程',
      description: '深入学习提示词优化技巧，包括输出样例、任务步骤设计和引导模型思考等方法。',
      lessons: [
        {
          id: 'lesson-2-1',
          title: '提示词优化之为模型提供输出样例',
          chapterId: 'chapter-2',
        },
        {
          id: 'lesson-2-2',
          title: '提示词优化之设计任务完成的步骤',
          chapterId: 'chapter-2',
        },
        {
          id: 'lesson-2-3',
          title: '提示词优化之引导模型思考',
          chapterId: 'chapter-2',
        },
      ],
    },
    {
      id: 'chapter-3',
      title: '智能办公',
      description: '掌握如何上传文件、图片、视频等多媒体内容来辅助智能体进行问答和处理任务。',
      lessons: [
        {
          id: 'lesson-3-1',
          title: '上传文件协助智能体问答',
          chapterId: 'chapter-3',
        },
        {
          id: 'lesson-3-2',
          title: '上传图片、视频辅助智能体问答',
          chapterId: 'chapter-3',
        },
      ],
    },
    {
      id: 'chapter-4',
      title: '生成式人工智能',
      description: '探索生成式AI的强大能力，学习生成教学大纲、游戏、图片和视频的实践方法。',
      lessons: [
        {
          id: 'lesson-4-1',
          title: '生成式人工智能之生成教学大纲',
          chapterId: 'chapter-4',
        },
        {
          id: 'lesson-4-2',
          title: '生成式人工智能之生成在线运行游戏',
          chapterId: 'chapter-4',
        },
        {
          id: 'lesson-4-3',
          title: '生成式人工智能之生成教学图片',
          chapterId: 'chapter-4',
        },
        {
          id: 'lesson-4-4',
          title: '生成式人工智能之生成教学视频',
          chapterId: 'chapter-4',
        },
      ],
    },
  ],
}

export function getChapterById(chapterId: string): Chapter | undefined {
  return courseData.chapters.find((chapter) => chapter.id === chapterId)
}

export function getLessonById(lessonId: string): Lesson | undefined {
  for (const chapter of courseData.chapters) {
    const lesson = chapter.lessons.find((l) => l.id === lessonId)
    if (lesson) return lesson
  }
  return undefined
}

export function getChapterByLessonId(lessonId: string): Chapter | undefined {
  for (const chapter of courseData.chapters) {
    const lesson = chapter.lessons.find((l) => l.id === lessonId)
    if (lesson) return chapter
  }
  return undefined
}
