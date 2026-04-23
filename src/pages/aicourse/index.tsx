import { motion } from "framer-motion";
import { CourseHeader } from '@/components/aicourse/course-header'
import { ChapterList } from '@/components/aicourse/chapter-list'
import { courseData } from '@/lib/course-data'
import styles from '@/styles/aicourse/home/index.module.css'

// 动画变体 - 容器（控制子元素交错动画）
const containerVariants = {
    hidden: { opacity: 0, y: 20, scale: 0.98 },
    visible: {
        opacity: 1,
        y: 0,
        scale: 1,
        transition: {
            duration: 0.5,
            ease: [0.19, 1, 0.22, 1],
            staggerChildren: 0.15, // 子元素依次出现的间隔
        },
    },
};

// 动画变体 - 子项（单个区域的动画）
const itemVariants = {
    hidden: { opacity: 0, y: 16 },
    visible: {
        opacity: 1,
        y: 0,
        transition: { duration: 0.4, ease: [0.19, 1, 0.22, 1] },
    },
};

export default function HomePage() {
    return (
        <main className={styles.pageContainer}>
            {/* 包裹层添加容器动画 */}
            <motion.div
                className={styles.contentWrapper}
                variants={containerVariants}
                initial="hidden"
                animate="visible"
            >
                {/* Course Header Section */}
                <motion.section
                    className={styles.courseSection}
                    variants={itemVariants}
                >
                    <CourseHeader course={courseData} />
                </motion.section>

                {/* Chapters Section */}
                <motion.section
                    className={styles.chapterSection}
                    variants={itemVariants}
                >
                    <ChapterList chapters={courseData.chapters} />
                </motion.section>

                {/* Footer */}
                <motion.footer
                    className={styles.footer}
                    variants={itemVariants}
                >
                    <p className={styles.footerText}>
                        2026 智汇伴学智能辅助平台.
                    </p>
                </motion.footer>
            </motion.div>
        </main>
    )
}