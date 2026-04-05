'use client';

import React, { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Modal, Button, Checkbox, Radio, Tag, message, Empty } from 'antd';
import {
  FileTextOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ArrowLeftOutlined,
  SendOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import {
  getStudentAssignmentList,
  getStudentAssignmentDetail,
  submitStudentAssignment,
} from '@/api/studentWorkspace';
import type {
  StudentAssignment,
  StudentAssignmentDetail,
  StudentQuestion,
} from '@/types/studentWorkspace/StudentWorkspaceType';
import styles from '@/styles/studentWorkspace/index.module.css';

interface StudentAssignmentPanelProps {
  courseId: number;
  studentId: string;
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

const StudentAssignmentPanel: React.FC<StudentAssignmentPanelProps> = ({
  courseId,
  studentId,
}) => {
  const [assignments, setAssignments] = useState<StudentAssignment[]>([]);
  const [loading, setLoading] = useState(true);

  // 作业详情/答题弹窗
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [currentAssignment, setCurrentAssignment] = useState<StudentAssignment | null>(null);
  const [assignmentDetail, setAssignmentDetail] = useState<StudentAssignmentDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // 学生作答
  const [userAnswers, setUserAnswers] = useState<Record<number, string | string[]>>({});
  const [submitLoading, setSubmitLoading] = useState(false);

  // 提交结果展示
  const [showResult, setShowResult] = useState(false);

  const fetchAssignments = useCallback(async () => {
    try {
      setLoading(true);
      const result = await getStudentAssignmentList(courseId, studentId);
      setAssignments(result);
    } catch (error) {
      console.error('获取作业列表失败:', error);
      message.error('获取作业列表失败');
    } finally {
      setLoading(false);
    }
  }, [courseId, studentId]);

  useEffect(() => {
    fetchAssignments();
  }, [fetchAssignments]);

  const getStatusText = (status: string, submitStatus: string) => {
    if (status === 'closed') return '已结束';
    if (submitStatus === 'completed') return '已提交';
    if (submitStatus === 'in_progress') return '答题中';
    return '进行中';
  };

  const getStatusClass = (status: string, submitStatus: string) => {
    if (status === 'closed') return styles.statusClosed;
    if (submitStatus === 'completed') return styles.statusCompleted;
    return styles.statusPublished;
  };

  const formatDate = (dateStr: string) => {
    return dayjs(dateStr).format('MM-DD HH:mm');
  };

  // 点击作业卡片
  const handleAssignmentClick = async (assignment: StudentAssignment) => {
    setCurrentAssignment(assignment);
    setDetailLoading(true);
    setDetailModalVisible(true);
    setUserAnswers({});
    setShowResult(false);

    try {
      const detail = await getStudentAssignmentDetail(courseId, assignment.id, studentId);
      setAssignmentDetail(detail);

      // 如果已提交，直接显示结果
      if (detail.submitStatus === 'completed') {
        setShowResult(true);
      }
    } catch (error) {
      console.error('获取作业详情失败:', error);
      message.error('获取作业详情失败');
    } finally {
      setDetailLoading(false);
    }
  };

  // 处理单选/判断答案
  const handleSingleAnswer = (questionId: number, value: string) => {
    setUserAnswers((prev) => ({
      ...prev,
      [questionId]: value,
    }));
  };

  // 处理多选答案
  const handleMultipleAnswer = (questionId: number, values: string[]) => {
    setUserAnswers((prev) => ({
      ...prev,
      [questionId]: values,
    }));
  };

  // 提交作业
  const handleSubmit = async () => {
    if (!assignmentDetail) return;

    // 检查是否所有题目都已作答
    const unanswered = assignmentDetail.questions.filter(
      (q) => !userAnswers[q.id] || (Array.isArray(userAnswers[q.id]) && (userAnswers[q.id] as string[]).length === 0)
    );

    if (unanswered.length > 0) {
      message.warning(`还有 ${unanswered.length} 道题目未作答`);
      return;
    }

    setSubmitLoading(true);
    try {
      const result = await submitStudentAssignment(courseId, studentId, {
        assignmentId: assignmentDetail.id,
        answers: Object.entries(userAnswers).map(([questionId, answer]) => ({
          questionId: parseInt(questionId),
          answer,
        })),
      });

      if (result.success) {
        message.success('提交成功');

        // 更新详情以显示结果
        const updatedQuestions = assignmentDetail.questions.map((q) => {
          const resultQ = result.questions.find((rq) => rq.id === q.id);
          return {
            ...q,
            answer: resultQ?.answer,
            analysis: resultQ?.analysis,
            userAnswer: resultQ?.userAnswer,
            isCorrect: resultQ?.isCorrect,
            userScore: resultQ?.userScore,
          };
        });

        setAssignmentDetail({
          ...assignmentDetail,
          submitStatus: 'completed',
          userScore: result.userScore,
          submitTime: result.submitTime,
          questions: updatedQuestions,
        });

        setShowResult(true);
        fetchAssignments(); // 刷新列表
      }
    } catch (error) {
      console.error('提交失败:', error);
      message.error('提交失败');
    } finally {
      setSubmitLoading(false);
    }
  };

  // 渲染答题选项
  const renderAnswerOptions = (question: StudentQuestion) => {
    const isMultiple = question.type === 'multiple';
    const currentAnswer = userAnswers[question.id];

    return (
      <div style={{ marginTop: 12 }}>
        {question.options.map((option, idx) => {
          const optionLabel = String.fromCharCode(65 + idx);
          const isSelected = isMultiple
            ? (currentAnswer as string[] | undefined)?.includes(option)
            : currentAnswer === option;

          return (
            <div
              key={idx}
              className={`${styles.optionItem} ${isSelected ? styles.optionSelected : ''}`}
              onClick={() => {
                if (isMultiple) {
                  const current = (currentAnswer as string[]) || [];
                  if (current.includes(option)) {
                    handleMultipleAnswer(
                      question.id,
                      current.filter((o) => o !== option)
                    );
                  } else {
                    handleMultipleAnswer(question.id, [...current, option]);
                  }
                } else {
                  handleSingleAnswer(question.id, option);
                }
              }}
            >
              {isMultiple ? (
                <Checkbox checked={isSelected} />
              ) : (
                <Radio checked={isSelected} />
              )}
              <span style={{ flex: 1 }}>
                <strong>{optionLabel}.</strong> {option}
              </span>
            </div>
          );
        })}
      </div>
    );
  };

  // 渲染结果选项（显示对错）
  const renderResultOptions = (question: StudentQuestion) => {
    const isMultiple = question.type === 'multiple';
    const userAnswer = question.userAnswer;
    const correctAnswer = question.answer;

    return (
      <div style={{ marginTop: 12 }}>
        {question.options.map((option, idx) => {
          const optionLabel = String.fromCharCode(65 + idx);
          const isSelected = Array.isArray(userAnswer)
            ? userAnswer.includes(option)
            : userAnswer === option;
          const isCorrect = Array.isArray(correctAnswer)
            ? correctAnswer.includes(option)
            : correctAnswer === option;

          let className = styles.optionItem;
          if (isSelected && isCorrect) {
            className += ` ${styles.optionCorrect}`;
          } else if (isSelected && !isCorrect) {
            className += ` ${styles.optionWrong}`;
          } else if (isCorrect) {
            className += ` ${styles.optionCorrectHint}`;
          }

          return (
            <div key={idx} className={className}>
              {isMultiple ? (
                <Checkbox checked={isSelected} disabled />
              ) : (
                <Radio checked={isSelected} disabled />
              )}
              <span style={{ flex: 1 }}>
                <strong>{optionLabel}.</strong> {option}
              </span>
              {isSelected &&
                (isCorrect ? (
                  <CheckCircleOutlined style={{ color: '#22c55e' }} />
                ) : (
                  <CloseCircleOutlined style={{ color: '#ef4444' }} />
                ))}
            </div>
          );
        })}
      </div>
    );
  };

  // 关闭弹窗
  const handleCloseModal = () => {
    setDetailModalVisible(false);
    setCurrentAssignment(null);
    setAssignmentDetail(null);
    setUserAnswers({});
    setShowResult(false);
  };

  // 判断是否可以提交
  const canSubmit =
    assignmentDetail &&
    assignmentDetail.status === 'published' &&
    assignmentDetail.submitStatus !== 'completed';

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
        <div className={styles.assignmentStats}>
          <span>
            进行中: {assignments.filter((a) => a.status === 'published').length}
          </span>
          <span>已结束: {assignments.filter((a) => a.status === 'closed').length}</span>
        </div>
      </motion.div>

      {/* 作业卡片列表 */}
      {loading ? (
        <div className={styles.loadingContainer}>加载中...</div>
      ) : assignments.length === 0 ? (
        <motion.div
          className={styles.emptyContainer}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <Empty description="暂无作业" />
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
                  <span
                    className={`${styles.assignmentStatus} ${getStatusClass(
                      assignment.status,
                      assignment.submitStatus
                    )}`}
                  >
                    {getStatusText(assignment.status, assignment.submitStatus)}
                  </span>
                </div>
                <div className={styles.assignmentMeta}>
                  <div className={styles.assignmentMetaItem}>
                    <CalendarOutlined className={styles.assignmentMetaIcon} />
                    <span>开始: {formatDate(assignment.startDate)}</span>
                  </div>
                  <div className={styles.assignmentMetaItem}>
                    <ClockCircleOutlined className={styles.assignmentMetaIcon} />
                    <span>截止: {formatDate(assignment.dueDate)}</span>
                  </div>
                </div>
                <div className={styles.assignmentFooter}>
                  <span>{assignment.questionCount} 道题</span>
                  <span>满分 {assignment.totalScore} 分</span>
                  {assignment.submitStatus === 'completed' && (
                    <span className={styles.userScore}>
                      得分: <strong>{assignment.userScore}</strong>
                    </span>
                  )}
                </div>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}

      {/* 作业详情/答题/结果弹窗 */}
      <Modal
        title={null}
        open={detailModalVisible}
        onCancel={handleCloseModal}
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
            <div className={styles.detailHeader}>
              <div>
                <h2 className={styles.detailTitle}>{assignmentDetail.title}</h2>
                <div className={styles.detailMeta}>
                  <span>共 {assignmentDetail.questions.length} 题</span>
                  <span>满分 {assignmentDetail.totalScore} 分</span>
                  {showResult && assignmentDetail.userScore !== undefined && (
                    <span className={styles.detailScore}>
                      得分: <strong>{assignmentDetail.userScore}</strong>
                    </span>
                  )}
                </div>
              </div>
              {showResult && (
                <div className={styles.scoreDisplay}>
                  <div className={styles.scoreValue}>
                    {assignmentDetail.userScore}
                    <span className={styles.scoreSuffix}>/{assignmentDetail.totalScore}</span>
                  </div>
                  <div className={styles.scoreLabel}>
                    提交时间: {dayjs(assignmentDetail.submitTime).format('YYYY-MM-DD HH:mm')}
                  </div>
                </div>
              )}
            </div>

            {/* 题目列表 */}
            <div className={styles.questionList}>
              {assignmentDetail.questions.map((question, idx) => (
                <motion.div
                  key={question.id}
                  className={`${styles.questionCard} ${
                    showResult
                      ? question.isCorrect
                        ? styles.questionCorrect
                        : styles.questionWrong
                      : ''
                  }`}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: idx * 0.05 }}
                >
                  <div className={styles.questionHeader}>
                    <span className={styles.questionNumber}>{idx + 1}</span>
                    <QuestionTypeTag type={question.type} />
                    {showResult && (
                      <span
                        className={`${styles.questionResult} ${
                          question.isCorrect ? styles.correct : styles.wrong
                        }`}
                      >
                        {question.isCorrect ? (
                          <>
                            <CheckCircleOutlined /> 正确
                          </>
                        ) : (
                          <>
                            <CloseCircleOutlined /> 错误
                          </>
                        )}
                      </span>
                    )}
                    <span className={styles.questionScore}>
                      {showResult && question.userScore !== undefined
                        ? `${question.userScore}/${question.score}`
                        : question.score}{' '}
                      分
                    </span>
                  </div>
                  <div className={styles.questionTitle}>{question.title}</div>

                  {/* 答题模式或结果模式 */}
                  {showResult ? renderResultOptions(question) : renderAnswerOptions(question)}

                  {/* 答案解析（仅结果模式显示） */}
                  {showResult && (
                    <div className={styles.answerSection}>
                      <div className={styles.answerRow}>
                        <span className={styles.answerLabel}>正确答案：</span>
                        <span className={styles.answerValue}>
                          {Array.isArray(question.answer)
                            ? question.answer.join('、')
                            : question.answer}
                        </span>
                      </div>
                      {question.analysis && (
                        <div className={styles.analysisText}>
                          <strong>解析：</strong>
                          {question.analysis}
                        </div>
                      )}
                    </div>
                  )}
                </motion.div>
              ))}
            </div>

            {/* 底部操作按钮 */}
            {canSubmit && !showResult && (
              <div className={styles.submitFooter}>
                <Button onClick={handleCloseModal}>稍后再答</Button>
                <Button
                  type="primary"
                  icon={<SendOutlined />}
                  loading={submitLoading}
                  onClick={handleSubmit}
                  style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
                >
                  提交作业
                </Button>
              </div>
            )}

            {showResult && (
              <div className={styles.submitFooter}>
                <Button onClick={handleCloseModal}>关闭</Button>
              </div>
            )}
          </motion.div>
        ) : null}
      </Modal>
    </div>
  );
};

export default StudentAssignmentPanel;
