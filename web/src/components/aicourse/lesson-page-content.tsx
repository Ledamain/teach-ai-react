'use client'

import { useRouter } from 'next/navigation'
import { ArrowLeft, FileText, ChevronLeft, ChevronRight } from 'lucide-react'
import { Lesson, Chapter, courseData } from '@/lib/course-data'
import { DocumentViewer } from '@/components/aicourse/document-viewer'
import { CustomComponentArea } from '@/components/aicourse/custom-component-area'
import { useMemo } from 'react'
import styles from '@/styles/aicourse/lessonPage/index.module.css'

interface LessonPageContentProps {
  lesson: Lesson
  chapter: Chapter
}

export function LessonPageContent({ lesson, chapter }: LessonPageContentProps) {
  const router = useRouter()
  
  // Get all lessons in flat array for navigation
  const { prevLesson, nextLesson, currentIndex, totalLessons } = useMemo(() => {
    const allLessons: { lesson: Lesson; chapter: Chapter }[] = []
    
    for (const ch of courseData.chapters) {
      for (const l of ch.lessons) {
        allLessons.push({ lesson: l, chapter: ch })
      }
    }
    
    const currentIdx = allLessons.findIndex(item => item.lesson.id === lesson.id)
    
    return {
      prevLesson: currentIdx > 0 ? allLessons[currentIdx - 1] : null,
      nextLesson: currentIdx < allLessons.length - 1 ? allLessons[currentIdx + 1] : null,
      currentIndex: currentIdx + 1,
      totalLessons: allLessons.length,
    }
  }, [lesson.id])
  
  const handleBack = () => {
    router.push('/aicourse')
  }
  
  const handlePrevLesson = () => {
    if (prevLesson) {
      router.push(`/lesson/${prevLesson.lesson.id}`)
    }
  }
  
  const handleNextLesson = () => {
    if (nextLesson) {
      router.push(`/lesson/${nextLesson.lesson.id}`)
    }
  }

  return (
    <div className={styles.pageContainer}>
      {/* Header */}
      <header className={styles.header}>
        <div className={styles.headerLeft}>
          <button onClick={handleBack} className={styles.backButton}>
            <ArrowLeft className={styles.backButtonIcon} />
            <span className={styles.backButtonText}>返回课程</span>
          </button>
          
          <div className={styles.breadcrumb}>
            <span className={styles.breadcrumbChapter}>{chapter.title}</span>
            <span className={styles.breadcrumbSeparator}>/</span>
            <span className={styles.breadcrumbLesson}>{lesson.title}</span>
          </div>
        </div>
        
        <div className={styles.headerRight}>
          <span className={styles.progressBadge}>
            {currentIndex} / {totalLessons}
          </span>
          
          <button
            onClick={handlePrevLesson}
            disabled={!prevLesson}
            className={styles.navButton}
            title={prevLesson ? `上一节：${prevLesson.lesson.title}` : '没有上一节'}
          >
            <ChevronLeft className={styles.navButtonIcon} />
          </button>
          
          <button
            onClick={handleNextLesson}
            disabled={!nextLesson}
            className={styles.navButton}
            title={nextLesson ? `下一节：${nextLesson.lesson.title}` : '没有下一节'}
          >
            <ChevronRight className={styles.navButtonIcon} />
          </button>
        </div>
      </header>

      {/* Main Content */}
      <div className={styles.mainContent}>
        {/* Left Panel - Document Viewer (35%) */}
        <div className={styles.leftPanel}>
          <div className={styles.panelHeader}>
            <FileText className={styles.panelHeaderIcon} />
            <span className={styles.panelHeaderText}>课程文档</span>
          </div>
          
          <div className={styles.panelContent}>
            <DocumentViewer lessonId={lesson.id} />
          </div>
        </div>

        {/* Right Panel - Custom Component Area (65%) */}
        <div className={styles.rightPanel}>
          <CustomComponentArea lessonId={lesson.id} lessonTitle={lesson.title} />
        </div>
      </div>
    </div>
  )
}
