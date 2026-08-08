'use client';

import React, {useState, useRef, useCallback, useEffect, useMemo} from 'react';
import {
    Button,
    Input,
    Tag,
    Typography,
    message,
    Modal, UploadProps, UploadFile, Flex, Upload, theme,
} from 'antd';
import {
    FileTextOutlined,
    ReloadOutlined,
    HolderOutlined,
    ArrowRightOutlined, CloudUploadOutlined, UploadOutlined, PaperClipOutlined,
} from '@ant-design/icons';
import {Sender} from '@ant-design/x';
import {motion, AnimatePresence, Reorder} from 'framer-motion';
import {flushSync} from 'react-dom';
import styles from '@/styles/ppt/index.module.css';

import pptApi from '@/api/ppt';
import {PptQuery, streamPptOutline} from '@/api/ppt/streamPptOutline';
import {UserInfo} from "@/types/login/LoginType";
import {pptExportParam} from "@/types/ppt/PptType";

const {Text} = Typography;

interface OutlineNode {
    id: string;
    level: number;
    type: 'title' | 'chapter' | 'section' | 'subsection' | 'content';
    content: string;
}

const TYPE_META: Record<OutlineNode['type'], { label: string; tagColor: string }> = {
    title:      {label: '主题', tagColor: '#0D47A1'},
    chapter:    {label: '章节', tagColor: '#1976D2'},
    section:    {label: '小节', tagColor: '#42A5F5'},
    subsection: {label: '子节', tagColor: '#64B5F6'},
    content:    {label: '内容', tagColor: '#90CAF9'},
};

const LEVEL_INDENT = [0, 20, 40, 60, 80];
const LEVEL_BORDER: Record<number, string> = {
    0: '3px solid #165DFF',
    1: '3px solid #58A9FF',
    2: '2px solid #83C2FF',
    3: '2px solid #ADD8FF',
    4: '2px solid #D6E8FF',
};

function parseOutlineLine(line: string, index: number): OutlineNode | null {
    const titleMatch = line.match(/^(#{1,6})\s+(.+)$/);
    if (titleMatch) {
        const level = Math.min(titleMatch[1].length - 1, 4);
        const typeMap: OutlineNode['type'][] = ['title', 'chapter', 'section', 'subsection', 'content'];
        return {id: `node-${index}`, level, type: typeMap[level] ?? 'content', content: titleMatch[2].trim()};
    }
    const listMatch = line.match(/^(\s*)-\s+(.+)$/);
    if (listMatch) {
        const level = Math.min(Math.floor(listMatch[1].length / 2) + 4, 4);
        return {id: `node-${index}`, level, type: 'content', content: listMatch[2].trim()};
    }
    if (line.trim()) {
        return {id: `node-${index}`, level: 4, type: 'content', content: line.trim()};
    }
    return null;
}

function parseMarkdown(text: string): OutlineNode[] {
    return text.split('\n').filter((l) => l.trim()).map((l, i) => parseOutlineLine(l, i)).filter(Boolean) as OutlineNode[];
}

function toMarkdown(nodes: OutlineNode[]): string {
    return nodes.map((n) => `${'#'.repeat(n.level + 1)} ${n.content}`).join('\n');
}

const generatePptMemoryId = (): string => {
    if (typeof window === 'undefined') return `ppt-memory-${Date.now()}`;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) throw new Error('用户信息不存在，请先登录');
    const userInfo: any = JSON.parse(userInfoStr);
    return '用户' + userInfo.id + '_PPT_' + Date.now().toString();
};

const getUserId = (): string | null => {
    if (typeof window === 'undefined') return null;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    try {
        const userInfo: any = JSON.parse(userInfoStr);
        return String(userInfo.id);
    } catch {
        return null;
    }
};

export default function PptGeneratePage() {

    const [title, setTitle] = useState('');
    const [outlineNodes, setOutlineNodes] = useState<OutlineNode[]>([]);
    const [sseStatus, setSseStatus] = useState<'idle' | 'loading' | 'done' | 'error'>('idle');
    const [statusMsg, setStatusMsg] = useState<{ text: string; type: 'success' | 'error' } | null>(null);
    const [exportMsg, setExportMsg] = useState<{ text: string; link?: string } | null>(null);
    const [pptLoading, setPptLoading] = useState(false);
    const [exportLoading, setExportLoading] = useState(false);
    const [showFullScreenSdk, setShowFullScreenSdk] = useState(false);
    const [isInputMoved, setIsInputMoved] = useState(false);
    const [confirmModalVisible, setConfirmModalVisible] = useState(false);

    const [recording, setRecording] = useState(false);
    const SpeechRecognitionRef = useRef<any>(null);
    const recognitionInstance = useRef<any>(null);
    const recordStartValueRef = useRef('');

    const taskIdRef = useRef<string | null>(null);
    const pptArtifactIdRef = useRef<string | null>(null);
    const pollTimerRef = useRef<ReturnType<typeof setInterval> | null>(null);
    const abortCtrlRef = useRef<AbortController | null>(null);

    const [fileList, setFileList] = useState<UploadFile[]>([]);

    const [open, setOpen] = React.useState(false);

    const {token} = theme.useToken();

    const [fileName, setFileName] = useState('');

    const pptMemoryIdRef = useRef<string>('');


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

    useEffect(() => {
        const G = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
        if (G) SpeechRecognitionRef.current = G;
    }, []);

    useEffect(() => {
        const script = document.createElement('script');
        script.src = 'https://quanmiao-public.oss-cn-beijing.aliyuncs.com/quanmiao-sdk/v1.0.0/index.js';
        script.async = true;
        script.onload = () => console.log('SDK 加载完成');
        script.onerror = () => console.error('SDK 加载失败');
        document.body.appendChild(script);
        return () => { script.remove(); };
    }, []);

    useEffect(() => {
        if (!pptMemoryIdRef.current) {
            try {
                const memoryId = generatePptMemoryId();
                pptMemoryIdRef.current = memoryId;
                console.log('PPT 记忆 ID 已生成：', memoryId);
            } catch (err) {
                message.error('用户未登录，无法生成记忆上下文');
            }
        }
    }, []);


    const uploadProps: UploadProps = useMemo(() => ({
        name: 'file',
        action: `${process.env.NEXT_PUBLIC_FETCH_API_URL}/admin-api/infra/file/upload`,
        headers: {
            fileflag: `authorization-PPT_FILE_UPLOAD-${encodeURIComponent(pptMemoryIdRef.current)}`,
        },
        fileList,
        limit: 1, // 限制1个文件
        beforeUpload: (file, fileList) => {
            if (fileList.length > 1) {
                message.warning('仅支持上传一个文件！');
                return false;
            }
            return true;
        },
        onChange(info) {
            // 自动只保留最后一个文件，确保永远只有一个
            setFileList(info.fileList.slice(-1));
            if (info.file.status !== 'uploading') {
                console.log(info.file, info.fileList);
            }
            if (info.file.status === 'done') {
                message.success(`${info.file.name} 文件上传成功`);
                const fileName = info.file.name || '';
                setFileName(fileName);
            } else if (info.file.status === 'error') {
                message.error(`${info.file.name} 文件上传失败`);
            }
        },
    }), [fileList]);

    const headerNode = (
        <div style={{
            position: 'absolute',
            top: -10,
            left: 0,
            right: 0,
            zIndex: 999,
            background: '#fff',
            borderRadius: 16,
            boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
            transform: 'translateY(-100%)'
        }}>
        <Sender.Header title="附件上传" open={open} onOpenChange={setOpen}>
            <Flex vertical align="center" gap="small" style={{ marginBlock: token.paddingLG }}>
                <CloudUploadOutlined style={{ fontSize: '4em' }} />
                <Typography.Title level={5} style={{ margin: 0 }}>
                    拖拽文件到此处
                </Typography.Title>
                <Typography.Text type="secondary">
                    支持 pdf, doc, xlsx, ppt, txt, 图片等文件类型
                </Typography.Text>
                <Upload {...uploadProps}  fileList={fileList}>
                    <Button
                        icon={<UploadOutlined />}
                    >
                        点击上传
                    </Button>
                </Upload>
            </Flex>
        </Sender.Header>
        </div>
    );

    const handleRecordingChange = (nextRecording: boolean) => {
        if (!SpeechRecognitionRef.current) { message.error('当前浏览器不支持语音识别'); return; }
        if (nextRecording) {
            recordStartValueRef.current = title;
            const rec = new SpeechRecognitionRef.current();
            rec.lang = 'zh-CN';
            rec.continuous = true;
            rec.interimResults = true;
            rec.onresult = (event: any) => {
                let speechText = '';
                for (let i = 0; i < event.results.length; ++i) speechText += event.results[i][0].transcript;
                setTitle(recordStartValueRef.current + speechText);
            };
            rec.onerror = () => setRecording(false);
            rec.onend = () => setRecording(false);
            try { rec.start(); recognitionInstance.current = rec; setRecording(true); } catch (err) { console.error(err); }
        } else {
            if (recognitionInstance.current) recognitionInstance.current.stop();
            setRecording(false);
        }
    };

    const runPptOutlineGeneration = useCallback(async () => {
        if (!title.trim()) { message.warning('请输入 PPT 主题'); return; }

        setIsInputMoved(true);
        taskIdRef.current = null;
        pptArtifactIdRef.current = null;
        setOutlineNodes([]);
        setSseStatus('loading');
        setStatusMsg(null);
        setExportMsg(null);

        abortCtrlRef.current?.abort();
        const abort = new AbortController();
        abortCtrlRef.current = abort;


        try {
            const mockOutlineText = `# ${title}
## 第一章 概述
### 1.1 背景介绍
### 1.2 核心概念
## 第二章 详细内容
### 2.1 关键要点
### 2.2 案例分析
## 第三章 总结展望
### 3.1 主要结论
### 3.2 未来方向`;

            const lines = mockOutlineText.split('\n');
            let currentText = '';
            for (let i = 0; i < lines.length; i++) {
                if (abort.signal.aborted) break;
                await new Promise(resolve => setTimeout(resolve, 150));
                currentText += (i > 0 ? '\n' : '') + lines[i];
                const nodes = parseMarkdown(currentText);
                flushSync(() => { setOutlineNodes([...nodes]); });
            }
            taskIdRef.current = 'task-' + Date.now();
            // setSseStatus('done');
        } catch (err: any) {
            setSseStatus('error');

        }

        const pptQuery : PptQuery = {
            query: title,
            fileName: fileName,
            pptMemoryId: pptMemoryIdRef.current,
        }

        try {
            await streamPptOutline(
                pptQuery,
                async (data, isFinal) => {
                    if (!isFinal) {
                        if (data.taskId) taskIdRef.current = data.taskId;
                        if (data.text) {
                            const nodes = parseMarkdown(data.text);
                            flushSync(() => { setOutlineNodes([...nodes]); });
                            await new Promise<void>((resolve) => setTimeout(resolve, 0));
                        }
                    } else {
                        if (data.taskId) taskIdRef.current = data.taskId;
                        flushSync(() => { setSseStatus('done'); });

                    }
                },
                abort.signal,
            );
        } catch (err: any) {
            setSseStatus('error');

        }
    }, [title]);

    const updateNode = useCallback((index: number, value: string) => {
        setOutlineNodes((prev) => {
            const next = [...prev];
            next[index] = {...next[index], content: value};
            return next;
        });
    }, []);

    const handleReorder = (newOrder: OutlineNode[]) => { setOutlineNodes(newOrder); };

    const initiatePptCreation = useCallback(async () => {
        if (!taskIdRef.current) { setStatusMsg({text: '请先生成大纲', type: 'error'}); return; }

        setShowFullScreenSdk(true);
        setPptLoading(true);
        setStatusMsg(null);

        const markdownContent = toMarkdown(outlineNodes);

        try {
            await new Promise(resolve => setTimeout(resolve, 1000));
            pptArtifactIdRef.current = 'artifact-' + Date.now();
            setStatusMsg({text: 'PPT 生成完成', type: 'success'});
        } catch (e: any) {
            message.error(e?.msg || e?.message || '生成失败');
        } finally {
            setPptLoading(false);
        }

        try {
            const resp = await pptApi.initiatePptCreation({taskId: taskIdRef.current, outline: markdownContent});
            const appKey = (resp as any)?.appKey ?? '';
            const secret = (resp as any)?.secret ?? '';

            if (!(window as any).Quanmiao?.createPPT) { message.error('SDK 未加载完成，请稍后重试'); return; }

            await (window as any).Quanmiao.createPPT({
                appkey: appKey,
                code: secret,
                container: document.getElementById('ppt-sdk-container')!,
                content: markdownContent,
                speaker: 'PPT主讲人',
                onMessage: (type: string, data: any) => {
                    if (type === 'SET_PPT_MAKING_STATUS') {
                        if (data?.status === '1') setStatusMsg({text: 'PPT 生成中...', type: 'success'});
                        if (data?.status === '0') setStatusMsg({text: 'PPT 生成完成', type: 'success'});
                    }
                    if (type === 'GENERATE_PPT_SUCCESS') {
                        pptArtifactIdRef.current = data?.id ?? null;
                        if (taskIdRef.current && pptArtifactIdRef.current) {
                            pptApi.bindPptArtifact({taskId: taskIdRef.current, artifactId: pptArtifactIdRef.current});
                        }
                        exportPptArtifact()
                    }
                },
            });
        } catch (e: any) {
            message.error(e?.msg || e?.message || '生成失败');
        } finally {
            setPptLoading(false);
        }
    }, [outlineNodes]);

    const exportPptArtifact = useCallback(async () => {
        if (!pptArtifactIdRef.current) { message.warning('请先生成 PPT'); return; }
        setExportLoading(true);
        setExportMsg({text: '正在导出...'});
        try {
            await new Promise(resolve => setTimeout(resolve, 2000));
            setExportMsg({text: '导出完成', link: '#'});
        } catch (err: any) {
            setExportMsg({text: '导出失败：' + err.message});
        } finally {
            setExportLoading(false);
        }

        try {
            const resp: any = await pptApi.exportPptArtifact({artifactId: pptArtifactIdRef.current});
            const exportTaskId = resp?.exportTaskId;
            if (!exportTaskId) { setExportMsg({text: '导出任务创建失败'}); return; }
            let count = 0;
            if (pollTimerRef.current) clearInterval(pollTimerRef.current);
            pollTimerRef.current = setInterval(async () => {
                count++;
                try {
                    const param: pptExportParam = {
                        exportTaskId: exportTaskId,
                        clientUserId: getUserId()
                    }
                    const result: any = await pptApi.getPptArtifactExportResult(param);
                    if (result) {
                        clearInterval(pollTimerRef.current!);
                        setExportMsg({text: '导出完成'});
                        setExportLoading(false);
                    } else if (count >= 30) {
                        clearInterval(pollTimerRef.current!);
                        setExportMsg({text: '导出超时，请重试'});
                        setExportLoading(false);
                    }
                } catch {
                    clearInterval(pollTimerRef.current!);
                    setExportMsg({text: '查询导出结果失败'});
                    setExportLoading(false);
                }
            }, 2000);
        } catch (err: any) {
            setExportMsg({text: '导出失败：' + err.message});
            setExportLoading(false);
        }
    }, []);

    const handleConfirmBack = () => { setConfirmModalVisible(false); setShowFullScreenSdk(false); };

    const containerVariants = {
        initial: {opacity: 0},
        animate: {opacity: 1, transition: {duration: 0.5, ease: [0.19, 1, 0.22, 1]}},
    };

    const outlineVariants = {
        hidden: {opacity: 0, y: 20},
        visible: {opacity: 1, y: 0, transition: {duration: 0.5, ease: [0.19, 1, 0.22, 1]}},
    };

    const itemVariants = {
        hidden: {opacity: 0, x: -20},
        visible: (i: number) => ({
            opacity: 1, x: 0,
            transition: {delay: i * 0.05, duration: 0.4, ease: [0.19, 1, 0.22, 1]},
        }),
    };

    const showNextButton = sseStatus === 'done' && outlineNodes.length > 0;

    return (
        <div className={styles.container}>
            <AnimatePresence mode="wait">
                {!showFullScreenSdk && (
                    <motion.div
                        key="outline-page"
                        className={styles.outlinePage}
                        variants={containerVariants}
                        initial="initial"
                        animate="animate"
                        exit={{opacity: 0, transition: {duration: 0.3}}}
                    >
                        {/* ========== 1. 顶部固定：输入框 ========== */}
                        <motion.div
                            className={`${styles.inputSection} ${isInputMoved ? styles.inputSectionTop : styles.inputSectionCenter}`}
                        >
                            <div className={styles.inputWrapper}>
                                {!isInputMoved && (
                                    <motion.div
                                        className={styles.welcomeTitle}
                                        initial={{opacity: 0, y: -20}}
                                        animate={{opacity: 1, y: 0}}
                                        transition={{duration: 0.5, ease: [0.19, 1, 0.22, 1]}}
                                    >
                                        <FileTextOutlined className={styles.welcomeIcon}/>
                                        <span>AI PPT 智能生成</span>
                                    </motion.div>
                                )}
                                <div className={styles.senderWrapper}>
                                    <Sender
                                        header={headerNode}
                                        prefix={
                                            <Button
                                                type="text"
                                                style={{ fontSize: 16 }}
                                                icon={<PaperClipOutlined />}
                                                onClick={() => {
                                                    setOpen(!open);
                                                }}
                                            />
                                        }
                                        value={title}
                                        onChange={setTitle}
                                        onSubmit={runPptOutlineGeneration}
                                        loading={sseStatus === 'loading'}
                                        placeholder="输入 PPT 主题，例如：人工智能发展现状与未来趋势"
                                        autoSize={{minRows: 1, maxRows: 4}}
                                        allowSpeech={{recording, onRecordingChange: handleRecordingChange}}
                                    />
                                </div>
                                {!isInputMoved && (
                                    <motion.div
                                        className={styles.hintText}
                                        initial={{opacity: 0}}
                                        animate={{opacity: 1}}
                                        transition={{delay: 0.3, duration: 0.5}}
                                    >
                                        输入主题后按 Enter 或点击发送按钮开始生成大纲
                                    </motion.div>
                                )}
                            </div>
                        </motion.div>

                        {/* ========== 2. 中间唯一滚动区域：大纲预览 ========== */}
                        <div className={styles.middleScrollSection}>
                            <AnimatePresence>
                                {sseStatus === 'loading' && outlineNodes.length === 0 && (
                                    <motion.div
                                        className={styles.loadingSection}
                                        initial={{opacity: 0}}
                                        animate={{opacity: 1}}
                                        exit={{opacity: 0}}
                                    >
                                        <div className={styles.loadingDots}>
                                            <span/><span/><span/>
                                        </div>
                                        <Text className={styles.loadingText}>正在生成大纲...</Text>
                                    </motion.div>
                                )}
                            </AnimatePresence>

                            <AnimatePresence>
                                {outlineNodes.length > 0 && (
                                    <motion.div
                                        className={styles.outlineSection}
                                        variants={outlineVariants}
                                        initial="hidden"
                                        animate="visible"
                                        exit={{opacity: 0, y: -20}}
                                    >
                                        <div className={styles.outlineHeader}>
                                          <span className={styles.outlineTitle}>
                                            <FileTextOutlined style={{marginRight: 8}}/>
                                            大纲预览
                                          </span>

                                            {/* 生成中：显示提示，隐藏按钮 */}
                                            {sseStatus === 'loading' && (
                                                <Typography.Text type="secondary" style={{ fontSize: '14px' }}>
                                                    正在生成大纲中...
                                                </Typography.Text>
                                            )}

                                            {/* 生成完成：显示成功提示 + 重新生成按钮 */}
                                            {sseStatus === 'done' && (
                                                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                                                    <Typography.Text type="success" style={{ fontSize: '14px' }}>
                                                        大纲生成完毕
                                                    </Typography.Text>
                                                    <Button
                                                        icon={<ReloadOutlined />}
                                                        onClick={runPptOutlineGeneration}
                                                        type="text"
                                                    >
                                                        重新生成
                                                    </Button>
                                                </div>
                                            )}

                                            {/* 生成失败：显示失败提示 + 重新生成按钮 */}
                                            {sseStatus === 'error' && (
                                                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                                                    <Typography.Text type="danger" style={{ fontSize: '14px' }}>
                                                        大纲生成失败
                                                    </Typography.Text>
                                                    <Button
                                                        icon={<ReloadOutlined />}
                                                        onClick={runPptOutlineGeneration}
                                                        type="text"
                                                    >
                                                        重新生成
                                                    </Button>
                                                </div>
                                            )}
                                        </div>

                                        {/* 唯一滚动容器 */}
                                        <div className={styles.outlineContent}>
                                            <Reorder.Group
                                                axis="y"
                                                values={outlineNodes}
                                                onReorder={handleReorder}
                                                className={styles.outlineList}
                                            >
                                                {outlineNodes.map((node, idx) => {
                                                    const lv = Math.min(node.level, 4);
                                                    const meta = TYPE_META[node.type];
                                                    return (
                                                        <Reorder.Item
                                                            key={node.id}
                                                            value={node}
                                                            className={styles.outlineItem}
                                                            style={{
                                                                borderLeft: LEVEL_BORDER[lv],
                                                                paddingLeft: LEVEL_INDENT[lv] + 16,
                                                            }}
                                                            whileDrag={{
                                                                scale: 1.02,
                                                                boxShadow: '0 8px 24px rgba(0,0,0,0.12)',
                                                                cursor: 'grabbing',
                                                            }}
                                                        >
                                                            <motion.div
                                                                className={styles.outlineItemInner}
                                                                custom={idx}
                                                                variants={itemVariants}
                                                                initial="hidden"
                                                                animate="visible"
                                                            >
                                                                <HolderOutlined className={styles.dragHandle}/>
                                                                <Tag color={meta.tagColor} className={styles.typeTag}>
                                                                    {meta.label}
                                                                </Tag>
                                                                <Input
                                                                    value={node.content}
                                                                    onChange={(e) => updateNode(idx, e.target.value)}
                                                                    className={styles.nodeInput}
                                                                    variant="borderless"
                                                                />
                                                            </motion.div>
                                                        </Reorder.Item>
                                                    );
                                                })}
                                            </Reorder.Group>
                                        </div>
                                    </motion.div>
                                )}
                            </AnimatePresence>
                        </div>

                        {/* ========== 3. 底部固定：下一步按钮 ========== */}
                        <AnimatePresence>
                            {showNextButton && (
                                <motion.div
                                    className={styles.nextStepWrapper}
                                    initial={{opacity: 0, y: 16}}
                                    animate={{opacity: 1, y: 0}}
                                    exit={{opacity: 0, y: 16}}
                                    transition={{duration: 0.35, ease: [0.19, 1, 0.22, 1]}}
                                >
                                    <Button
                                        type="primary"
                                        size="large"
                                        icon={<ArrowRightOutlined/>}
                                        loading={pptLoading}
                                        onClick={initiatePptCreation}
                                        className={styles.nextStepButton}
                                    >
                                        下一步：生成 PPT
                                    </Button>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </motion.div>
                )}

                {/* ── 全屏 SDK 页 ── */}
                {showFullScreenSdk && (
                    <motion.div
                        key="sdk-page"
                        className={styles.sdkPage}
                        initial={{opacity: 0}}
                        animate={{opacity: 1}}
                        exit={{opacity: 0}}
                        transition={{duration: 0.4}}
                    >
                        {exportMsg && (
                            <div className={styles.exportMsg}>
                                <Text style={{color: exportMsg.link ? '#52c41a' : '#666'}}>{exportMsg.text}</Text>
                                {exportMsg.link && (
                                    <a href={exportMsg.link} target="_blank" rel="noreferrer">点击下载</a>
                                )}
                            </div>
                        )}
                        <div id="ppt-sdk-container" className={styles.sdkContainer}/>
                    </motion.div>
                )}
            </AnimatePresence>

            <Modal
                title="确认返回"
                open={confirmModalVisible}
                onOk={handleConfirmBack}
                onCancel={() => setConfirmModalVisible(false)}
                okText="确定"
                cancelText="取消"
                centered
            >
                <p>确定要返回大纲编辑页面吗？当前的 PPT 编辑进度将会保留。</p>
            </Modal>
        </div>
    );
}