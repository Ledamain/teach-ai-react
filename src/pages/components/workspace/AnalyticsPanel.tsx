'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  QuestionCircleOutlined, 
  TeamOutlined, 
  AppstoreOutlined, 
  RiseOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  MinusOutlined
} from '@ant-design/icons';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
  Area,
  AreaChart,
} from 'recharts';
import { getCourseAnalytics } from '@/api/workspace/courseDetail';
import type { AnalyticsData } from '@/types/workspace/CourseDetailType';
import styles from '@/styles/workspace/courseDetail.module.css';

interface AnalyticsPanelProps {
  courseId: string;
}

// 教育平台配色 - OpenAI简约风格 + 蓝青色调
const COLORS = ['#2dd4bf', '#0ea5e9', '#10b981', '#94a3b8', '#cbd5e1', '#e2e8f0'];
const CHART_BLUE = '#0ea5e9';
const CHART_CYAN = '#2dd4bf';
const CHART_GREEN = '#10b981';
const CHART_GRAY = '#94a3b8';

const AnalyticsPanel: React.FC<AnalyticsPanelProps> = ({ courseId }) => {
  const [data, setData] = useState<AnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const result = await getCourseAnalytics(courseId);
        setData(result);
      } catch (error) {
        console.error('获取分析数据失败:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [courseId]);

  if (loading || !data) {
    return <div className={styles.analyticsContainer}>加载中...</div>;
  }

  // 统计卡片数据 - 包含趋势
  const statCards = [
    { 
      icon: <QuestionCircleOutlined />, 
      value: data.totalQuestions, 
      label: '总提问数',
      trend: 'up' as const,
      trendValue: '+12.5%',
      miniData: data.questionTrend.slice(-7)
    },
    { 
      icon: <TeamOutlined />, 
      value: data.participantStudents, 
      label: '参与学生',
      trend: 'up' as const,
      trendValue: '+8.3%',
      miniData: data.activeStudents.slice(-7)
    },
    { 
      icon: <AppstoreOutlined />, 
      value: data.subjectCategories, 
      label: '学科分类',
      trend: 'stable' as const,
      trendValue: '0%',
      miniData: null
    },
    { 
      icon: <RiseOutlined />, 
      value: data.todayQuestions, 
      label: '今日提问',
      trend: 'down' as const,
      trendValue: '-5.2%',
      miniData: data.questionTrend.slice(-7)
    },
  ];

  const getTrendIcon = (trend: 'up' | 'down' | 'stable') => {
    switch (trend) {
      case 'up':
        return <ArrowUpOutlined />;
      case 'down':
        return <ArrowDownOutlined />;
      default:
        return <MinusOutlined />;
    }
  };

  return (
    <div className={styles.analyticsContainer}>
      {/* 统计卡片 - 一行四个 */}
      <motion.div 
        className={styles.statsCards}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        {statCards.map((card, index) => (
          <motion.div
            key={card.label}
            className={styles.statsCard}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: index * 0.1 }}
          >
            <div className={styles.statsCardHeader}>
              <div className={`${styles.statsCardIcon} ${styles.gray}`}>
                {card.icon}
              </div>
              <div className={`${styles.statsCardTrend} ${styles[card.trend]}`}>
                {getTrendIcon(card.trend)}
                <span>{card.trendValue}</span>
              </div>
            </div>
            <div className={styles.statsCardValue}>{card.value.toLocaleString()}</div>
            <div className={styles.statsCardLabel}>{card.label}</div>
            {card.miniData && (
              <div className={styles.statsCardMini}>
                <div className={styles.miniChart}>
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={card.miniData}>
                      <defs>
                        <linearGradient id={`gradient-${index}`} x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={CHART_BLUE} stopOpacity={0.3} />
                          <stop offset="100%" stopColor={CHART_BLUE} stopOpacity={0} />
                        </linearGradient>
                      </defs>
                      <Area 
                        type="monotone" 
                        dataKey="count" 
                        stroke={CHART_BLUE}
                        strokeWidth={1.5}
                        fill={`url(#gradient-${index})`}
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
              </div>
            )}
          </motion.div>
        ))}
      </motion.div>

      {/* 图表区域 - 两行三列 */}
      <div className={styles.chartsGrid}>
        {/* 第一行 */}
        {/* 问题类型分布 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.4 }}
        >
          <div className={styles.chartTitle}>问题类型分布</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data.questionTypeDistribution}
                  cx="50%"
                  cy="50%"
                  innerRadius={50}
                  outerRadius={70}
                  paddingAngle={2}
                  dataKey="count"
                  nameKey="type"
                >
                  {data.questionTypeDistribution.map((_, index) => (
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
                <Legend 
                  verticalAlign="bottom" 
                  height={36}
                  formatter={(value) => <span style={{ color: '#5e5e66', fontSize: 12 }}>{value}</span>}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 提问趋势 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.5 }}
        >
          <div className={styles.chartTitle}>提问趋势</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data.questionTrend}>
                <defs>
                  <linearGradient id="questionGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stopColor={CHART_CYAN} stopOpacity={0.15} />
                    <stop offset="100%" stopColor={CHART_CYAN} stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#f2f2f7" />
                <XAxis dataKey="date" stroke="#86868b" fontSize={11} tickLine={false} />
                <YAxis stroke="#86868b" fontSize={11} tickLine={false} axisLine={false} />
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
                    dataKey="count"
                    stroke={CHART_CYAN}
                    strokeWidth={2}
                    fill="url(#questionGradient)"
                    name="提问数"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 活跃学生数 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.6 }}
        >
          <div className={styles.chartTitle}>活跃学生数</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.activeStudents}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f2f2f7" />
                <XAxis dataKey="date" stroke="#86868b" fontSize={11} tickLine={false} />
                <YAxis stroke="#86868b" fontSize={11} tickLine={false} axisLine={false} />
                <Tooltip 
                  contentStyle={{ 
                    background: '#fff', 
                    border: '1px solid #eef0f2',
                    borderRadius: 8,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)'
                  }}
                />
                <Bar
                    dataKey="count"
                    fill={CHART_GREEN}
                    radius={[4, 4, 0, 0]}
                    name="活跃学生"
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 第二行 */}
        {/* 问题复杂度分布 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.7 }}
        >
          <div className={styles.chartTitle}>问题复杂度分布</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.complexityDistribution} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="#f2f2f7" />
                <XAxis type="number" stroke="#86868b" fontSize={11} tickLine={false} />
                <YAxis 
                  type="category" 
                  dataKey="level" 
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
                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)'
                  }}
                />
                <Bar
                    dataKey="count"
                    fill={CHART_CYAN}
                    radius={[0, 4, 4, 0]}
                    name="问题数"
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 学科分布 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.8 }}
        >
          <div className={styles.chartTitle}>学科分布</div>
          <div className={styles.chartContent}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data.subjectDistribution}
                  cx="50%"
                  cy="50%"
                  outerRadius={70}
                  dataKey="count"
                  nameKey="subject"
                >
                  {data.subjectDistribution.map((_, index) => (
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
                <Legend 
                  verticalAlign="bottom" 
                  height={36}
                  formatter={(value) => <span style={{ color: '#5e5e66', fontSize: 12 }}>{value}</span>}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </motion.div>

        {/* 热门关键词趋势 */}
        <motion.div
          className={styles.chartCard}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.9 }}
        >
          <div className={styles.chartTitle}>热门关键词趋势</div>
          <div className={styles.keywordsList}>
            {data.hotKeywords.map((keyword, index) => (
              <motion.div
                key={keyword.keyword}
                className={styles.keywordItem}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.2, delay: 0.9 + index * 0.05 }}
              >
                <span className={styles.keywordName}>{keyword.keyword}</span>
                <div className={styles.keywordMeta}>
                  <span className={styles.keywordCount}>{keyword.count}次</span>
                  <span className={`${styles.keywordTrend} ${styles[keyword.trend]}`}>
                    {getTrendIcon(keyword.trend)}
                  </span>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default AnalyticsPanel;
