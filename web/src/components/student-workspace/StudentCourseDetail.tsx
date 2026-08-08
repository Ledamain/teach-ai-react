'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  ArrowLeftOutlined,
  BarChartOutlined,
  FolderOutlined,
  FileTextOutlined, RobotOutlined,
} from '@ant-design/icons';
import type { Course } from '@/types/workspace/WorkspaceType';
import StudentAnalyticsPanel from './StudentAnalyticsPanel';
import StudentKnowledgePanel from './StudentKnowledgePanel';
import StudentAssignmentPanel from './StudentAssignmentPanel';
import styles from '@/styles/studentWorkspace/index.module.css';
import AssignmentPanel from "../workspace/AssignmentPanel";
import {router} from "next/client";

interface StudentCourseDetailProps {
  course: Course;
  studentId: string;
  onBack: () => void;
}

type TabKey = 'analytics' | 'knowledge' | 'assignment' | 'aiCourse';

interface TabItem {
  key: TabKey;
  label: string;
  icon: React.ReactNode;
}

// 学生工作台只有3个标签：数据分析、知识库管理、作业练习
const tabs: TabItem[] = [
  { key: 'analytics', label: '数据分析', icon: <BarChartOutlined /> },
  { key: 'knowledge', label: '知识库管理', icon: <FolderOutlined /> },
  { key: 'assignment', label: '作业练习', icon: <FileTextOutlined /> },
  { key: 'aiCourse', label: 'AI通识课', icon: <RobotOutlined /> },
];

const StudentCourseDetail: React.FC<StudentCourseDetailProps> = ({
  course,
  studentId,
  onBack,
}) => {
  const [activeTab, setActiveTab] = useState<TabKey>('analytics');

  // 处理Tab点击
  const handleTabClick = (key: TabKey) => {
    if (key === 'aiCourse') {
      // setShowAICourse(true);
      router.push('/aicourse')
    } else {
      setActiveTab(key);
    }
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'analytics':
        return <StudentAnalyticsPanel courseId={course.id} studentId={studentId} />;
      case 'knowledge':
        return <StudentKnowledgePanel courseId={course.id} studentId={studentId} />;
      case 'assignment':
        return <StudentAssignmentPanel courseId={course.id} studentId={studentId} />;
      default:
        return null;
    }
  };

  return (
    <motion.div
      className={styles.courseDetailContainer}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.3 }}
    >
      {/* 课程信息头部 */}
      <motion.div
        className={styles.courseHeader}
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3, delay: 0.1 }}
      >
        <div className={styles.headerTop}>
          <button className={styles.backButton} onClick={onBack}>
            <ArrowLeftOutlined />
            <span>返回</span>
          </button>
          <h1 className={styles.courseName}>{course.repoCategoryName}</h1>
        </div>
        <div className={styles.courseStats}>
          <div className={styles.statItem}>
            <span className={styles.statLabel}>学生数量</span>
            <span className={styles.statValue}>{course.studentCount}</span>
          </div>
          <div className={styles.statItem}>
            <span className={styles.statLabel}>所属课程组</span>
            <span className={styles.statValue}>{course.courseGroupName || '未分组'}</span>
          </div>
        </div>
      </motion.div>

      {/* 标签页导航 */}
      <motion.div
        className={styles.tabsContainer}
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.3, delay: 0.2 }}
      >
        <div className={styles.tabsList}>
          {tabs.map((tab) => (
            <div
              key={tab.key}
              className={`${styles.tabItem} ${
                activeTab === tab.key ? styles.tabItemActive : ''
              }`}
              onClick={() => handleTabClick(tab.key)}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </div>
          ))}
        </div>
      </motion.div>

      {/* 内容区域 */}
      <div className={styles.contentArea}>
        <AnimatePresence mode="wait">
          <motion.div
            key={activeTab}
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            transition={{ duration: 0.25, ease: [0.19, 1, 0.22, 1] }}
          >
            {renderTabContent()}
          </motion.div>
        </AnimatePresence>
      </div>
    </motion.div>
  );
};

export default StudentCourseDetail;
