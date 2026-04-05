'use client';

import React, { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Modal, 
  Form, 
  Input, 
  DatePicker, 
  message, 
  Empty,
  Button,
  Checkbox,
  Tag,
  Radio,
  Table,
  Select
} from 'antd';
import { 
  FileTextOutlined, 
  PlusOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  TeamOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ArrowLeftOutlined,
  SendOutlined,
  EyeOutlined,
  UserOutlined
} from '@ant-design/icons';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { 
  getAssignmentList, 
  createAssignment,
  getClassList
} from '@/api/workspace/courseDetail';
import type { 
  Assignment,
  ClassInfo
} from '@/types/workspace/CourseDetailType';
import styles from '@/styles/workspace/courseDetail.module.css';
import {AssignmentDetail, Question, StudentSubmission, SubmissionListItem} from "@/types/assignment/AssignmentType";
import {getAssignmentDetail, getStudentSubmission, getSubmissionList, publishAssignment} from "@/api/assignment";

const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface AssignmentPanelProps {
  courseId: string;
}

// 题目类型标签
const QuestionTypeTag: React.FC<{ type: string }> = ({ type }) => {
  const config: Record<string, { color: string; text: string }> = {
    single: { color: '#1a1a1a', text: '单选' },
    multiple: { color: '#5e5e66', text: '多选' },
    judge: { color: '#86868b', text: '判断' },
  };
  const { color, text } = config[type] || { color: '#86868b', text: type };
  return <Tag style={{ background: color, color: '#fff', border: 'none' }}>{text}</Tag>;
};

const AssignmentPanel: React.FC<AssignmentPanelProps> = ({ courseId }) => {
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [form] = Form.useForm();
  
  // 作业详情弹窗
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [currentAssignment, setCurrentAssignment] = useState<Assignment | null>(null);
  const [assignmentDetail, setAssignmentDetail] = useState<AssignmentDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  
  // 发布弹窗
  const [publishModalVisible, setPublishModalVisible] = useState(false);
  const [publishForm] = Form.useForm();
  const [publishLoading, setPublishLoading] = useState(false);
  const [classes, setClasses] = useState<ClassInfo[]>([]);
  
  // 作答情况弹窗
  const [submissionModalVisible, setSubmissionModalVisible] = useState(false);
  const [submissionList, setSubmissionList] = useState<SubmissionListItem[]>([]);
  const [submissionLoading, setSubmissionLoading] = useState(false);
  
  // 学生作答详情弹窗
  const [studentDetailModalVisible, setStudentDetailModalVisible] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState<SubmissionListItem | null>(null);
  const [studentSubmission, setStudentSubmission] = useState<StudentSubmission | null>(null);
  const [studentDetailLoading, setStudentDetailLoading] = useState(false);

  const fetchAssignments = useCallback(async () => {
    try {
      setLoading(true);
      const result = await getAssignmentList(courseId);
      setAssignments(result);
    } catch (error) {
      console.error('获取作业列表失败:', error);
      message.error('获取作业列表失败');
    } finally {
      setLoading(false);
    }
  }, [courseId]);

  const fetchClasses = useCallback(async () => {
    try {
      const result = await getClassList(courseId);
      setClasses(result);
    } catch (error) {
      console.error('获取班级列表失败:', error);
    }
  }, [courseId]);

  useEffect(() => {
    fetchAssignments();
    fetchClasses();
  }, [fetchAssignments, fetchClasses]);

  const handleCreateAssignment = async (values: {
    title: string;
    description?: string;
    dateRange: [Dayjs, Dayjs];
  }) => {
    try {
      setSubmitLoading(true);
      const [startDate, endDate] = values.dateRange;
      await createAssignment(courseId, {
        title: values.title,
        description: values.description,
        dueDate: endDate.format('YYYY-MM-DD HH:mm'),
        startDate: startDate.format('YYYY-MM-DD HH:mm'),
        status: 'draft',
        totalStudents: 0,
      });
      message.success('创建成功');
      setModalVisible(false);
      form.resetFields();
      fetchAssignments();
    } catch (error) {
      console.error('创建作业失败:', error);
      message.error('创建失败');
    } finally {
      setSubmitLoading(false);
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'draft':
        return '草稿';
      case 'published':
        return '进行中';
      case 'closed':
        return '已结束';
      default:
        return status;
    }
  };

  const formatDate = (dateStr: string) => {
    return dayjs(dateStr).format('MM-DD HH:mm');
  };

  // 点击作业卡片，查看详情
  const handleAssignmentClick = async (assignment: Assignment) => {
    setCurrentAssignment(assignment);
    setDetailLoading(true);
    setDetailModalVisible(true);
    
    try {
      const detail = await getAssignmentDetail(courseId, assignment.id);
      setAssignmentDetail(detail);
    } catch (error) {
      console.error('获取作业详情失败:', error);
      message.error('获取作业详情失败');
    } finally {
      setDetailLoading(false);
    }
  };

  // 打开发布弹窗
  const handleOpenPublish = () => {
    setPublishModalVisible(true);
  };

  // 发布作业
  const handlePublish = async (values: { classIds: string[]; dateRange: [Dayjs, Dayjs] }) => {
    if (!currentAssignment) return;
    
    setPublishLoading(true);
    try {
      const [startTime, endTime] = values.dateRange;
      await publishAssignment(courseId, currentAssignment.id, {
        classIds: values.classIds,
        startTime: startTime.format('YYYY-MM-DD HH:mm'),
        endTime: endTime.format('YYYY-MM-DD HH:mm'),
      });
      message.success('发布成功');
      setPublishModalVisible(false);
      publishForm.resetFields();
      setDetailModalVisible(false);
      fetchAssignments();
    } catch (error) {
      console.error('发布失败:', error);
      message.error('发布失败');
    } finally {
      setPublishLoading(false);
    }
  };

  // 查看作答情况
  const handleViewSubmissions = async () => {
    if (!currentAssignment) return;
    
    setSubmissionLoading(true);
    setSubmissionModalVisible(true);
    
    try {
      const list = await getSubmissionList(courseId, currentAssignment.id);
      setSubmissionList(list);
    } catch (error) {
      console.error('获取作答情况失败:', error);
      message.error('获取作答情况失败');
    } finally {
      setSubmissionLoading(false);
    }
  };

  // 查看学生作答详情
  const handleViewStudentSubmission = async (student: SubmissionListItem) => {
    if (!currentAssignment) return;
    
    setSelectedStudent(student);
    setStudentDetailLoading(true);
    setStudentDetailModalVisible(true);
    
    try {
      const submission = await getStudentSubmission(courseId, currentAssignment.id, student.studentId);
      setStudentSubmission(submission);
    } catch (error) {
      console.error('获取学生作答详情失败:', error);
      message.error('获取学生作答详情失败');
    } finally {
      setStudentDetailLoading(false);
    }
  };

  // 渲染题目选项
  const renderOptions = (question: Question, userAnswer?: string | string[]) => {
    const isMultiple = question.type === 'multiple';
    const correctAnswer = question.answer;
    
    return (
      <div style={{ marginTop: 12 }}>
        {question.options.map((option, idx) => {
          const optionLabel = String.fromCharCode(65 + idx); // A, B, C, D
          const isSelected = userAnswer 
            ? (Array.isArray(userAnswer) ? userAnswer.includes(option) : userAnswer === option)
            : false;
          const isCorrect = Array.isArray(correctAnswer) 
            ? correctAnswer.includes(option) 
            : correctAnswer === option;
          
          let style: React.CSSProperties = {
            padding: '10px 14px',
            marginBottom: 8,
            borderRadius: 8,
            border: '1px solid #eef0f2',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            transition: 'all 0.2s',
          };
          
          if (userAnswer !== undefined) {
            // 有答案时显示对错状态
            if (isSelected && isCorrect) {
              style = { ...style, background: 'rgba(34, 197, 94, 0.1)', borderColor: '#22c55e' };
            } else if (isSelected && !isCorrect) {
              style = { ...style, background: 'rgba(239, 68, 68, 0.1)', borderColor: '#ef4444' };
            } else if (isCorrect) {
              style = { ...style, background: 'rgba(34, 197, 94, 0.05)', borderColor: '#86efac' };
            }
          }
          
          return (
            <div key={idx} style={style}>
              {isMultiple ? (
                <Checkbox checked={isSelected} disabled />
              ) : (
                <Radio checked={isSelected} disabled />
              )}
              <span style={{ flex: 1 }}><strong>{optionLabel}.</strong> {option}</span>
              {userAnswer !== undefined && isSelected && (
                isCorrect 
                  ? <CheckCircleOutlined style={{ color: '#22c55e' }} />
                  : <CloseCircleOutlined style={{ color: '#ef4444' }} />
              )}
            </div>
          );
        })}
      </div>
    );
  };

  // 学生列表表格列
  const submissionColumns = [
    {
      title: '学号',
      dataIndex: 'studentId',
      key: 'studentId',
      width: 120,
    },
    {
      title: '姓名',
      dataIndex: 'studentName',
      key: 'studentName',
      width: 100,
      render: (name: string) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <div style={{
            width: 28,
            height: 28,
            borderRadius: '50%',
            background: '#1a1a1a',
            color: '#fff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: 12,
          }}>
            {name.charAt(0)}
          </div>
          <span>{name}</span>
        </div>
      ),
    },
    {
      title: '班级',
      dataIndex: 'className',
      key: 'className',
      width: 120,
    },
    {
      title: '提交状态',
      dataIndex: 'submitStatus',
      key: 'submitStatus',
      width: 100,
      render: (status: string) => (
        status === 'completed' 
          ? <Tag color="success">已提交</Tag>
          : <Tag color="default">未提交</Tag>
      ),
    },
    {
      title: '提交时间',
      dataIndex: 'submitTime',
      key: 'submitTime',
      width: 160,
      render: (time?: string) => time || '-',
    },
    {
      title: '得分',
      dataIndex: 'score',
      key: 'score',
      width: 80,
      render: (score?: number) => score !== undefined ? (
        <span style={{ fontWeight: 600, color: '#1a1a1a' }}>{score}</span>
      ) : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: unknown, record: SubmissionListItem) => (
        record.submitStatus === 'completed' ? (
          <Button 
            type="link" 
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewStudentSubmission(record)}
          >
            查看
          </Button>
        ) : null
      ),
    },
  ];

  return (
    <div className={styles.assignmentContainer}>
      {/* 工具栏 */}
      <motion.div 
        className={styles.assignmentToolbar}
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <div className={styles.assignmentTitle}>
          <FileTextOutlined style={{ marginRight: 8 }} />
          作业练习
        </div>
        <button 
          className={styles.generateButton}
          onClick={() => setModalVisible(true)}
        >
          <PlusOutlined />
          生成试卷/作业
        </button>
      </motion.div>

      {/* 作业卡片列表 */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>
      ) : assignments.length === 0 ? (
        <motion.div
          className={styles.emptyContainer}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <div className={styles.emptyIcon}>
            <FileTextOutlined />
          </div>
          <div className={styles.emptyText}>暂无作业，点击上方按钮创建</div>
          <button 
            className={styles.generateButton}
            onClick={() => setModalVisible(true)}
          >
            <PlusOutlined />
            生成试卷/作业
          </button>
        </motion.div>
      ) : (
        <div className={styles.assignmentGrid}>
          <AnimatePresence>
            {assignments.map((assignment, index) => (
              <motion.div
                key={assignment.id}
                className={styles.assignmentCard}
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
                transition={{ duration: 0.25, delay: index * 0.05 }}
                layout
                onClick={() => handleAssignmentClick(assignment)}
              >
                <div className={styles.assignmentCardHeader}>
                  <div>
                    <div className={styles.assignmentName}>{assignment.title}</div>
                    {assignment.description && (
                      <div className={styles.assignmentDesc}>{assignment.description}</div>
                    )}
                  </div>
                  <span className={`${styles.assignmentStatus} ${styles[assignment.status]}`}>
                    {getStatusText(assignment.status)}
                  </span>
                </div>
                <div className={styles.assignmentMeta}>
                  <div className={styles.assignmentMetaItem}>
                    <CalendarOutlined className={styles.assignmentMetaIcon} />
                    <span>开始: {formatDate(assignment.startDate || '')}</span>
                  </div>
                  <div className={styles.assignmentMetaItem}>
                    <ClockCircleOutlined className={styles.assignmentMetaIcon} />
                    <span>截止: {formatDate(assignment.dueDate)}</span>
                  </div>
                </div>
                {assignment.totalStudents > 0 && (
                  <div className={styles.assignmentProgress}>
                    <div className={styles.progressLabel}>
                      <span>
                        <TeamOutlined style={{ marginRight: 4 }} />
                        提交进度
                      </span>
                      <span>{assignment.submissionCount}/{assignment.totalStudents}</span>
                    </div>
                    <div className={styles.progressBar}>
                      <div 
                        className={styles.progressFill}
                        style={{ 
                          width: `${(assignment.submissionCount / assignment.totalStudents) * 100}%` 
                        }}
                      />
                    </div>
                  </div>
                )}
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}

      {/* 生成作业弹窗 */}
      <Modal
        title="生成试卷/作业"
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
        width={520}
        centered
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleCreateAssignment}
          style={{ marginTop: 20 }}
        >
          <Form.Item
            name="title"
            label="课程名称"
            rules={[{ required: true, message: '请输入课程/作业名称' }]}
          >
            <Input placeholder="请输入课程/作业名称" />
          </Form.Item>
          <Form.Item
            name="description"
            label="题目要求"
          >
            <TextArea 
              placeholder="请输入题目要求或说明（可选）" 
              rows={4}
              showCount
              maxLength={500}
            />
          </Form.Item>
          <Form.Item
            name="dateRange"
            label="起止时间"
            rules={[{ required: true, message: '请选择起止时间' }]}
          >
            <RangePicker 
              showTime={{ format: 'HH:mm' }}
              format="YYYY-MM-DD HH:mm"
              style={{ width: '100%' }}
              placeholder={['开始时间', '结束时间']}
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 0, marginTop: 24 }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <Button onClick={() => {
                setModalVisible(false);
                form.resetFields();
              }}>
                取消
              </Button>
              <Button 
                type="primary" 
                htmlType="submit" 
                loading={submitLoading}
                style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
              >
                确定生成
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* 作业详情弹窗 */}
      <Modal
        title={null}
        open={detailModalVisible}
        onCancel={() => {
          setDetailModalVisible(false);
          setCurrentAssignment(null);
          setAssignmentDetail(null);
        }}
        footer={null}
        width={800}
        centered
        style={{ maxHeight: '85vh' }}
        styles={{ body: { maxHeight: 'calc(85vh - 60px)', overflowY: 'auto' } }}
      >
        {detailLoading ? (
          <div style={{ textAlign: 'center', padding: 60 }}>加载中...</div>
        ) : assignmentDetail ? (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25 }}
          >
            {/* 头部 */}
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'space-between',
              marginBottom: 24,
              paddingBottom: 20,
              borderBottom: '1px solid #eef0f2'
            }}>
              <div>
                <h2 style={{ fontSize: 20, fontWeight: 600, color: '#1a1a1a', margin: 0, marginBottom: 8 }}>
                  {assignmentDetail.title}
                </h2>
                <div style={{ display: 'flex', gap: 16, fontSize: 14, color: '#86868b' }}>
                  <span>共 {assignmentDetail.questions.length} 题</span>
                  <span>满分 {assignmentDetail.totalScore} 分</span>
                </div>
              </div>
              <div style={{ display: 'flex', gap: 12 }}>
                {currentAssignment?.status === 'draft' && (
                  <Button 
                    type="primary" 
                    icon={<SendOutlined />}
                    onClick={handleOpenPublish}
                    style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
                  >
                    发布作业
                  </Button>
                )}
                {(currentAssignment?.status === 'published' || currentAssignment?.status === 'closed') && (
                  <Button 
                    icon={<TeamOutlined />}
                    onClick={handleViewSubmissions}
                  >
                    作答情况
                  </Button>
                )}
              </div>
            </div>

            {/* 题目列表 */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              {assignmentDetail.questions.map((question, idx) => (
                <motion.div
                  key={question.id}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: idx * 0.05 }}
                  style={{
                    padding: 20,
                    background: '#f7f7f8',
                    borderRadius: 12,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12, marginBottom: 8 }}>
                    <span style={{ 
                      fontWeight: 600, 
                      color: '#1a1a1a',
                      background: '#fff',
                      padding: '2px 10px',
                      borderRadius: 6,
                      fontSize: 14,
                    }}>
                      {idx + 1}
                    </span>
                    <QuestionTypeTag type={question.type} />
                    <span style={{ 
                      marginLeft: 'auto', 
                      fontSize: 13, 
                      color: '#86868b',
                      background: '#fff',
                      padding: '2px 8px',
                      borderRadius: 4,
                    }}>
                      {question.score} 分
                    </span>
                  </div>
                  <div style={{ fontSize: 15, color: '#1a1a1a', lineHeight: 1.6, marginBottom: 8 }}>
                    {question.title}
                  </div>
                  {renderOptions(question)}
                  <div style={{ 
                    marginTop: 16, 
                    padding: 12, 
                    background: '#fff', 
                    borderRadius: 8,
                    borderLeft: '3px solid #1a1a1a'
                  }}>
                    <div style={{ fontSize: 13, color: '#1a1a1a', fontWeight: 500, marginBottom: 4 }}>
                      正确答案：
                      <span style={{ color: '#22c55e', fontWeight: 600 }}>
                        {Array.isArray(question.answer) ? question.answer.join('、') : question.answer}
                      </span>
                    </div>
                    {question.analysis && (
                      <div style={{ fontSize: 13, color: '#86868b', marginTop: 8 }}>
                        <strong>解析：</strong>{question.analysis}
                      </div>
                    )}
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        ) : null}
      </Modal>

      {/* 发布作业弹窗 */}
      <Modal
        title="发布作业"
        open={publishModalVisible}
        onCancel={() => {
          setPublishModalVisible(false);
          publishForm.resetFields();
        }}
        footer={null}
        width={480}
        centered
      >
        <Form
          form={publishForm}
          layout="vertical"
          onFinish={handlePublish}
          style={{ marginTop: 20 }}
        >
          <Form.Item
            name="classIds"
            label="选择发布班级"
            rules={[{ required: true, message: '请选择发布班级' }]}
          >
            <Select
              mode="multiple"
              placeholder="请选择要发布的班级"
              options={classes.map(c => ({ label: c.name, value: c.id }))}
            />
          </Form.Item>
          <Form.Item
            name="dateRange"
            label="作业时间"
            rules={[{ required: true, message: '请选择开始和截止时间' }]}
          >
            <RangePicker 
              showTime={{ format: 'HH:mm' }}
              format="YYYY-MM-DD HH:mm"
              style={{ width: '100%' }}
              placeholder={['开始时间', '截止时间']}
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 0, marginTop: 24 }}>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <Button onClick={() => {
                setPublishModalVisible(false);
                publishForm.resetFields();
              }}>
                取消
              </Button>
              <Button 
                type="primary" 
                htmlType="submit" 
                loading={publishLoading}
                icon={<SendOutlined />}
                style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
              >
                确定发布
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>

      {/* 作答情况弹窗 */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <TeamOutlined />
            <span>作答情况 - {currentAssignment?.title}</span>
          </div>
        }
        open={submissionModalVisible}
        onCancel={() => {
          setSubmissionModalVisible(false);
          setSubmissionList([]);
        }}
        footer={null}
        width={900}
        centered
      >
        {submissionLoading ? (
          <div style={{ textAlign: 'center', padding: 60 }}>加载中...</div>
        ) : (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.25 }}
          >
            {/* 统计信息 */}
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'repeat(3, 1fr)', 
              gap: 16, 
              marginBottom: 20,
              padding: 16,
              background: '#f7f7f8',
              borderRadius: 12
            }}>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 24, fontWeight: 600, color: '#1a1a1a' }}>
                  {submissionList.length}
                </div>
                <div style={{ fontSize: 13, color: '#86868b' }}>总人数</div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 24, fontWeight: 600, color: '#22c55e' }}>
                  {submissionList.filter(s => s.submitStatus === 'completed').length}
                </div>
                <div style={{ fontSize: 13, color: '#86868b' }}>已提交</div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 24, fontWeight: 600, color: '#ef4444' }}>
                  {submissionList.filter(s => s.submitStatus === 'not_submitted').length}
                </div>
                <div style={{ fontSize: 13, color: '#86868b' }}>未提交</div>
              </div>
            </div>
            
            <Table
              dataSource={submissionList}
              columns={submissionColumns}
              rowKey="studentId"
              pagination={false}
              size="middle"
            />
          </motion.div>
        )}
      </Modal>

      {/* 学生作答详情弹窗 */}
      <Modal
        title={null}
        open={studentDetailModalVisible}
        onCancel={() => {
          setStudentDetailModalVisible(false);
          setSelectedStudent(null);
          setStudentSubmission(null);
        }}
        footer={null}
        width={800}
        centered
        style={{ maxHeight: '85vh' }}
        styles={{ body: { maxHeight: 'calc(85vh - 60px)', overflowY: 'auto' } }}
      >
        {studentDetailLoading ? (
          <div style={{ textAlign: 'center', padding: 60 }}>加载中...</div>
        ) : studentSubmission && selectedStudent ? (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25 }}
          >
            {/* 头部 */}
            <div style={{ marginBottom: 24, paddingBottom: 20, borderBottom: '1px solid #eef0f2' }}>
              <Button 
                type="text" 
                icon={<ArrowLeftOutlined />} 
                onClick={() => {
                  setStudentDetailModalVisible(false);
                  setSelectedStudent(null);
                  setStudentSubmission(null);
                }}
                style={{ marginBottom: 16, padding: 0 }}
              >
                返回列表
              </Button>
              <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                <div style={{
                  width: 48,
                  height: 48,
                  borderRadius: '50%',
                  background: '#1a1a1a',
                  color: '#fff',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 20,
                  fontWeight: 500,
                }}>
                  {selectedStudent.studentName.charAt(0)}
                </div>
                <div>
                  <h2 style={{ fontSize: 20, fontWeight: 600, color: '#1a1a1a', margin: 0 }}>
                    {selectedStudent.studentName}
                  </h2>
                  <div style={{ fontSize: 14, color: '#86868b' }}>
                    {selectedStudent.studentId} · {selectedStudent.className}
                  </div>
                </div>
                <div style={{ marginLeft: 'auto', textAlign: 'right' }}>
                  <div style={{ fontSize: 32, fontWeight: 600, color: '#1a1a1a' }}>
                    {studentSubmission.userScore}
                    <span style={{ fontSize: 16, color: '#86868b', fontWeight: 400 }}>
                      /{studentSubmission.totalScore}
                    </span>
                  </div>
                  <div style={{ fontSize: 13, color: '#86868b' }}>
                    提交时间: {studentSubmission.submitTime}
                  </div>
                </div>
              </div>
            </div>

            {/* 作答详情 */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              {studentSubmission.answers.map((answer, idx) => {
                // 找到对应的题目信息（这里简化处理）
                const question: Question = {
                  id: answer.questionId,
                  type: answer.type,
                  title: `题目 ${answer.questionId}`,
                  options: answer.type === 'judge' 
                    ? ['正确', '错误'] 
                    : ['选项A', '选项B', '选项C', '选项D'],
                  answer: answer.correctAnswer,
                  score: answer.score,
                };
                
                return (
                  <motion.div
                    key={answer.questionId}
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: idx * 0.03 }}
                    style={{
                      padding: 16,
                      background: answer.isCorrect ? 'rgba(34, 197, 94, 0.05)' : 'rgba(239, 68, 68, 0.05)',
                      borderRadius: 12,
                      border: `1px solid ${answer.isCorrect ? '#86efac' : '#fca5a5'}`,
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <span style={{ 
                          fontWeight: 600, 
                          color: '#1a1a1a',
                          background: '#fff',
                          padding: '2px 10px',
                          borderRadius: 6,
                        }}>
                          {idx + 1}
                        </span>
                        <QuestionTypeTag type={answer.type} />
                        {answer.isCorrect ? (
                          <Tag color="success" icon={<CheckCircleOutlined />}>正确</Tag>
                        ) : (
                          <Tag color="error" icon={<CloseCircleOutlined />}>错误</Tag>
                        )}
                      </div>
                      <div style={{ fontSize: 14 }}>
                        <span style={{ color: answer.isCorrect ? '#22c55e' : '#ef4444', fontWeight: 600 }}>
                          {answer.userScore}
                        </span>
                        <span style={{ color: '#86868b' }}>/{answer.score} 分</span>
                      </div>
                    </div>
                    
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                      <div style={{ padding: 12, background: '#fff', borderRadius: 8 }}>
                        <div style={{ fontSize: 12, color: '#86868b', marginBottom: 4 }}>学生答案</div>
                        <div style={{ fontSize: 14, color: '#1a1a1a', fontWeight: 500 }}>
                          {Array.isArray(answer.userAnswer) ? answer.userAnswer.join('、') : answer.userAnswer}
                        </div>
                      </div>
                      <div style={{ padding: 12, background: '#fff', borderRadius: 8 }}>
                        <div style={{ fontSize: 12, color: '#86868b', marginBottom: 4 }}>正确答案</div>
                        <div style={{ fontSize: 14, color: '#22c55e', fontWeight: 500 }}>
                          {Array.isArray(answer.correctAnswer) ? answer.correctAnswer.join('、') : answer.correctAnswer}
                        </div>
                      </div>
                    </div>
                  </motion.div>
                );
              })}
            </div>
          </motion.div>
        ) : null}
      </Modal>
    </div>
  );
};

export default AssignmentPanel;
