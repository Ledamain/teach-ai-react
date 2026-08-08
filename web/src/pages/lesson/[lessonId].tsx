import { GetStaticProps, GetStaticPaths, NextPage } from 'next';
import Head from 'next/head';
import { notFound } from 'next/navigation';
import { getLessonById, getChapterByLessonId, courseData } from '@/lib/course-data';
import { LessonPageContent } from '@/components/aicourse/lesson-page-content';

// 类型定义（可根据你的课程数据结构细化）
interface Lesson {
  id: string;
  title: string;
  // 其他课程字段
}

interface Chapter {
  title: string;
  // 其他章节字段
}

interface LessonPageProps {
  lesson: Lesson;
  chapter: Chapter;
}

const LessonPage: NextPage<LessonPageProps> = ({ lesson, chapter }) => {
  // 数据不存在时触发 404
  if (!lesson || !chapter) {
    notFound();
    return null;
  }

  return (
      <>
        {/* Page Router 用 Head 配置 SEO */}
        <Head>
          <title>{lesson.title} - {chapter.title}</title>
          <meta name="description" content={`学习${chapter.title}中的${lesson.title}`} />
        </Head>
        <LessonPageContent lesson={lesson} chapter={chapter} />
      </>
  );
};

// 预生成所有课程页面（替代 App Router 的 generateStaticParams）
export const getStaticPaths: GetStaticPaths = async () => {
  const paths = courseData.chapters.flatMap((chapter: any) =>
      chapter.lessons.map((lesson: any) => ({
        params: { lessonId: lesson.id },
      }))
  );

  return {
    paths,
    fallback: false, // 不存在的 lessonId 直接返回 404
  };
};

// 预获取页面数据（替代 App Router 直接 await 数据）
export const getStaticProps: GetStaticProps<LessonPageProps> = async (context) => {
  const { lessonId } = context.params as { lessonId: string };

  const lesson = getLessonById(lessonId);
  const chapter = getChapterByLessonId(lessonId);

  // 数据不存在时返回 404
  if (!lesson || !chapter) {
    return {
      notFound: true,
    };
  }

  return {
    props: {
      lesson,
      chapter,
    },
  };
};

export default LessonPage;