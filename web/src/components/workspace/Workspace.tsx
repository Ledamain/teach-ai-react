'use client';

import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  ArrowLeftOutlined,
  PlusOutlined,
  BookOutlined,
  FolderOutlined,
} from '@ant-design/icons';
import { message, Modal, Spin } from 'antd';
import Image from 'next/image';
import type { StaticImageData } from 'next/image';
import { Course, CourseGroup } from '@/types/workspace/WorkspaceType';
import {
  getCourseGroupList,
  updateCourse,
  deleteCourse,
} from '@/api/workspace';
import WorkspaceApi from '@/api/workspace'
import RepoCategoryApi from '@/api/repoCategory'
import CourseGroupApi from '@/api/coursegroup'
import CourseCard from './CourseCard';
import CourseModal from './CourseModal';
import CourseDetail from './CourseDetail';
import styles from '@/styles/workspace/index.module.css';
import {UserInfo} from "@/types/login/LoginType";
import {CourseGroupType} from "@/types/coursegroup/CourseGroupType";

// Props 使用可选的 logoImage
interface WorkspaceProps {
  onBack: () => void;
  logoImage?: StaticImageData;
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

const Workspace: React.FC<WorkspaceProps> = ({ onBack, logoImage = '/placeholder-logo.jpg' }) => {
  // 课程相关状态
  const [courses, setCourses] = useState<Course[]>([]);
  const [courseGroups, setCourseGroups] = useState<CourseGroupType[]>([]);
  const [loading, setLoading] = useState(false);

  // 课程详情相关状态
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);

  // Modal 相关状态
  const [modalVisible, setModalVisible] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [modalLoading, setModalLoading] = useState(false);

  const getUserId = (): number | null => {
    if (typeof window === 'undefined') return null;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    try {
      const userInfo: UserInfo = JSON.parse(userInfoStr);
      return Number(userInfo.id);
    } catch {
      return null;
    }
  };

  const getUserRole = (): String | null => {
    if (typeof window === 'undefined') return null;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    try {
      const userInfo: UserInfo = JSON.parse(userInfoStr);
      return String(userInfo.clientRole);
    } catch {
      return null;
    }
  };

  // 获取课程列表
  const fetchCourses = useCallback(async () => {
    setLoading(true);
    try {
      const userRole = getUserRole()
      if (userRole === "1"){
        const teacherUserId = getUserId()
        const res : Course[] = await WorkspaceApi.getCourseList(teacherUserId);
        setCourses(res);
      }
    } catch (error) {
      message.error('获取课程列表失败');
      console.error('获取课程列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  // 获取课程组列表
  const fetchCourseGroups = useCallback(async () => {
    try {
      const res = await CourseGroupApi.getCourseGroupList();
        setCourseGroups(res);
    } catch (error) {
      console.error('获取课程组列表失败:', error);
    }
  }, []);

  // 初始化数据
  useEffect(() => {
    fetchCourses();
    fetchCourseGroups();
  }, [fetchCourses, fetchCourseGroups]);

  // 打开创建 Modal
  const handleCreate = () => {
    setModalMode('create');
    setEditingCourse(null);
    setModalVisible(true);
  };

  // 打开编辑 Modal
  const handleEdit = (course: Course) => {
    setModalMode('edit');
    setEditingCourse(course);
    setModalVisible(true);
  };

  // 删除课程
  const handleDelete = (course: Course) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除课程「${course.repoCategoryName}」吗？此操作不可恢复。`,
      okText: '确认删除',
      cancelText: '取消',
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          const res = await deleteCourse(course.id);
          if (res.code === 200) {
            message.success('删除成功');
            fetchCourses();
          } else {
            message.error(res.message || '删除失败');
          }
        } catch (error) {
          message.error('删除失败');
          console.error('删除失败:', error);
        }
      },
    });
  };

  // 关闭 Modal
  const handleModalCancel = () => {
    setModalVisible(false);
    setEditingCourse(null);
  };

  // 提交表单
  const handleModalSubmit = async (values: { repoCategoryName: string; courseGroupId: number }) => {
    setModalLoading(true);
    try {
      const teacherUserId = getUserId();
      const submit = {
        ...values,
        teacherUserId: teacherUserId ?? 0,
      }
      if (modalMode === 'create') {
        const res = await RepoCategoryApi.createRepoCategory(submit);
        try {
          message.success('创建成功');
          setModalVisible(false);
          fetchCourses();
        }catch ( error){
          message.error('创建失败');
        }
      } else {
        if (!editingCourse) return;
        const updateSubmit = {
          ...submit,
          id: editingCourse.id
        }
        const res = await RepoCategoryApi.updateRepoCategory(updateSubmit);
          try {
            message.success('修改成功');
            setModalVisible(false);
            fetchCourses();
          }catch (error){
            message.error('修改失败');
          }
      }
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      }
    } finally {
      setModalLoading(false);
    }
  };

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
        <CourseDetail 
          course={selectedCourse} 
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
            课程数量:
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
          <span className={styles.headerTitleText}>智汇伴学教学辅助平台</span>
        </div>

        {/* 右侧按钮 */}
        <div className={styles.headerRight}>
          <button className={styles.createButton} onClick={handleCreate}>
            <PlusOutlined />
            创建新课程
          </button>
        </div>
      </div>

      {/* 课程卡片区域 */}
      <div className={styles.courseContainer}>
        <Spin spinning={loading}>
          {courses.length > 0 ? (
            <div className={styles.courseGrid}>
              <AnimatePresence mode="popLayout">
                {courses.map((course, index) => (
                  <CourseCard
                    key={course.id}
                    course={course}
                    index={index}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onClick={handleCourseClick}
                  />
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
                  点击右上角的「创建新课程」按钮开始创���您的第一个课程
                </p>
                <button className={styles.createButton} onClick={handleCreate}>
                  <PlusOutlined />
                  创建新课程
                </button>
              </motion.div>
            )
          )}
        </Spin>
      </div>

      {/* 课程编辑 Modal */}
      <CourseModal
        visible={modalVisible}
        mode={modalMode}
        course={editingCourse}
        courseGroups={courseGroups}
        loading={modalLoading}
        onCancel={handleModalCancel}
        onSubmit={handleModalSubmit}
      />
    </motion.div>
  );
};

export default Workspace;
