'use client';

import React from 'react';
import { motion } from 'framer-motion';
import {
  TeamOutlined,
  CalendarOutlined,
  EditOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import { Course } from '@/types/workspace/WorkspaceType';
import styles from '@/styles/workspace/index.module.css';

interface CourseCardProps {
  course: Course;
  index: number;
  onEdit: (course: Course) => void;
  onDelete: (course: Course) => void;
  onClick?: (course: Course) => void;
}

// 格式化时间
const formatDate = (timestamp: number): string => {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 卡片动画变体
const cardVariants = {
  hidden: { opacity: 0, y: 20, scale: 0.98 },
  visible: (i: number) => ({
    opacity: 1,
    y: 0,
    scale: 1,
    transition: {
      delay: i * 0.08,
      duration: 0.4,
      ease: [0.19, 1, 0.22, 1] as const,
    },
  }),
  exit: {
    opacity: 0,
    y: -10,
    scale: 0.98,
    transition: { duration: 0.2 },
  },
};

const CourseCard: React.FC<CourseCardProps> = ({
  course,
  index,
  onClick,
}) => {
  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit(course);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete(course);
  };

  return (
    <motion.div
      className={styles.courseCard}
      custom={index}
      variants={cardVariants}
      initial="hidden"
      animate="visible"
      exit="exit"
      onClick={() => onClick?.(course)}
      whileHover={{ scale: 1.01 }}
      whileTap={{ scale: 0.99 }}
    >
      <div className={styles.courseCardHeader}>
        <h3 className={styles.courseName}>{course.repoCategoryName}</h3>
        <span className={styles.courseGroup}>{course.courseGroupName}</span>
      </div>

      <div className={styles.courseInfo}>
        <div className={styles.courseInfoItem}>
          <TeamOutlined className={styles.courseInfoIcon} />
          <span>学生数量: {course.studentCount} 人</span>
        </div>
        <div className={styles.courseInfoItem}>
          <CalendarOutlined className={styles.courseInfoIcon} />
          <span>创建时间: {formatDate(course.createTime)}</span>
        </div>
      </div>
    </motion.div>
  );
};

export default CourseCard;
