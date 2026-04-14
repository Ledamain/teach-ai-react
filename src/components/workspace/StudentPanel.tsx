'use client';

import React, { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Input, 
  Select, 
  Button, 
  Checkbox, 
  Modal, 
  Form, 
  message, 
  Popconfirm,
  Empty 
} from 'antd';
import { 
  SearchOutlined, 
  PlusOutlined, 
  DeleteOutlined,
  UserOutlined,
  BulbOutlined,
  BookOutlined,
  ClockCircleOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  MinusOutlined
} from '@ant-design/icons';
import {
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Area,
  AreaChart,
} from 'recharts';
import {
  addStudent,
  deleteStudents,
  getStudentDetail
} from '@/api/workspace/courseDetail';
import CourseDetailApi from '@/api/workspace/courseDetail'
import ClassesApi from '@/api/classes'
import type { Student, ClassInfo, StudentDetail } from '@/types/workspace/CourseDetailType';
import styles from '@/styles/workspace/courseDetail.module.css';
import {CLassesType} from "@/types/classes/ClassesType";

interface StudentPanelProps {
  courseId: string;
}

const COLORS = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'];

const StudentPanel: React.FC<StudentPanelProps> = ({ courseId }) => {
  const [students, setStudents] = useState<Student[]>([]);
  const [classes, setClasses] = useState<CLassesType[]>([]);
  const [loading, setLoading] = useState(true);
  const [keyword, setKeyword] = useState('');
  const [selectedClass, setSelectedClass] = useState<string | undefined>();
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState<StudentDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [form] = Form.useForm();

  const fetchStudents = useCallback(async () => {
    try {
      setLoading(true);
      const result =  await CourseDetailApi.getStudentListForClient(Number(courseId), {
        keyword,
        classId: selectedClass,
      });
      setStudents(result.list);
    } catch (error) {
      console.error('获取学生列表失败:', error);
      message.error('获取学生列表失败');
    } finally {
      setLoading(false);
    }
  }, [courseId, keyword, selectedClass]);

  const fetchClasses = useCallback(async () => {
    try {
      const result = await ClassesApi.getClassesListByRepoCategoryId(Number(courseId));
      setClasses(result);
    } catch (error) {
      console.error('获取班级列表失败:', error);
    }
  }, [courseId]);

  useEffect(() => {
    fetchStudents();
    fetchClasses();
  }, [fetchStudents, fetchClasses]);

  const handleSearch = (value: string) => {
    setKeyword(value);
  };

  const handleClassChange = (value: string) => {
    setSelectedClass(value || undefined);
  };

  const handleSelectStudent = (id: string, checked: boolean) => {
    if (checked) {
      setSelectedIds([...selectedIds, id]);
    } else {
      setSelectedIds(selectedIds.filter(sid => sid !== id));
    }
  };

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedIds(students.map(s => s.id));
    } else {
      setSelectedIds([]);
    }
  };

  const handleBatchDelete = async () => {
    if (selectedIds.length === 0) {
      message.warning('请先选择要删除的学生');
      return;
    }

    try {
      await deleteStudents(courseId, selectedIds);
      message.success('删除成功');
      setSelectedIds([]);
      fetchStudents();
    } catch (error) {
      console.error('删除学生失败:', error);
      message.error('删除失败');
    }
  };

  const handleDeleteStudent = async (studentId: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await deleteStudents(courseId, [studentId]);
      message.success('删除成功');
      fetchStudents();
    } catch (error) {
      console.error('删除学生失败:', error);
      message.error('删除失败');
    }
  };

  const handleAddStudent = async (values: { studentId: string; name: string; className: string }) => {
    try {
      await addStudent(courseId, values);
      message.success('添加成功');
      setModalVisible(false);
      form.resetFields();
      fetchStudents();
    } catch (error) {
      console.error('添加学生失败:', error);
      message.error('添加失败');
    }
  };

  // 点击学生卡片，打开详情弹窗
  const handleStudentClick = async (student: Student) => {
    setDetailLoading(true);
    setDetailModalVisible(true);
    try {
      const detail = await getStudentDetail(courseId, student.studentUserId);
      setSelectedStudent(detail);
    } catch (error) {
      console.error('获取学生详情失败:', error);
      message.error('获取学生详情失败');
    } finally {
      setDetailLoading(false);
    }
  };

  const getTrendIcon = (trend: 'up' | 'down' | 'stable') => {
    switch (trend) {
      case 'up':
        return <ArrowUpOutlined style={{ color: '#16a34a' }} />;
      case 'down':
        return <ArrowDownOutlined style={{ color: '#dc2626' }} />;
      default:
        return <MinusOutlined style={{ color: '#86868b' }} />;
    }
  };

  return (
    <div className={styles.studentContainer}>
      {/* 工具栏 */}
      <motion.div 
        className={styles.studentToolbar}
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <Input
          className={styles.searchInput}
          placeholder="搜索学号/姓名"
          prefix={<SearchOutlined />}
          allowClear
          onChange={e => handleSearch(e.target.value)}
        />
        <Select
          className={styles.classSelect}
          placeholder="按班级筛选"
          allowClear
          onChange={handleClassChange}
          options={classes.map(c => ({ label: c.classesName, value: c.classesName }))}
        />
        <Checkbox
          checked={selectedIds.length === students.length && students.length > 0}
          indeterminate={selectedIds.length > 0 && selectedIds.length < students.length}
          onChange={e => handleSelectAll(e.target.checked)}
        >
          全选
        </Checkbox>
        <div className={styles.toolbarRight}>
          {selectedIds.length > 0 && (
            <Popconfirm
              title="确定要删除选中的学生吗？"
              onConfirm={handleBatchDelete}
              okText="确定"
              cancelText="取消"
            >
              <Button danger icon={<DeleteOutlined />}>
                批量删除 ({selectedIds.length})
              </Button>
            </Popconfirm>
          )}
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalVisible(true)}
            style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
          >
            添加学生
          </Button>
        </div>
      </motion.div>

      {/* 学生卡片列表 */}
      {loading ? (
        <div>加载中...</div>
      ) : students.length === 0 ? (
        <Empty description="暂无学生数据" />
      ) : (
        <div className={styles.studentGrid}>
          <AnimatePresence>
            {students.map((student, index) => (
              <motion.div
                key={student.id}
                className={`${styles.studentCard} ${selectedIds.includes(student.id) ? styles.selected : ''}`}
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ duration: 0.25, delay: index * 0.05 }}
                layout
                onClick={() => handleStudentClick(student)}
              >
                <Checkbox
                  className={styles.checkbox}
                  checked={selectedIds.includes(student.id)}
                  onChange={e => {
                    e.stopPropagation();
                    handleSelectStudent(student.id, e.target.checked);
                  }}
                  onClick={e => e.stopPropagation()}
                />
                <div className={styles.studentActions}>
                  <Popconfirm
                    title="确定要删除该学生吗？"
                    onConfirm={(e) => handleDeleteStudent(student.id, e as React.MouseEvent)}
                    onCancel={e => e?.stopPropagation()}
                    okText="确定"
                    cancelText="取消"
                  >
                    <Button 
                      type="text" 
                      danger 
                      size="small" 
                      icon={<DeleteOutlined />} 
                      onClick={e => e.stopPropagation()}
                    />
                  </Popconfirm>
                </div>
                <div className={styles.studentCardHeader}>
                  <div className={styles.studentAvatar}>
                    {student.clientAvator ? (
                        <img
                            src={student.clientAvator}
                            alt="头像"
                            className={styles.avatarImg}
                            onError={(e) => {
                              e.target.style.display = 'none';
                              e.target.parentElement.innerText = student.nickname?.charAt(0) || '';
                            }}
                        />
                    ) : (
                        student.nickname?.charAt(0) || ''
                    )}
                  </div>
                  <div className={styles.studentInfo}>
                    <div className={styles.studentName}>{student.nickname}</div>
                    <div className={styles.studentId}>{student.clientNum}</div>
                  </div>
                </div>
                <div className={styles.studentMeta}>
                  <span>{student.classesName}</span>
                  <span>提问 {student.recordCount || 0} 次</span>
                </div>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}

      {/* 添加学生弹窗 */}
      <Modal
        title="添加学生"
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleAddStudent}
        >
          <Form.Item
            name="studentId"
            label="学号"
            rules={[{ required: true, message: '请输入学号' }]}
          >
            <Input placeholder="请输入学号" />
          </Form.Item>
          <Form.Item
            name="name"
            label="姓名"
            rules={[{ required: true, message: '请输入姓名' }]}
          >
            <Input placeholder="请输入姓名" prefix={<UserOutlined />} />
          </Form.Item>
          <Form.Item
            name="className"
            label="班级"
            rules={[{ required: true, message: '请选择班级' }]}
          >
            <Select
              placeholder="请选择班级"
              options={classes.map(c => ({ label: c.classesName, value: c.id }))}
            />
          </Form.Item>
          <Form.Item>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <Button onClick={() => {
                setModalVisible(false);
                form.resetFields();
              }}>
                取消
              </Button>
              <Button type="primary" htmlType="submit" style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}>
                确定
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* 学生详情弹窗 */}
      <Modal
        title={selectedStudent ? `${selectedStudent.name} - 学习分析` : '学生详情'}
        open={detailModalVisible}
        onCancel={() => {
          setDetailModalVisible(false);
          setSelectedStudent(null);
        }}
        footer={null}
        width={720}
        centered
      >
        {detailLoading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>
        ) : selectedStudent ? (
          <div className={styles.studentDetailModal}>
            {/* 顶部统计 */}
            <div className={styles.studentDetailHeader}>
              <div className={styles.detailStatCard}>
                <div className={styles.detailStatValue}>{selectedStudent.totalQuestions}</div>
                <div className={styles.detailStatLabel}>总提问数</div>
              </div>
              <div className={styles.detailStatCard}>
                <div className={styles.detailStatValue}>{selectedStudent.mainSubject}</div>
                <div className={styles.detailStatLabel}>主要学科</div>
              </div>
              <div className={styles.detailStatCard}>
                <div className={styles.detailStatValue}>{selectedStudent.lastActive}</div>
                <div className={styles.detailStatLabel}>最后活跃</div>
              </div>
            </div>

            {/* 图表区域 */}
            <div className={styles.studentDetailCharts}>
              {/* 问题复杂度分布 */}
              <div className={styles.detailChartCard}>
                <div className={styles.detailChartTitle}>问题复杂度分布</div>
                <div className={styles.detailChartContent}>
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={selectedStudent.complexityDistribution}
                        cx="50%"
                        cy="50%"
                        innerRadius={40}
                        outerRadius={60}
                        paddingAngle={2}
                        dataKey="count"
                        nameKey="level"
                      >
                        {selectedStudent.complexityDistribution.map((_, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip 
                        contentStyle={{ 
                          background: '#fff', 
                          border: '1px solid #eef0f2',
                          borderRadius: 8,
                          boxShadow: '0 4px 12px rgba(0,0,0,0.08)'
                        }}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
              </div>

              {/* 学情分析趋势 */}
              <div className={styles.detailChartCard}>
                <div className={styles.detailChartTitle}>学情分析趋势</div>
                <div className={styles.detailChartContent}>
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={selectedStudent.learningTrend}>
                      <defs>
                        <linearGradient id="learningGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor="#1a1a1a" stopOpacity={0.1} />
                          <stop offset="100%" stopColor="#1a1a1a" stopOpacity={0} />
                        </linearGradient>
                      </defs>
                      <CartesianGrid strokeDasharray="3 3" stroke="#f2f2f7" />
                      <XAxis dataKey="date" stroke="#86868b" fontSize={10} tickLine={false} />
                      <YAxis stroke="#86868b" fontSize={10} tickLine={false} axisLine={false} />
                      <Tooltip 
                        contentStyle={{ 
                          background: '#fff', 
                          border: '1px solid #eef0f2',
                          borderRadius: 8,
                          boxShadow: '0 4px 12px rgba(0,0,0,0.08)'
                        }}
                      />
                      <Area 
                        type="monotone" 
                        dataKey="score" 
                        stroke="#3b82f6"
                        strokeWidth={2}
                        fill="url(#learningGradient)"
                        name="学习指数"
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>

            {/* 学习建议 */}
            <div className={styles.learningAdvice}>
              <div className={styles.adviceTitle}>
                <BulbOutlined />
                学习建议
              </div>
              <div className={styles.adviceContent}>
                {selectedStudent.learningAdvice.map((advice, index) => (
                  <div key={index} className={styles.adviceItem}>
                    <span className={styles.adviceIcon}>
                      {index === 0 ? <BookOutlined /> : <ClockCircleOutlined />}
                    </span>
                    <span>{advice}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ) : null}
      </Modal>
    </div>
  );
};

export default StudentPanel;
