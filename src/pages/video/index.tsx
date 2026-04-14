'use client';

import React, { useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
    Video,
    Send,
    Download,
    RefreshCw,
    AlertCircle,
    Clock,
    Film,
    Maximize2,
    Type,
    Sparkles,
    CheckCircle2,
} from 'lucide-react';
import styles from '@/styles/video/index.module.css';
import VideoApi from '@/api/video/index'
import {VideoChatParam} from "@/types/video/VideoType";
import {UserInfo} from "@/types/login/LoginType";
import {getUserId} from "@/utils/userUtil";

interface VideoResponse {
    request_id: string;
    output: {
        task_id: string;
        task_status: string;
        video_url: string;
        orig_prompt: string;
        submit_time: string;
        scheduled_time: string;
        end_time: string;
    };
    usage: {
        video_count: number;
        duration: number;
        size: string; // "1280*720"
        input_video_duration: number;
        output_video_duration: number;
        SR: string;
    };
    status_code: number;
    code: string;
    message: string;
}

interface GeneratedVideo {
    url: string;
    width: number;
    height: number;
    duration: number;
}

async function pollVideoResult(taskId: string, timeout = 480000): Promise<string> {
    const interval = 5000; // 视频生成慢，5秒轮询一次
    const deadline = Date.now() + timeout;

    while (Date.now() < deadline) {
        const res = await VideoApi.getVideoResult(taskId, getUserId() as number);
        const task: { status: string; result: string; errorMsg: string } = JSON.parse(res);

        if (task.status === 'DONE') return task.result;
        if (task.status === 'ERROR') throw new Error(task.errorMsg || '视频生成失败');

        await new Promise(resolve => setTimeout(resolve, interval));
    }
    throw new Error('生成超时，请重试');
}


type AspectRatio = '1280*720' | '720*1280' | '1024*1024';
type Duration = '5' | '10' | '15';

export default function VideoGeneratePage() {
    const [prompt, setPrompt] = useState('');
    const [aspectRatio, setAspectRatio] = useState<AspectRatio>('1280*720');
    const [duration, setDuration] = useState<Duration>('5');
    const [status, setStatus] = useState<'idle' | 'loading' | 'done' | 'error'>('idle');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [generatedVideo, setGeneratedVideo] = useState<GeneratedVideo | null>(null);

    // 解析 API 返回的字符串
    const parseVideoResponse = (responseText: string): GeneratedVideo | null => {
        try {
            const data: VideoResponse = JSON.parse(responseText);

            // 检查是否生成成功
            if (data.status_code !== 200 || data.output?.task_status !== 'SUCCEEDED') {
                throw new Error(data.output?.message || data.message || '视频生成失败');            }

            // 拿到视频地址
            const videoUrl = data.output?.video_url;
            if (!videoUrl) return null;

            // 解析尺寸 1280*720 → 宽、高
            const size = data.usage?.size || '1280*720';
            const [width, height] = size.split('*').map(Number);

            return {
                url: videoUrl,
                width: width || 1280,
                height: height || 720,
                duration: data.usage?.duration || 10,
            };
        } catch (error) {
            console.error('解析视频响应失败:', error);
            return null;
        }
    };

    const getUserId = (): number => {
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

    // 发送生成请求
    const handleGenerate = useCallback(async () => {
        if (!prompt.trim()) return;

        setStatus('loading');
        setErrorMessage('');
        setGeneratedVideo(null);

        try {
            const submit: VideoChatParam = {
                userId: getUserId(),
                prompt: prompt.trim(),
                aspectRatio,
                duration,
            };

            // 1. 提交任务，拿到 taskId
            const taskId = await VideoApi.createVideo(submit);
            if (!taskId) throw new Error('任务提交失败');

            // 2. 轮询直到完成，最多等 3 分钟
            const result = await pollVideoResult(taskId);
            const parsedVideo = parseVideoResponse(result);

            if (parsedVideo) {
                setGeneratedVideo(parsedVideo);
                setStatus('done');
            } else {
                throw new Error('解析视频数据失败');
            }
        } catch (error: unknown) {
            setStatus('error');
            setErrorMessage(error instanceof Error ? error.message : '生成失败，请重试');
        }
    }, [prompt, aspectRatio, duration]);

    // 下载视频
    const handleDownload = async () => {
        if (!generatedVideo) return;

        try {
            const response = await fetch(generatedVideo.url);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `ai-video-${Date.now()}.mp4`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('下载失败:', error);
            window.open(generatedVideo.url, '_blank');
        }
    };

    const containerVariants = {
        initial: { opacity: 0 },
        animate: { opacity: 1, transition: { duration: 0.5, ease: [0.19, 1, 0.22, 1] } },
    };

    const contentVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: { opacity: 1, y: 0, transition: { duration: 0.5, ease: [0.19, 1, 0.22, 1] } },
    };

    return (
        <div className={styles.container}>
            <motion.div
                className={styles.mainPage}
                variants={containerVariants}
                initial="initial"
                animate="animate"
            >
                {/* ========== 左侧面板 ========== */}
                <div className={styles.leftPanel}>
                    <div className={styles.leftHeader}>
                        <div className={styles.logoSection}>
                            <div className={styles.logoIcon}>
                                <Video size={24} />
                            </div>
                            <div className={styles.logoText}>
                                <h1>AI 视频生成</h1>
                                <p>文字描述，智能成片</p>
                            </div>
                        </div>
                    </div>

                    <div className={styles.leftContent}>
                        <div className={styles.formSection}>
                            {/* 描述输入 */}
                            <div className={styles.formGroup}>
                                <label className={styles.formLabel}>
                                    <Type size={16} />
                                    视频描述
                                </label>
                                <div className={styles.textareaWrapper}>
                  <textarea
                      value={prompt}
                      onChange={(e) => setPrompt(e.target.value)}
                      placeholder="描述你想生成的视频内容，例如：制作面向学生的深度学习课程架构讲解视频，简洁易懂，拆解课程基础、核心理论、实践应用模块，帮助学生理清课程脉络"
                      maxLength={500}
                      disabled={status === 'loading'}
                  />
                                    <span className={styles.charCount}>{prompt.length}/500</span>
                                </div>
                            </div>

                            {/* 画面比例 */}
                            <div className={styles.formGroup}>
                                <label className={styles.formLabel}>
                                    <Maximize2 size={16} />
                                    画面比例
                                </label>
                                <div className={styles.optionGroup}>
                                    {(['1280*720', '720*1280', '1024*1024'] as string[]).map((ratio) => (
                                        <button
                                            key={ratio}
                                            className={`${styles.optionButton} ${aspectRatio === ratio ? styles.optionButtonActive : ''}`}
                                            onClick={() => setAspectRatio(ratio)}
                                            disabled={status === 'loading'}
                                        >
                                            {ratio === '1280*720' && '横屏 1280*720'}
                                            {ratio === '720*1280' && '竖屏 720*1280'}
                                            {ratio === '1024*1024' && '方形 1024*1024'}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* 视频时长 */}
                            <div className={styles.formGroup}>
                                <label className={styles.formLabel}>
                                    <Clock size={16} />
                                    视频时长
                                </label>
                                <div className={styles.optionGroup}>
                                    {(['5', '10', '15'] as Duration[]).map((d) => (
                                        <button
                                            key={d}
                                            className={`${styles.optionButton} ${duration === d ? styles.optionButtonActive : ''}`}
                                            onClick={() => setDuration(d)}
                                            disabled={status === 'loading'}
                                        >
                                            {d === '5' && '5 秒'}
                                            {d === '10' && '10 秒'}
                                            {d === '15' && '15 秒'}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* 生成按钮 */}
                            <button
                                className={styles.generateButton}
                                onClick={handleGenerate}
                                disabled={!prompt.trim() || status === 'loading'}
                            >
                                {status === 'loading' ? (
                                    <>
                                        <RefreshCw className="animate-spin" size={20} />
                                        生成中...
                                    </>
                                ) : (
                                    <>
                                        <Sparkles size={20} />
                                        开始生成视频
                                    </>
                                )}
                            </button>
                        </div>
                    </div>
                </div>

                {/* ========== 右侧预览区域 ========== */}
                <div className={styles.rightPanel}>
                    <AnimatePresence mode="wait">
                        {/* 空状态 */}
                        {status === 'idle' && (
                            <motion.div
                                key="empty"
                                className={styles.emptyState}
                                variants={contentVariants}
                                initial="hidden"
                                animate="visible"
                                exit={{ opacity: 0 }}
                            >
                                <div className={styles.emptyIcon}>
                                    <Film size={40} />
                                </div>
                                <h2 className={styles.emptyTitle}>视频预览区</h2>
                                <p className={styles.emptyDesc}>
                                    在左侧输入视频描述并选择参数，点击生成按钮后，AI 生成的视频将在此处展示
                                </p>
                            </motion.div>
                        )}

                        {/* 加载状态 */}
                        {status === 'loading' && (
                            <motion.div
                                key="loading"
                                className={styles.loadingState}
                                variants={contentVariants}
                                initial="hidden"
                                animate="visible"
                                exit={{ opacity: 0 }}
                            >
                                <div className={styles.loadingSpinner} />
                                <p className={styles.loadingText}>AI 正在创作视频</p>
                                <p className={styles.loadingSubtext}>视频生成可能需要 30-60 秒，请耐心等待</p>
                            </motion.div>
                        )}

                        {/* 错误状态 */}
                        {status === 'error' && (
                            <motion.div
                                key="error"
                                className={styles.errorState}
                                variants={contentVariants}
                                initial="hidden"
                                animate="visible"
                                exit={{ opacity: 0 }}
                            >
                                <AlertCircle className={styles.errorIcon} />
                                <p className={styles.errorText}>{errorMessage || '生成失败，请重试'}</p>
                                <button className={styles.retryButton} onClick={handleGenerate}>
                                    <RefreshCw size={18} />
                                    重新生成
                                </button>
                            </motion.div>
                        )}

                        {/* 视频预览 */}
                        {status === 'done' && generatedVideo && (
                            <motion.div
                                key="preview"
                                className={styles.videoPreviewSection}
                                variants={contentVariants}
                                initial="hidden"
                                animate="visible"
                                exit={{ opacity: 0 }}
                            >
                                <div className={styles.videoCard}>
                                    <div className={styles.videoCardHeader}>
                    <span className={styles.videoCardTitle}>
                      <Video size={20} />
                      生成结果
                    </span>
                                        <span className={styles.videoCardStatus}>
                      <span className={styles.statusDot} />
                      <CheckCircle2 size={16} />
                      生成完成
                    </span>
                                    </div>

                                    <div className={styles.videoWrapper}>
                                        <video
                                            className={styles.videoPlayer}
                                            src={generatedVideo.url}
                                            controls
                                            autoPlay
                                            loop
                                            style={{
                                                aspectRatio: `${generatedVideo.width} / ${generatedVideo.height}`,
                                            }}
                                        />
                                    </div>

                                    <div className={styles.videoInfo}>
                                        <div className={styles.videoMeta}>
                                            <div className={styles.metaItem}>
                                                <Maximize2 size={16} />
                                                {generatedVideo.width} * {generatedVideo.height}
                                            </div>
                                            <div className={styles.metaItem}>
                                                <Clock size={16} />
                                                {generatedVideo.duration} 秒
                                            </div>
                                            <div className={styles.metaItem}>
                                                <Film size={16} />
                                                MP4 格式
                                            </div>
                                        </div>

                                        <div className={styles.actionBar}>
                                            <button className={styles.downloadButton} onClick={handleDownload}>
                                                <Download size={18} />
                                                下载视频
                                            </button>
                                            <button className={styles.regenerateButton} onClick={handleGenerate}>
                                                <RefreshCw size={18} />
                                                重新生成
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            </motion.div>
        </div>
    );
}
