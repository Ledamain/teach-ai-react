import { useState, useEffect } from 'react';
import styles from '@/styles/HotQuestions/index.module.css';
import HotQueryApi from '@/api/hotquery'


interface HotQuestionsProps {
    onQuestionClick: (question: string) => void;
}

const HotQuestions = ({ onQuestionClick }: HotQuestionsProps) => {
    const [questions, setQuestions] = useState<string[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    // 获取数据 API
    const fetchQuestions = async () => {
        try {
            setLoading(true);
            const result = await HotQueryApi.getClassesListByRepoCategoryId()
            const questionList = JSON.parse(result);
            setQuestions(questionList);
        } catch (error) {
            console.error('获取失败', error);
            setQuestions([
                '资讯：高盛看多全球科技股称低估值带来买入机会',
                '想了解如何用 AI agent 优化日常工作流程，请提供思路',
                '火车为什么没有 18 号车厢？',
                '资讯：宁德时代拟 41 亿入股中恒电气控股东',
                '帮我总结观察者模式与发布订阅模式的区别',
                '高等数学从哪里入手？',
            ]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchQuestions();
    }, []);

    // 加载中
    if (loading) {
        return (
            <div className={styles.container}>
                {[...Array(9)].map((_, i) => (
                    <div key={i} className={`${styles.questionItem} ${styles.skeleton}`} />
                ))}
            </div>
        );
    }

    // 最多展示6个气泡
    const displayQuestions = questions.slice(0, 9);

    return (
        <div className={styles.container}>
            {displayQuestions.map((item, idx) => (
                <button
                    key={idx}
                    className={styles.questionItem}
                    onClick={() => onQuestionClick(item)} // 👉 调用父组件传入的方法
                >
                    {item}
                </button>
            ))}
        </div>
    );
};

export default HotQuestions;
