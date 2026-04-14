import { CourseHeader } from '@/components/aicourse/course-header'
import { ChapterList } from '@/components/aicourse/chapter-list'
import { courseData } from '@/lib/course-data'
import styles from '@/styles/aicourse/home/index.module.css'

export default function HomePage() {
    return (
        <main className={styles.pageContainer}>
            <div className={styles.contentWrapper}>
                {/* Course Header Section */}
                <section className={styles.courseSection}>
                    <CourseHeader course={courseData} />
                </section>

                {/* Chapters Section */}
                <section className={styles.chapterSection}>
                    <ChapterList chapters={courseData.chapters} />
                </section>

                {/* Footer */}
                <footer className={styles.footer}>
                    <p className={styles.footerText}>
                        © 2024 AI智学智能辅助平台. All rights reserved.
                    </p>
                </footer>
            </div>
        </main>
    )
}
