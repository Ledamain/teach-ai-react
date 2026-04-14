'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { ChevronDown, ChevronRight, FileText, Play } from 'lucide-react'
import { Chapter } from '@/lib/course-data'
import styles from '@/styles/aicourse/chapterList/index.module.css'

interface ChapterListProps {
  chapters: Chapter[]
}

export function ChapterList({ chapters }: ChapterListProps) {
  const [expandedChapters, setExpandedChapters] = useState<Set<string>>(new Set())
  const router = useRouter()

  const toggleChapter = (chapterId: string) => {
    setExpandedChapters((prev) => {
      const newSet = new Set(prev)
      if (newSet.has(chapterId)) {
        newSet.delete(chapterId)
      } else {
        newSet.add(chapterId)
      }
      return newSet
    })
  }

  const handleLessonClick = (lessonId: string) => {
    router.push(`/lesson/${lessonId}`)
  }

  return (
    <div className={styles.container}>
      <h2 className={styles.sectionTitle}>课程章节</h2>
      
      <div className={styles.chapterList}>
        {chapters.map((chapter, index) => {
          const isExpanded = expandedChapters.has(chapter.id)
          
          return (
            <div key={chapter.id} className={styles.chapterCard}>
              {/* Chapter Header */}
              <button
                onClick={() => toggleChapter(chapter.id)}
                className={styles.chapterHeader}
              >
                <div className={styles.chapterIndex}>
                  <span className={styles.chapterIndexText}>{index + 1}</span>
                </div>
                
                <div className={styles.chapterContent}>
                  <h3 className={styles.chapterTitle}>
                    {chapter.title}
                  </h3>
                  <p className={styles.chapterDescription}>
                    {chapter.description}
                  </p>
                </div>
                
                <div className={styles.expandIcon}>
                  {isExpanded ? (
                    <ChevronDown className={styles.expandIconSvg} />
                  ) : (
                    <ChevronRight className={styles.expandIconSvg} />
                  )}
                </div>
              </button>

              {/* Lessons List */}
              <div className={isExpanded ? styles.lessonListWrapperExpanded : styles.lessonListWrapperCollapsed}>
                <div className={styles.lessonListContainer}>
                  <div className={styles.lessonList}>
                    {chapter.lessons.map((lesson, lessonIndex) => (
                      <button
                        key={lesson.id}
                        onClick={() => handleLessonClick(lesson.id)}
                        className={styles.lessonItem}
                      >
                        <div className={styles.lessonIconWrapper}>
                          <FileText className={styles.lessonIcon} />
                        </div>
                        
                        <span className={styles.lessonTitle}>
                          第 {lessonIndex + 1} 节：{lesson.title}
                        </span>
                        
                        <Play className={styles.lessonPlayIcon} />
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
