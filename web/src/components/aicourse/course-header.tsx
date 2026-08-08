'use client'

import { ArrowLeft, Layers, Tag } from 'lucide-react'
import { useRouter } from 'next/navigation'
import { Course } from '@/lib/course-data'
import Image from 'next/image'
import styles from '@/styles/aicourse/courseHeader/index.module.css'

interface CourseHeaderProps {
  course: Course
}

export function CourseHeader({ course }: CourseHeaderProps) {
  const router = useRouter()

  return (
    <div className={styles.container}>
      {/* Back Button */}
      <button
        onClick={() => router.push('/chat')}
        className={styles.backButton}
      >
        <ArrowLeft className={styles.backButtonIcon} />
        <span className={styles.backButtonText}>返回</span>
      </button>

      {/* Course Info */}
      <div className={styles.infoWrapper}>
        {/* Cover Image */}
        <div className={styles.coverImage}>
          <Image
            src={course.coverImage}
            alt={course.title}
            fill
            className="object-cover"
            priority
          />
          <div className={styles.coverImageOverlay} />
        </div>

        {/* Course Details */}
        <div className={styles.detailsWrapper}>
          <h1 className={styles.title}>
            {course.title}
          </h1>
          
          {/* Meta Info */}
          <div className={styles.metaInfo}>
            <div className={styles.metaItemAccent}>
              <Layers className={styles.metaIcon} />
              <span className={styles.metaText}>共 {course.chapterCount} 个章节</span>
            </div>
            <div className={styles.metaItemSecondary}>
              <Tag className={styles.metaIcon} />
              <span className={styles.metaText}>专业分类：{course.category}</span>
            </div>
          </div>

          {/* Description */}
          <div className={styles.descriptionWrapper}>
            <h3 className={styles.descriptionTitle}>课程简介</h3>
            <p className={styles.descriptionText}>
              {course.description}
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
