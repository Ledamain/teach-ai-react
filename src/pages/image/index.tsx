'use client';

import React, { useState, useRef, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
    ImageIcon,
    Send,
    Download,
    RefreshCw,
    AlertCircle,
    Info
} from 'lucide-react';
import styles from '@/styles/image/index.module.css';
import ImageApi from '@/api/image/index'
import {ImageParam} from "@/types/image/ImageType";
import {UserInfo} from "@/types/login/LoginType";
import {getUserId} from "@/utils/userUtil";

interface ImageResponse {
    requestId: string;
    usage: {
        image_count: number;
        width: number;
        height: number;
    };
    output: {
        choices: Array<{
            finish_reason: string;
            message: {
                role: string;
                content: Array<{
                    image: string;
                }>;
            };
        }>;
    };
}

interface GeneratedImage {
    url: string;
    width: number;
    height: number;
}

async function pollImageResult(taskId: string, timeout = 120000): Promise<string> {
    const interval = 3000;
    const deadline = Date.now() + timeout;

    while (Date.now() < deadline) {
        const res = await ImageApi.getImageResult(taskId, getUserId() as number);
        const task: { status: string; result: string; errorMsg: string } = JSON.parse(res);

        if (task.status === 'DONE') return task.result;
        if (task.status === 'ERROR') throw new Error(task.errorMsg || '生成失败');

        await new Promise(resolve => setTimeout(resolve, interval));
    }
    throw new Error('生成超时，请重试');
}

export default function ImageGeneratePage() {
    const [prompt, setPrompt] = useState('');
    const [status, setStatus] = useState<'idle' | 'loading' | 'done' | 'error'>('idle');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [generatedImage, setGeneratedImage] = useState<GeneratedImage | null>(null);
    const [isInputMoved, setIsInputMoved] = useState(false);

    const textareaRef = useRef<HTMLTextAreaElement>(null);
    const abortCtrlRef = useRef<AbortController | null>(null);

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

    // 解析 API 返回的字符串
    const parseImageResponse = (responseText: string): GeneratedImage | null => {
        try {
            const data: ImageResponse = JSON.parse(responseText);

            if (data.output?.choices?.[0]?.message?.content?.[0]?.image) {
                return {
                    url: data.output.choices[0].message.content[0].image,
                    width: data.usage?.width || 1024,
                    height: data.usage?.height || 1024,
                };
            }
            return null;
        } catch (error) {
            console.error('解析图片响应失败:', error);
            return null;
        }
    };

    // 处理文本框高度自适应
    const handleTextareaChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setPrompt(e.target.value);
        const textarea = e.target;
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
    };

    // 发送生成请求
    const handleGenerate = useCallback(async () => {
        if (!prompt.trim()) return;

        setIsInputMoved(true);
        setStatus('loading');
        setErrorMessage('');
        setGeneratedImage(null);

        abortCtrlRef.current?.abort();
        const abort = new AbortController();
        abortCtrlRef.current = abort;

        try {
            const submit: ImageParam = {
                userId: getUserId(),
                prompt: prompt.trim(),
            };

            // 提交任务，拿到 taskId
            const taskId = await ImageApi.createImage(submit);
            if (!taskId) throw new Error('任务提交失败');

            // 轮询直到完成
            const result = await pollImageResult(taskId);
            const parsedImage = parseImageResponse(result);

            if (parsedImage) {
                setGeneratedImage(parsedImage);
                setStatus('done');
            } else {
                throw new Error('解析图片数据失败');
            }
        } catch (error: unknown) {
            if (error instanceof Error && error.name === 'AbortError') return;
            setStatus('error');
            setErrorMessage(error instanceof Error ? error.message : '生成失败，请重试');
        }
    }, [prompt]);

    // 处理键盘事件
    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleGenerate();
        }
    };

    // 下载图片
    const handleDownload = async () => {
        if (!generatedImage) return;

        try {
            const response = await fetch(generatedImage.url);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `ai-generated-${Date.now()}.png`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('下载失败:', error);
            // 如果 blob 下载失败，尝试直接打开链接
            window.open(generatedImage.url, '_blank');
        }
    };

    // 重新生成
    const handleRegenerate = () => {
        handleGenerate();
    };

    const containerVariants = {
        initial: { opacity: 0 },
        animate: { opacity: 1, transition: { duration: 0.5, ease: [0.19, 1, 0.22, 1] } },
    };

    const imageVariants = {
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
                {/* ========== 1. 顶部输入框 ========== */}
                <motion.div
                    className={`${styles.inputSection} ${isInputMoved ? styles.inputSectionTop : styles.inputSectionCenter}`}
                >
                    <div className={styles.inputWrapper}>
                        {!isInputMoved && (
                            <motion.div
                                className={styles.welcomeTitle}
                                initial={{ opacity: 0, y: -20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ duration: 0.5, ease: [0.19, 1, 0.22, 1] }}
                            >
                                <ImageIcon className={styles.welcomeIcon} />
                                <span>AI 图像智能生成</span>
                            </motion.div>
                        )}
                        <div className={styles.senderWrapper}>
                            <div className={styles.inputArea}>
                <textarea
                    ref={textareaRef}
                    value={prompt}
                    onChange={handleTextareaChange}
                    onKeyDown={handleKeyDown}
                    placeholder="描述你想生成的图像，例如：制作面向学生的深度学习课程架构图，简洁易懂"
                    rows={1}
                    disabled={status === 'loading'}
                />
                                <button
                                    className={styles.sendButton}
                                    onClick={handleGenerate}
                                    disabled={!prompt.trim() || status === 'loading'}
                                >
                                    {status === 'loading' ? (
                                        <RefreshCw className="animate-spin" />
                                    ) : (
                                        <Send />
                                    )}
                                </button>
                            </div>
                        </div>
                        {!isInputMoved && (
                            <motion.div
                                className={styles.hintText}
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                transition={{ delay: 0.3, duration: 0.5 }}
                            >
                                输入描述后按 Enter 或点击发送按钮开始生成图像
                            </motion.div>
                        )}
                    </div>
                </motion.div>

                {/* ========== 2. 中间内容区域 ========== */}
                <div className={styles.middleScrollSection}>
                    <AnimatePresence>
                        {/* 加载状态 */}
                        {status === 'loading' && (
                            <motion.div
                                className={styles.loadingSection}
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                exit={{ opacity: 0 }}
                            >
                                <div className={styles.loadingDots}>
                                    <span />
                                    <span />
                                    <span />
                                </div>
                                <p className={styles.loadingText}>正在生成图像...</p>
                            </motion.div>
                        )}
                    </AnimatePresence>

                    <AnimatePresence>
                        {/* 错误状态 */}
                        {status === 'error' && (
                            <motion.div
                                className={styles.errorSection}
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                exit={{ opacity: 0 }}
                            >
                                <AlertCircle className={styles.errorIcon} />
                                <p className={styles.errorText}>{errorMessage || '生成失败，请重试'}</p>
                                <button className={styles.retryButton} onClick={handleRegenerate}>
                                    <RefreshCw size={16} />
                                    重新生成
                                </button>
                            </motion.div>
                        )}
                    </AnimatePresence>

                    <AnimatePresence>
                        {/* 图片展示 */}
                        {status === 'done' && generatedImage && (
                            <motion.div
                                className={styles.imageSection}
                                variants={imageVariants}
                                initial="hidden"
                                animate="visible"
                                exit={{ opacity: 0, y: -20 }}
                            >
                                <div className={styles.imageHeader}>
                  <span className={styles.imageTitle}>
                    <ImageIcon style={{ marginRight: 8, width: 20, height: 20 }} />
                    生成结果
                  </span>
                                    <p style={{ fontSize: 14, color: '#52c41a' }}>图像生成完成</p>
                                </div>

                                <div className={styles.imageContent}>
                                    <div className={styles.imagePreviewWrapper}>
                                        {/* eslint-disable-next-line @next/next/no-img-element */}
                                        <img
                                            src={generatedImage.url}
                                            alt="AI 生成的图像"
                                            className={styles.imagePreview}
                                            style={{
                                                aspectRatio: `${generatedImage.width} / ${generatedImage.height}`,
                                            }}
                                        />

                                        <div className={styles.imageInfo}>
                      <span>
                        <Info size={16} />
                        尺寸: {generatedImage.width} × {generatedImage.height}
                      </span>
                                        </div>

                                        <div className={styles.actionButtons}>
                                            <button className={styles.downloadButton} onClick={handleDownload}>
                                                <Download size={18} />
                                                下载图片
                                            </button>
                                            <button className={styles.regenerateButton} onClick={handleRegenerate}>
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
