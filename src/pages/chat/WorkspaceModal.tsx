'use client';

import React, {useState, useEffect, useCallback} from 'react';
import {motion, AnimatePresence} from 'framer-motion';
import {
    ArrowLeftOutlined,
    BookOutlined,
    FolderOutlined,
    FileDoneOutlined
} from '@ant-design/icons';
import {Button, message, Spin} from 'antd';
import Image from 'next/image';
import type {StaticImageData} from 'next/image';
import {Course, CourseGroup} from '@/types/workspace/WorkspaceType';
import {getCourseGroupList} from '@/api/workspace/index';
import WorkspaceApi from '@/api/workspace/index'
import CourseCardModal from './CourseCardModal';
import styles from '@/styles/workspace/index.module.css';

interface WorkspaceModalProps {
    onBack: () => void;
    logoImage?: StaticImageData;
    onCourseSelect: (courseId: Course) => void; // 🔥 关键：点击卡片回调
}

const detailVariants = {
    hidden: {opacity: 0, scale: 0.95},
    visible: {opacity: 1, scale: 1, transition: {duration: 0.4}},
    exit: {opacity: 0, scale: 0.95, transition: {duration: 0.25}},
};

const contentVariants = {
    hidden: {opacity: 0, x: 20},
    visible: {opacity: 1, x: 0, transition: {duration: 0.4}},
    exit: {opacity: 0, x: -20, transition: {duration: 0.25}},
};

const emptyVariants = {
    hidden: {opacity: 0, y: 20},
    visible: {opacity: 1, y: 0, transition: {duration: 0.5}},
};

// 🔥 纯查看版 Workspace
const WorkspaceModal: React.FC<WorkspaceModalProps> = ({
                                                           onBack,
                                                           logoImage = '/placeholder-logo.jpg',
                                                           onCourseSelect,
                                                       }) => {
    const [courses, setCourses] = useState<Course[]>([]);
    const [courseGroups, setCourseGroups] = useState<CourseGroup[]>([]);
    const [loading, setLoading] = useState(false);
    const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
    const [defaultCourse,setDefaultCourse] = useState<Course | null>(null);

    // 获取课程列表
    const fetchCourses = useCallback(async () => {
        setLoading(true);
        try {
            const res:Course[] = await WorkspaceApi.getCourseList(null);
            setCourses(res)
        } catch (error) {
            message.error('获取课程失败');
        } finally {
            setLoading(false);
        }
    }, []);

    // 获取分组（仅展示用）
    const fetchCourseGroups = useCallback(async () => {
        try {
            const res = await getCourseGroupList();
            if (res.code === 200) setCourseGroups(res.data);
        } catch (e) {
            console.error(e);
        }
    }, []);

    useEffect(() => {
        fetchCourses();
        fetchCourseGroups();
    }, [fetchCourses, fetchCourseGroups]);

    // 🔥 点击课程 → 触发回调，返回ID
    const handleCourseClick = (course: Course) => {
        onCourseSelect(course);
    };

    // 详情返回
    const handleBackFromDetail = () => setSelectedCourse(null);

    // 详情页
    if (selectedCourse) {
        return (
            <motion.div className={styles.rightContain} variants={detailVariants} initial="hidden" animate="visible"
                        exit="exit" style={{padding: 0}}>
                <CourseDetail course={selectedCourse} onBack={handleBackFromDetail}/>
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
            // 1. 让最外层占满 Modal body 的高度，并使用 Flex 布局
            style={{
                padding: '24px',
                height: '100%',
                display: 'flex',
                flexDirection: 'column'
            }}
        >
            {/* 顶部 (保持不变) */}
            <div className={styles.workspaceHeader}>
                <div className={styles.headerLeft}>
                    <button className={styles.backButton} onClick={onBack}>
                        <ArrowLeftOutlined /> 返回
                    </button>
                    <div className={styles.courseCount}>
                        <BookOutlined /> 课程数量: <span>{courses.length}</span>
                    </div>
                    <Button color="cyan" variant="filled" shape="round" icon={ <FileDoneOutlined /> } onClick={() => handleCourseClick(defaultCourse)}>通用模式</Button>
                </div>

                <div className={styles.headerTitle}>
                    <Image src={logoImage} alt="logo" width={36} height={36} className={styles.headerLogo} />
                    <span>AI智学教学辅助平台</span>
                </div>
            </div>

            {/* 课程列表 */}
            <div
                className={styles.courseContainer}
                // 2. 使用 flex: 1 撑满剩余空间，并允许垂直滚动 (overflowY: 'auto')
                style={{
                    marginTop: 16,
                    flex: 1,
                    overflowY: 'auto',
                    paddingRight: '8px' // 留出一点滚动条的空间，防止内容贴边
                }}
            >
                <Spin spinning={loading} style={{ height: '100%' }}>
                    {courses.length > 0 ? (
                        <div className={styles.courseGrid}>
                            <AnimatePresence mode="popLayout">
                                {courses.map((course, index) => (
                                    <CourseCardModal
                                        key={course.id}
                                        course={course}
                                        index={index}
                                        onClick={() => handleCourseClick(course)}
                                    />
                                ))}
                            </AnimatePresence>
                        </div>
                    ) : (
                        !loading && (
                            <motion.div className={styles.emptyState} variants={emptyVariants} initial="hidden" animate="visible">
                                <FolderOutlined className={styles.emptyIcon} />
                                <h3>暂无课程</h3>
                            </motion.div>
                        )
                    )}
                </Spin>
            </div>
        </motion.div>
    );

};

export default WorkspaceModal;