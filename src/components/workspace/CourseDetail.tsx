'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  ArrowLeftOutlined,
  BarChartOutlined,
  TeamOutlined,
  FolderOutlined,
  FileTextOutlined,
  RobotOutlined
} from '@ant-design/icons';
import type { Course } from '@/types/workspace/WorkspaceType';
import AnalyticsPanel from './AnalyticsPanel';
import StudentPanel from './StudentPanel';
import KnowledgePanel from './KnowledgePanel';
import AssignmentPanel from './AssignmentPanel';
import styles from '@/styles/workspace/courseDetail.module.css';
import HomePage from "@/pages/aicourse";
import {router} from "next/client";
import {getUserId} from "@/utils/userUtil";

interface CourseDetailProps {
  course: Course;
  onBack: () => void;
}

type TabKey = 'analytics' | 'students' | 'knowledge' | 'assignment' | 'aiCourse';

interface TabItem {
  key: TabKey;
  label: string;
  icon: React.ReactNode;
}

const userid: number | null = getUserId();

const baseTabs: TabItem[] = [
  { key: 'analytics', label: '数据分析', icon: <BarChartOutlined /> },
  { key: 'students', label: '学生管理', icon: <TeamOutlined /> },
  { key: 'knowledge', label: '知识库管理', icon: <FolderOutlined /> },
  { key: 'assignment', label: '作业练习', icon: <FileTextOutlined /> },
  { key: 'aiCourse', label: 'AI通识课', icon: <RobotOutlined /> },
];

//TODO
const tabs: TabItem[] = baseTabs.filter(tab => {
  // 如果userId是9，排除key为analytics的项
  if (userid === 101) {
    return tab.key !== 'analytics';
  }
  // 其他情况（null / 其他数字），显示全部
  return true;
});

const CourseDetail: React.FC<CourseDetailProps> = ({ course, onBack }) => {
  const [activeTab, setActiveTab] = useState<TabKey>('analytics');
  const [showAICourse, setShowAICourse] = useState(false);


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
        return <AnalyticsPanel courseId={course.id} />;
      case 'students':
        return <StudentPanel courseId={course.id} />;
      case 'knowledge':
        return <KnowledgePanel courseId={course.id} />;
      case 'assignment':
        return <AssignmentPanel courseId={course.id} />;
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
      {/* 课程信息头部 - 简约白色风格 */}
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
            <span className={styles.statLabel}>创建时间</span>
            <span className={styles.statValue}>{course.createTime ? new Date(course.createTime).toLocaleDateString() : ''}</span>
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
              className={`${styles.tabItem} ${activeTab === tab.key ? styles.tabItemActive : ''}`}
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
      {/* AI通识课全屏模态框 */}
      {/*{showAICourse && (*/}
      {/*    // <AICoursePanel*/}
      {/*    //     courseId={course.id}*/}
      {/*    //     courseName={course.repoCategoryName}*/}
      {/*    //     onClose={() => setShowAICourse(false)}*/}
      {/*    // />*/}
      {/*    <HomePage/>*/}
      {/*)}*/}
    </motion.div>
  );
};

export default CourseDetail;
