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
    GiftIcon,
} from 'lucide-react';
import styles from '@/styles/video/index.module.css';
import VideoApi from '@/api/video/index'
import { VideoChatParam } from "@/types/video/VideoType";
import { UserInfo } from "@/types/login/LoginType";
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
        size: string;
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
    const interval = 5000;
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
type Duration = '5' | '10' | '15' | '30' | '60';

export default function VideoGeneratePage() {
    const [prompt, setPrompt] = useState('');
    const [aspectRatio, setAspectRatio] = useState<AspectRatio>('1280*720');
    const [duration, setDuration] = useState<Duration>('5');
    const [status, setStatus] = useState<'idle' | 'loading' | 'done' | 'error'>('idle');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [generatedVideo, setGeneratedVideo] = useState<GeneratedVideo | null>(null);

    // GIF 导出状态
    const [isExportingGif, setIsExportingGif] = useState(false);

    const getUserId = (): number => {
        if (typeof window === 'undefined') return 0;
        const userInfoStr = window.localStorage.getItem('userInfo');
        if (!userInfoStr) return 0;
        try {
            const userInfo: UserInfo = JSON.parse(userInfoStr);
            return Number(userInfo.id) || 0;
        } catch {
            return 0;
        }
    };

    const parseVideoResponse = (responseText: string): GeneratedVideo | null => {
        try {
            const data: VideoResponse = JSON.parse(responseText);
            if (data.status_code !== 200 || data.output?.task_status !== 'SUCCEEDED') {
                throw new Error(data.output?.message || data.message || '视频生成失败');
            }
            const videoUrl = data.output?.video_url;
            if (!videoUrl) return null;
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
            const taskId = await VideoApi.createVideo(submit);
            if (!taskId) throw new Error('任务提交失败');
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

    // 下载原视频
    const handleDownload = async () => {
        if (!generatedVideo) return;
        try {
            const response = await fetch(generatedVideo.url);
            const blob = await response.blob();
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `ai-video-${Date.now()}.mp4`;
            a.click();
            URL.revokeObjectURL(url);
        } catch (error) {
            console.error('下载失败:', error);
            window.open(generatedVideo.url, '_blank');
        }
    };

    // ======================
    // 导出 GIF（时长跟随选择）
    // ======================
    const handleExportGif = async () => {
        if (!generatedVideo || isExportingGif) return;
        setIsExportingGif(true);

        try {
            if (!(window as any).GIF) {
                await loadScript('https://cdn.jsdelivr.net/npm/gif.js@0.2.0/dist/gif.min.js');
            }

            const { width, height, url } = generatedVideo;
            const targetDuration = parseInt(duration);

            const video = document.createElement('video');
            video.muted = true;
            video.crossOrigin = 'anonymous';
            video.src = url;

            await new Promise<void>(resolve => {
                video.onloadedmetadata = () => resolve();
            });

            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            const ctx = canvas.getContext('2d')!;

            const fps = 8;
            const totalFrames = fps * targetDuration;

            const gif = new (window as any).GIF({
                workers: 2,
                quality: 10,
                width,
                height,
                repeat: 0,
                workerScript: '/gif.worker.js',
            });

            for (let i = 0; i < totalFrames; i++) {
                const targetTime = i / fps;

                await new Promise<void>(resolve => {
                    video.onseeked = () => resolve();
                    video.currentTime = targetTime;
                });

                ctx.drawImage(video, 0, 0, width, height);
                gif.addFrame(canvas, { delay: 1000 / fps, copy: true }); // ✅ copy: true 很重要
            }

            gif.on('finished', (blob: Blob) => {
                const gifUrl = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = gifUrl;
                a.download = `ai-video-${Date.now()}.gif`;
                a.click();
                URL.revokeObjectURL(gifUrl);
                setIsExportingGif(false);
            });

            gif.render();

        } catch (err) {
            console.error('GIF 导出失败', err);
            alert('GIF 导出失败，请重试');
            setIsExportingGif(false);
        }
    };

    // 动态加载脚本
    const loadScript = (src: string): Promise<void> => {
        return new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = src;
            script.onload = () => resolve();
            script.onerror = reject;
            document.body.appendChild(script);
        });
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

                            <div className={styles.formGroup}>
                                <label className={styles.formLabel}>
                                    <Maximize2 size={16} />
                                    画面比例
                                </label>
                                <div className={styles.optionGroup}>
                                    {(['1280*720', '720*1280', '1024*1024'] as AspectRatio[]).map((ratio) => (
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

                            <div className={styles.formGroup}>
                                <label className={styles.formLabel}>
                                    <Clock size={16} />
                                    视频时长
                                </label>
                                <div className={styles.optionGroup}>
                                    {(['5', '10', '15', '30', '60'] as Duration[]).map((d) => (
                                        <button
                                            key={d}
                                            className={`${styles.optionButton} ${duration === d ? styles.optionButtonActive : ''}`}
                                            onClick={() => setDuration(d)}
                                            disabled={status === 'loading'}
                                        >
                                            {d} 秒
                                        </button>
                                    ))}
                                </div>
                            </div>

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

                <div className={styles.rightPanel}>
                    <AnimatePresence mode="wait">
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

                                            {/* 导出 GIF 按钮 */}
                                            <button
                                                className={styles.downloadButton}
                                                onClick={handleExportGif}
                                                disabled={isExportingGif}
                                            >
                                                {isExportingGif ? (
                                                    <RefreshCw size={18} className="animate-spin" />
                                                ) : (
                                                    <GiftIcon size={18} />
                                                )}
                                                {isExportingGif ? '导出中...' : '导出 GIF'}
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