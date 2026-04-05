'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import {
  TrophyOutlined,
  BookOutlined,
  ClockCircleOutlined,
  BulbOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  MinusOutlined,
} from '@ant-design/icons';
import {
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Area,
  AreaChart,
  BarChart,
  Bar,
} from 'recharts';
import { getStudentAnalytics } from '@/api/studentWorkspace';
import type { StudentAnalyticsData } from '@/types/studentWorkspace/StudentWorkspaceType';
import styles from '@/styles/studentWorkspace/index.module.css';

interface StudentAnalyticsPanelProps {
  courseId: number;
  studentId: string;
}

const COLORS = ['#2dd4bf', '#0ea5e9', '#10b981', '#94a3b8', '#cbd5e1', '#e2e8f0'];

const StudentAnalyticsPanel: React.FC<StudentAnalyticsPanelProps> = ({ courseId, studentId }) => {
  const [data, setData] = useState<StudentAnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const result = await getStudentAnalytics(courseId, studentId);
        setData(result);
      } catch (error) {
        console.error('获取学习分析数据失败:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [courseId, studentId]);

  if (loading || !data) {
    return <div className={styles.loadingContainer}>加载中...</div>;
  }

  const getTrendIcon = (current: number, previous: number) => {
    if (current > previous) return <ArrowUpOutlined style={{ color: '#16a34a' }} />;
    if (current < previous) return <ArrowDownOutlined style={{ color: '#dc2626' }} />;
    return <MinusOutlined style={{ color: '#86868b' }} />;
  };

  return (
    <div className={styles.analyticsContainer}>
      {/* 顶部统计卡片 */}
      <motion.div
        className={styles.statsHeader}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <div className={styles.statCard}>
          <div className={styles.statIcon}>
            <TrophyOutlined />
          </div>
          <div className={styles.statContent}>
            <div className={styles.statValue}>{data.averageScore}</div>
            <div className={styles.statLabel}>平均分数</div>
          </div>
        </div>
        <div className={styles.statCard}>
          <div className={styles.statIcon}>
            <BookOutlined />
          </div>
          <div className={styles.statContent}>
            <div className={styles.statValue}>{data.totalQuestions}</div>
            <div className={styles.statLabel}>总提问数</div>
          </div>
        </div>
        <div className={styles.statCard}>
          <div className={styles.statIcon}>
            <TrophyOutlined />
          </div>
          <div className={styles.statContent}>
            <div className={styles.statValue}>
              {data.rankInClass}
              <span className={styles.statSuffix}>/{data.totalStudents}</span>
            </div>
            <div className={styles.statLabel}>班级排名</div>
          </div>
        </div>
        <div className={styles.statCard}>
          <div className={styles.statIcon}>
            <ClockCircleOutlined />
          </div>
          <div className={styles.statContent}>
            <div className={styles.statValue}>{data.lastActive}</div>
            <div className={styles.statLabel}>最后活跃</div>
          </div>
        </div>
      </motion.div>

      {/* 图表区域 */}
      <div className={styles.chartsRow}>
        {/* 学习趋势 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.1 }}
        >
          <div className={styles.chartTitle}>学情分析趋势</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data.learningTrend}>
                <defs>
                  <linearGradient id="learningGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor="#1a1a1a" stopOpacity={0.1} />
                    <stop offset="100%" stopColor="#1a1a1a" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#f2f2f7" />
                <XAxis dataKey="date" stroke="#86868b" fontSize={10} tickLine={false} />
                <YAxis stroke="#86868b" fontSize={10} tickLine={false} axisLine={false} domain={[0, 100]} />
                <Tooltip
                  contentStyle={{
                    background: '#fff',
                    border: '1px solid #eef0f2',
                    borderRadius: 8,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
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
        </motion.div>

        {/* 问题复杂度分布 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.2 }}
        >
          <div className={styles.chartTitle}>问题复杂度分布</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data.complexityDistribution}
                  cx="50%"
                  cy="50%"
                  innerRadius={40}
                  outerRadius={60}
                  paddingAngle={2}
                  dataKey="count"
                  nameKey="level"
                >
                  {data.complexityDistribution.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{
                    background: '#fff',
                    border: '1px solid #eef0f2',
                    borderRadius: 8,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
                  }}
                />
              </PieChart>
            </ResponsiveContainer>
            <div className={styles.legendList}>
              {data.complexityDistribution.map((item, index) => (
                <div key={item.level} className={styles.legendItem}>
                  <span
                    className={styles.legendDot}
                    style={{ backgroundColor: COLORS[index % COLORS.length] }}
                  />
                  <span className={styles.legendLabel}>{item.level}</span>
                  <span className={styles.legendValue}>{item.percentage}%</span>
                </div>
              ))}
            </div>
          </div>
        </motion.div>
      </div>

      <div className={styles.chartsRow}>
        {/* 学科分布 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.3 }}
        >
          <div className={styles.chartTitle}>学科分布</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.subjectDistribution} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="#f2f2f7" />
                <XAxis type="number" stroke="#86868b" fontSize={11} tickLine={false} />
                <YAxis
                  type="category"
                  dataKey="subject"
                  stroke="#86868b"
                  fontSize={11}
                  width={50}
                  tickLine={false}
                  axisLine={false}
                />
                <Tooltip
                  contentStyle={{
                    background: '#fff',
                    border: '1px solid #eef0f2',
                    borderRadius: 8,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
                  }}
                />
                <Bar dataKey="count" fill="#0ea5e9" radius={[0, 4, 4, 0]} name="提问数" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 最近作业成绩 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.4 }}
        >
          <div className={styles.chartTitle}>最近作业成绩</div>
          <div className={styles.recentAssignments}>
            {data.recentAssignments.map((assignment, index) => (
              <motion.div
                key={assignment.id}
                className={styles.assignmentItem}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.2, delay: 0.4 + index * 0.05 }}
              >
                <div className={styles.assignmentInfo}>
                  <div className={styles.assignmentTitle}>{assignment.title}</div>
                  <div className={styles.assignmentDate}>{assignment.submitTime}</div>
                </div>
                <div className={styles.assignmentScore}>
                  <span className={styles.scoreValue}>{assignment.score}</span>
                  <span className={styles.scoreSuffix}>/{assignment.totalScore}</span>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </div>

      {/* 学习建议 */}
      <motion.div
        className={styles.adviceCard}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3, delay: 0.5 }}
      >
        <div className={styles.adviceTitle}>
          <BulbOutlined />
          学习建议
        </div>
        <div className={styles.adviceList}>
          {data.learningAdvice.map((advice, index) => (
            <motion.div
              key={index}
              className={styles.adviceItem}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.2, delay: 0.5 + index * 0.05 }}
            >
              <span className={styles.adviceIcon}>
                {index === 0 ? <BookOutlined /> : <ClockCircleOutlined />}
              </span>
              <span>{advice}</span>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default StudentAnalyticsPanel;
