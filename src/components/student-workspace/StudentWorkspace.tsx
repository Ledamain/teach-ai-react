'use client';

import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ArrowLeftOutlined, BookOutlined, FolderOutlined } from '@ant-design/icons';
import { message, Spin } from 'antd';
import Image, {type StaticImageData} from 'next/image';
import { Course } from '@/types/workspace/WorkspaceType';
import StudentWorkspaceApi from '@/api/studentWorkspace'
import StudentCourseDetail from './StudentCourseDetail';
import styles from '@/styles/studentWorkspace/index.module.css';

interface StudentWorkspaceProps {
  onBack: () => void;
  logoImage?: StaticImageData;
  studentId?: string;
}

// 课程详情展开动画变体
const detailVariants = {
  hidden: {
    opacity: 0,
    scale: 0.95,
  },
  visible: {
    opacity: 1,
    scale: 1,
    transition: { duration: 0.4, ease: [0.19, 1, 0.22, 1] as const },
  },
  exit: {
    opacity: 0,
    scale: 0.95,
    transition: { duration: 0.25, ease: [0.19, 1, 0.22, 1] as const },
  },
};

// 主内容区动画变体
const contentVariants = {
  hidden: { opacity: 0, x: 20 },
  visible: {
    opacity: 1,
    x: 0,
    transition: { duration: 0.4, ease: [0.19, 1, 0.22, 1] as const },
  },
  exit: {
    opacity: 0,
    x: -20,
    transition: { duration: 0.25, ease: [0.19, 1, 0.22, 1] as const },
  },
};

// 空状态动画变体
const emptyVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.5, ease: [0.19, 1, 0.22, 1] as const },
  },
};

// 课程卡片动画变体
const cardVariants = {
  hidden: { opacity: 0, scale: 0.9, y: 20 },
  visible: (i: number) => ({
    opacity: 1,
    scale: 1,
    y: 0,
    transition: {
      duration: 0.3,
      delay: i * 0.05,
      ease: [0.19, 1, 0.22, 1] as const,
    },
  }),
  exit: {
    opacity: 0,
    scale: 0.9,
    transition: { duration: 0.2 },
  },
};

const StudentWorkspace: React.FC<StudentWorkspaceProps> = ({
  onBack,
  logoImage = '/placeholder-logo.jpg',
  studentId = 0, // 默认学生ID，实际应从登录信息获取
}) => {
  // 课程相关状态
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(false);

  // 课程详情相关状态
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);

  // 获取课程列表（学生可加入的课程）
  const fetchCourses = useCallback(async () => {
    setLoading(true);
    try {
      const res = await StudentWorkspaceApi.getCourseList(Number(studentId));
        try {
          setCourses(res);
        }catch (error){
          message.error('获取课程列表失败');
        }
    } catch (error) {
      message.error('获取课程列表失败');
      console.error('获取课程列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  // 初始化数据
  useEffect(() => {
    fetchCourses();
  }, [fetchCourses]);

  // 点击课程卡片 - 打开课程详情
  const handleCourseClick = (course: Course) => {
    setSelectedCourse(course);
  };

  // 返回课程列表
  const handleBackFromDetail = () => {
    setSelectedCourse(null);
  };

  // 如果选中了课程，显示课程详情页面
  if (selectedCourse) {
    return (
      <motion.div
        className={styles.rightContain}
        variants={detailVariants}
        initial="hidden"
        animate="visible"
        exit="exit"
        style={{ padding: 0 }}
      >
        <StudentCourseDetail
          course={selectedCourse}
          studentId={studentId}
          onBack={handleBackFromDetail}
        />
      </motion.div>
    );
  }

  return (
    <motion.div
      className={styles.rightContain}
      variants={contentVariants}
      initial="hidden"
      animate="visible"
      exit="exit"
    >
      {/* 顶部区域 */}
      <div className={styles.workspaceHeader}>
        <div className={styles.headerLeft}>
          {/* 返回按钮 */}
          <button className={styles.backButton} onClick={onBack}>
            <ArrowLeftOutlined />
            返回
          </button>

          {/* 课程数量 */}
          <div className={styles.courseCount}>
            <BookOutlined />
            我的课程:
            <span className={styles.courseCountNumber}>{courses.length}</span>
          </div>
        </div>

        {/* 中间标题 */}
        <div className={styles.headerTitle}>
          <Image
            src={logoImage}
            alt="logo"
            width={36}
            height={36}
            className={styles.headerLogo}
          />
          <span className={styles.headerTitleText}>AI智学学习平台</span>
        </div>

        {/* 右侧留空（学生没有创建课程按钮） */}
        <div className={styles.headerRight}></div>
      </div>

      {/* 课程卡片区域 */}
      <div className={styles.courseContainer}>
        <Spin spinning={loading}>
          {courses.length > 0 ? (
            <div className={styles.courseGrid}>
              <AnimatePresence mode="popLayout">
                {courses.map((course, index) => (
                  <motion.div
                    key={course.id}
                    className={styles.courseCard}
                    custom={index}
                    variants={cardVariants}
                    initial="hidden"
                    animate="visible"
                    exit="exit"
                    layout
                    onClick={() => handleCourseClick(course)}
                    whileHover={{ scale: 1.02, y: -4 }}
                    whileTap={{ scale: 0.98 }}
                  >
                    <div className={styles.courseCardHeader}>
                      <div className={styles.courseIcon}>
                        <BookOutlined />
                      </div>
                      <div className={styles.courseInfo}>
                        <div className={styles.courseName}>{course.repoCategoryName}</div>
                        <div className={styles.courseGroup}>
                          {course.courseGroupName || '未分组'}
                        </div>
                      </div>
                    </div>
                    <div className={styles.courseMeta}>
                      <span>学生数: {course.studentCount}</span>
                      <span>进入学习</span>
                    </div>
                  </motion.div>
                ))}
              </AnimatePresence>
            </div>
          ) : (
            !loading && (
              <motion.div
                className={styles.emptyState}
                variants={emptyVariants}
                initial="hidden"
                animate="visible"
              >
                <FolderOutlined className={styles.emptyIcon} />
                <h3 className={styles.emptyTitle}>暂无课程</h3>
                <p className={styles.emptyDescription}>
                  您还没有加入任何课程，请联系老师获取课程邀请
                </p>
              </motion.div>
            )
          )}
        </Spin>
      </div>
    </motion.div>
  );
};

export default StudentWorkspace;
