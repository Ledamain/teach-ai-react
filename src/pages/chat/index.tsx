'use client';
import {
    CodeOutlined,
    CodeSandboxOutlined,
    CopyOutlined,
    RedoOutlined,
    UserOutlined,
    RobotOutlined,
    OpenAIOutlined,
    SyncOutlined, LogoutOutlined, SettingOutlined,
    CloudUploadOutlined, PaperClipOutlined, UploadOutlined,
    PlayCircleOutlined, CheckOutlined,
    DatabaseOutlined,
    FileTextOutlined,
    VerticalLeftOutlined,
    VerticalRightOutlined, AppstoreOutlined,
    TeamOutlined, SwapOutlined, FileDoneOutlined, ReadOutlined, FileImageOutlined,VideoCameraAddOutlined
} from '@ant-design/icons';
import {
    Conversations,
    ConversationsProps,
    Actions,
    Bubble,
    Sender,
    Welcome,
    Think,
} from '@ant-design/x';
import {Prism as SyntaxHighlighter} from 'react-syntax-highlighter';
import {atomDark} from 'react-syntax-highlighter/dist/esm/styles/prism';
import KeyCode from '@rc-component/util/lib/KeyCode';
import {
    GetProp,
    message as Message,
    Flex,
    Avatar,
    theme,
    Dropdown,
    MenuProps,
    Divider,
    message, Button,
    Typography, UploadProps, Upload, UploadFile,
    Collapse, Switch, Spin, Tag, Modal, Tooltip
} from 'antd';
import ImageGeneratePage from '@/pages/image/index';
import {css, Global} from '@emotion/react';
import React, {useState, useRef, useEffect, useCallback, useMemo} from 'react';
import {motion, AnimatePresence} from 'framer-motion';
import styles from '@/styles/chat/index.module.css';
import Image from 'next/image';
import logoImage from '@/assets/images/logo.jpg';
import {streamChat} from "@/api/chat";
import {router} from "next/client";
import {ChatStreamParams} from "@/types/chat/ChatType";
import {UserInfo} from "@/types/login/LoginType";
import {getHistoryList, getHistory} from "@/api/chathistory";
import {getKnowledgeArray, getKnowledgeList} from "@/api/repo";
import WorkspaceModal from "./WorkspaceModal"

// ========================
// 引入 PPT 页面组件
// ========================
import PptGeneratePage from '@/pages/ppt/index';
import {Workspace} from "@/components/workspace";
import {Course} from "@/types/workspace/WorkspaceType";
import {KnowledgeCategory, KnowLedgeCourseParams} from "@/types/repo/RepoType";
import {StudentWorkspace} from "@/components/student-workspace";
import HotQueryBubbles from "@/components/HotQueryBubbles";
import DocPreviewPanel from "@/components/DocPreviewPanel";
import {MessageContent} from '@/components/MessageContent'
import VideoGeneratePage from "@/pages/video";

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;

type MessageRole = 'user' | 'assistant';
type CurrentView = 'chat' | 'ppt' | 'image' | 'video';
type UserRole = 'teacher' | 'student';

interface ChatMessage {
    id: string;
    role: MessageRole;
    content: string;
    thinking?: boolean;
    isHistorical?: boolean;
    isComplete?: boolean;
}

type MessagesMap = Record<string, ChatMessage[]>;

type SuperView = 'chatSystem' | 'workspace';

const userActionItems = [
    {key: 'retry', icon: <RedoOutlined/>, label: 'Retry'},
    {key: 'copy', icon: <CopyOutlined/>, label: 'Copy'},
];
const assistantActionItems = [
    {key: 'copy', icon: <CopyOutlined/>, label: 'Copy'},
];

const GROUP_ORDER: Record<string, number> = {
    '今天': 0,
    '昨天': 1,
    '最近 7 天': 2,
    '最近 30 天': 3,
    '更早': 4,
    'More Features': 5,
};

const getTimeGroup = (timestamp: number): string => {
    const now = new Date();
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
    const yesterdayStart = todayStart - 86400000;
    const sevenDaysAgo = todayStart - 6 * 86400000;
    const thirtyDaysAgo = todayStart - 29 * 86400000;

    if (timestamp >= todayStart) return '今天';
    if (timestamp >= yesterdayStart) return '昨天';
    if (timestamp >= sevenDaysAgo) return '最近 7 天';
    if (timestamp >= thirtyDaysAgo) return '最近 30 天';
    return '更早';
};

const generateMemoryId = (): string => {
    if (typeof window === 'undefined') return `temp-${Date.now()}`;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) throw new Error('用户信息不存在，请先登录');
    const userInfo: UserInfo = JSON.parse(userInfoStr);
    return '用户' + userInfo.id + '_' + Date.now().toString();
};

const getUserId = (): string | null => {
    if (typeof window === 'undefined') return null;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    try {
        const userInfo: UserInfo = JSON.parse(userInfoStr);
        return String(userInfo.id);
    } catch {
        return null;
    }
};

const getUserClientRole = (): string | null => {
    if (typeof window === 'undefined') return null;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    try {
        const userInfo: UserInfo = JSON.parse(userInfoStr);
        return String(userInfo.clientRole);
    } catch {
        return null;
    }
};
// ========================
// 动画变体
// ========================
const historicalMsgVariants = {
    hidden: {opacity: 0, y: -20, scale: 0.98},
    visible: (i: number) => ({
        opacity: 1,
        y: 0,
        scale: 1,
        transition: {
            delay: i * 0.06,
            duration: 0.4,
            ease: [0.19, 1, 0.22, 1],
        },
    }),
};

const newMsgVariants = {
    hidden: {opacity: 0, y: 12},
    visible: {
        opacity: 1,
        y: 0,
        transition: {duration: 0.35, ease: [0.19, 1, 0.22, 1]},
    },
};

const viewVariants = {
    hidden: { opacity: 0, y: 8 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.35 } },
    exit: { opacity: 0, y: -8, transition: { duration: 0.2 } },
};

// ========================
// 主组件
// ========================
const Chat: React.FC = () => {
    const {token} = theme.useToken();
    const abortControllerRef = useRef<AbortController | null>(null);

    const [mounted, setMounted] = useState(false);
    const [activeKey, setActiveKey] = useState<string>('');
    const [messagesMap, setMessagesMap] = useState<MessagesMap>({});
    const [memoryIdMap, setMemoryIdMap] = useState<Record<string, string>>({});
    const [inputValue, setInputValue] = useState<string>('');
    const [sending, setSending] = useState<boolean>(false);
    const [historicalItems, setHistoricalItems] = useState<GetProp<ConversationsProps, 'items'>>([]);
    const [pendingItem, setPendingItem] = useState<GetProp<ConversationsProps, 'items'>[0] | null>(null);
    const [historyKeySet, setHistoryKeySet] = useState<Set<string>>(new Set());
    const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
    const [animContainerKey, setAnimContainerKey] = useState<string>('');
    const [currentView, setCurrentView] = useState<CurrentView>('chat');
    const [superView, setSuperView] = useState<SuperView>('chatSystem');

    const messagesEndRef = useRef<HTMLDivElement>(null);
    const messagesScrollContainerRef = useRef<HTMLDivElement>(null);
    const pendingHistoryScrollRef = useRef<ReturnType<typeof setTimeout> | null>(null);
    const fullResponseRef = useRef<string>('');
    const aiMessageIdRef = useRef<string>('');
    const activeKeyRef = useRef<string>('');
    const [recording, setRecording] = React.useState(false);
    const SpeechRecognitionRef = useRef<any>(null);
    const recognitionInstance = useRef<any>(null);
    const recordStartValueRef = useRef('');
    const [open, setOpen] = React.useState(false);
    const currentMemoryId = memoryIdMap[activeKey] || '';
    const [fileList, setFileList] = useState<UploadFile[]>([]);

    // 知识库状态
    const [knowledgeVisible, setKnowledgeVisible] = useState(false);
    const [knowledgeList, setKnowledgeList] = useState<KnowledgeCategory[]>([]);
    const [activeKnowledgeIds, setActiveKnowledgeIds] = useState<string[]>([]);
    const [knowledgeLoading, setKnowledgeLoading] = useState(false);
    const [workspaceVisible, setWorkspaceVisible] = useState(false);
    const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);

    // 文档预览状态
    const [docContent, setDocContent] = useState<string>('');
    const [showDocPanel, setShowDocPanel] = useState<boolean>(false);
    const [userRole, setUserRole] = useState<UserRole>('student');

    // 检测是否为教学文档
    const isTeachingDoc = (text: string): boolean => {
        const keywords = ['教学目标', '教学内容', '课程大纲', '学习目标', '教案', '课程介绍'];
        return keywords.some(kw => text.includes(kw));
    };

    const handleQuestionClick = (question: string) => {
        setInputValue(question)
        handleSend(question)
    };

    // 弹窗控制
    const openWorkspaceModal = () => setWorkspaceVisible(true);
    const closeWorkspaceModal = () => setWorkspaceVisible(false);

    // 选择课程
    const handleCourseSelect = async (course: Course) => {
        if (course === null){
            setSelectedCourse(course);
            closeWorkspaceModal();
            setActiveKnowledgeIds([])
        }else {
            setSelectedCourse(course);
            message.success(`已选择学科：${course.repoCategoryName}`);
            const knowledgeParams : KnowLedgeCourseParams = {
                repoCategoryId: Number(course.id),
            };
            const res = await getKnowledgeArray(knowledgeParams);
            setActiveKnowledgeIds(res)
            closeWorkspaceModal();
        }
    };

    const openWorkspace = useCallback(() => setSuperView('workspace'), []);
    const backToChatSystem = useCallback(() => setSuperView('chatSystem'), []);

    // 展示用户角色
    const showUserRole = () => {
        if (getUserClientRole() === "1"){
            return (
                <Tag icon={<UserOutlined />} color="#55acee">
                    教师
                </Tag>
            )
        }else if (getUserClientRole() === "0"){
            return (
                <Tag icon={<TeamOutlined />} color="#108ee9">
                    学生
                </Tag>
            )
        }
    }

    // 提示使用模式
    const showUseMode = () => {
        if (selectedCourse === null){
            return (
                <Tag icon={<FileDoneOutlined />} color="#108ee9">
                    通用模式
                </Tag>
            )
        } else {
            return (
                <Tag icon={<ReadOutlined />} color="#f50">
                    专属知识库：{selectedCourse.repoCategoryName}
                </Tag>
            )
        }
    }

    const uploadProps: UploadProps = useMemo(() => ({
        name: 'file',
        action: `${process.env.NEXT_PUBLIC_FETCH_API_URL}/admin-api/infra/file/upload`,
        headers: {
            fileflag: `authorization-${encodeURIComponent(currentMemoryId)}`,
        },
        fileList,
        onChange(info) {
            setFileList(info.fileList);
            if (info.file.status === 'done') {
                message.success(`${info.file.name} 文件上传成功`);
            } else if (info.file.status === 'error') {
                message.error(`${info.file.name} 文件上传失败`);
            }
        },
    }), [currentMemoryId, fileList]);

    const agentItems: GetProp<ConversationsProps, 'items'> = [
        {
            key: 'coding',
            label: 'PPT 生成',
            icon: <CodeOutlined />,
        },
        ...(!selectedCourse ? [
            {
                key: 'knowledge',
                label: (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        width: '100%'
                    }}>
                        知识库选择
                        {knowledgeVisible ? (
                            <VerticalRightOutlined />
                        ) : (
                            <VerticalLeftOutlined />
                        )}
                    </div>
                ),
                icon: <DatabaseOutlined />,
            }
        ]:[])
    ];

    const headerNode = (
        <Sender.Header title="附件上传" open={open} onOpenChange={setOpen}>
            <Flex vertical align="center" gap="small" style={{ marginBlock: token.paddingLG }}>
                <CloudUploadOutlined style={{ fontSize: '4em' }} />
                <Typography.Title level={5} style={{ margin: 0 }}>
                    拖拽文件到此处
                </Typography.Title>
                <Typography.Text type="secondary">
                    支持 pdf, doc, xlsx, ppt, txt, 图片等文件类型
                </Typography.Text>
                <Upload {...uploadProps} disabled={!currentMemoryId} fileList={fileList}>
                    <Button
                        icon={<UploadOutlined />}
                        disabled={!currentMemoryId}
                    >
                        {currentMemoryId ? '点击上传' : '请先开启对话'}
                    </Button>
                </Upload>
            </Flex>
        </Sender.Header>
    );

    useEffect(() => {
        activeKeyRef.current = activeKey;
    }, [activeKey]);

    // 获取知识库列表
    const fetchKnowledgeList = useCallback(async () => {
        setKnowledgeLoading(true);
        try {
            const res = await getKnowledgeList();
            setKnowledgeList(res);
            setKnowledgeLoading(false);
        } catch (err) {
            message.error('获取知识库失败');
            setKnowledgeLoading(false);
        }
    }, []);

    // 切换知识库
    const toggleKnowledge = (fileId: string) => {
        setActiveKnowledgeIds(prev =>
            prev.includes(fileId)
                ? prev.filter(id => id !== fileId)
                : [...prev, fileId]
        );
    };

    useEffect(() => {
        if (knowledgeVisible && knowledgeList.length === 0) {
            fetchKnowledgeList();
        }
    }, [knowledgeVisible, knowledgeList, fetchKnowledgeList]);

    const fetchHistoryList = useCallback(async () => {
        const userId = getUserId();
        if (!userId) return;
        try {
            const res = await getHistoryList(userId);
            let list: any[] | null = null;
            if (Array.isArray(res)) list = res;
            else if (Array.isArray(res?.data)) list = res.data;
            else if (Array.isArray(res?.data?.data)) list = res.data.data;

            if (!list) return;

            const items: GetProp<ConversationsProps, 'items'> = list.map((item: any) => ({
                key: item.memoryId,
                label: item.messageTitle || item.memoryId,
                group: getTimeGroup(item.createTime),
            }));

            setHistoricalItems(items);
            setHistoryKeySet(new Set(list.map((item: any) => item.memoryId)));
        } catch (e) {
            console.error('[fetchHistoryList] 请求失败:', e);
        }
    }, []);

    useEffect(() => {
        if (mounted) {
            fetchHistoryList();
            if (!activeKey) {
                const newKey = `new-${Date.now()}`;
                const newMemoryId = generateMemoryId();
                setMemoryIdMap(prev => ({ ...prev, [newKey]: newMemoryId }));
                setMessagesMap(prev => ({ ...prev, [newKey]: [] }));
                setActiveKey(newKey);
                setAnimContainerKey(newKey);
                setPendingItem({ key: newKey, label: '新对话', group: '今天' });
            }
        }
    }, [mounted]);

    const handleRecordingChange = (nextRecording: boolean) => {
        if (!SpeechRecognitionRef.current) {
            message.error('当前浏览器不支持语音识别');
            return;
        }
        if (nextRecording) {
            recordStartValueRef.current = inputValue;
            const rec = new SpeechRecognitionRef.current();
            rec.lang = 'zh-CN';
            rec.continuous = true;
            rec.interimResults = true;
            rec.onresult = (event: any) => {
                let speechText = '';
                for (let i = 0; i < event.results.length; ++i) {
                    speechText += event.results[i][0].transcript;
                }
                setInputValue(recordStartValueRef.current + speechText);
            };
            rec.onerror = () => setRecording(false);
            rec.onend = () => setRecording(false);
            try {
                rec.start();
                recognitionInstance.current = rec;
                setRecording(true);
            } catch (err) {
                console.error(err);
            }
        } else {
            if (recognitionInstance.current) recognitionInstance.current.stop();
            setRecording(false);
        }
    };

    useEffect(() => {
        setMounted(true);
        const G = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
        if (G) SpeechRecognitionRef.current = G;
    }, []);

    useEffect(() => {
        if (!mounted) return;
        try {
            const raw = window.localStorage.getItem('userInfo');
            if (raw) setUserInfo(JSON.parse(raw));
        } catch (e) {
            console.error('用户信息解析失败', e);
        }
    }, [mounted]);

    useEffect(() => {
        if (mounted) fetchHistoryList();
    }, [mounted, fetchHistoryList]);

    useEffect(() => {
        if (getUserClientRole() === '0'){
            setUserRole('student')
        }else if (getUserClientRole() === '1'){
            setUserRole('teacher')
        }
    },[userRole])

    const scrollToBottom = useCallback((behavior: ScrollBehavior = 'smooth') => {
        if (messagesScrollContainerRef.current) {
            const container = messagesScrollContainerRef.current;
            container.scrollTo({top: container.scrollHeight, behavior});
        } else {
            messagesEndRef.current?.scrollIntoView({behavior});
        }
    }, []);

    useEffect(() => {
        if (historyKeySet.has(activeKey)) return;
        scrollToBottom();
    }, [messagesMap[activeKey]]);

    // 切换历史对话
    const handleActiveChange = useCallback(async (key: string) => {
        if (pendingHistoryScrollRef.current) {
            clearTimeout(pendingHistoryScrollRef.current);
            pendingHistoryScrollRef.current = null;
        }
        setCurrentView('chat');
        setKnowledgeVisible(false);
        setActiveKey(key);
        setAnimContainerKey(key + '-' + Date.now());
        setFileList([]);

        if (historyKeySet.has(key) && !messagesMap[key]) {
            try {
                const res = await getHistory(key);
                const data =
                    res?.messages
                        ? res
                        : res?.data?.messages
                            ? res.data
                            : res?.data?.data?.messages
                                ? res.data.data
                                : null;

                if (!data) {
                    Message.error('历史消息格式异常');
                    return;
                }

                const rawMessages: Array<{ id: string; role: string; content: string; thinking: boolean; }> = data.messages ?? [];
                if (rawMessages.length > 0) {
                    const msgs: ChatMessage[] = rawMessages.map((m) => ({
                        id: m.id,
                        role: (m.role === 'user' ? 'user' : 'assistant') as MessageRole,
                        content: m.content,
                        thinking: false,
                        isHistorical: true,
                        isComplete: true,
                    }));

                    setMessagesMap(prev => ({...prev, [key]: msgs}));
                    setMemoryIdMap(prev => ({...prev, [key]: key}));

                    const totalWait = 250 + (msgs.length - 1) * 70 + 450 + 150;
                    pendingHistoryScrollRef.current = setTimeout(() => {
                        requestAnimationFrame(() => scrollToBottom('smooth'));
                        pendingHistoryScrollRef.current = null;
                    }, totalWait);
                } else {
                    setMessagesMap(prev => ({...prev, [key]: []}));
                }
            } catch (e) {
                console.error('获取历史消息失败', e);
                Message.error('加载历史消息失败');
            }
        }

        if (!historyKeySet.has(key)) {
            setMemoryIdMap(prev => ({...prev, [key]: prev[key] || generateMemoryId()}));
            setMessagesMap(prev => ({...prev, [key]: prev[key] || []}));
        }
    }, [historyKeySet, messagesMap, scrollToBottom]);

    useEffect(() => {
        return () => {
            if (pendingHistoryScrollRef.current) clearTimeout(pendingHistoryScrollRef.current);
        };
    }, []);

    const logout = async () => {
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
        await router.push('/login');
        message.success('登出成功');
    };
    const inSettings = async () => {await router.push('settings');};

    const menuItems: MenuProps['items'] = [
        {
            key: '1',
            label: (
                <div onClick={inSettings}>
                    <div className={styles.userLabel}>
                        <SettingOutlined/>
                        <span>个人设置</span>
                    </div>
                    <Divider size='small'/>
                </div>
            ),
        },
        {
            key: '2',
            label: (
                <div onClick={logout}>
                    <div className={styles.userLabel}>
                        <LogoutOutlined/>
                        <span>退出登录</span>
                    </div>
                    <Divider size='small'/>
                </div>
            ),
        },
    ];

    // 新建对话
    const newChatClick = () => {
        if (activeKey && !historyKeySet.has(activeKey) && currentView === 'chat') {
            const currentMsgs = messagesMap[activeKey] || [];
            if (currentMsgs.length === 0) {
                message.warning('请先在当前对话中发起一次提问，再新建对话');
                return;
            }
        }
        setCurrentView('chat');
        setKnowledgeVisible(false);
        const newKey = `new-${Date.now()}`;
        const newMemoryId = generateMemoryId();
        setMemoryIdMap(prev => ({...prev, [newKey]: newMemoryId}));
        setMessagesMap(prev => ({...prev, [newKey]: []}));
        setActiveKey(newKey);
        setAnimContainerKey(newKey);
        setPendingItem({key: newKey, label: '新对话', group: '今天'});
        setFileList([]);
    };

    const handleChatChunk: ChatChunkCallback = useCallback((chunk, isFinal) => {
        if (isFinal) return;
        fullResponseRef.current += chunk;
        const key = activeKeyRef.current;
        setMessagesMap((prev) => {
            const msgs = [...(prev[key] || [])];
            const last = msgs[msgs.length - 1];
            if (last?.id === aiMessageIdRef.current) {
                msgs[msgs.length - 1] = {...last, content: fullResponseRef.current, thinking: false};
            }
            return {...prev, [key]: msgs};
        });

        const finalContent = fullResponseRef.current;
        if (isTeachingDoc(finalContent)) {
            setDocContent(finalContent);
            setShowDocPanel(true);
        }
    }, []);

    const handleSend = async (text: string) => {
        if (!text.trim() || sending) return;
        const isUploading = fileList.some(file => file.status === 'uploading');
        if (isUploading) {
            message.warning('文件正在上传中，请等待上传完成后再发送消息');
            return;
        }
        const hasError = fileList.some(file => file.status === 'error');
        if (hasError) {
            message.warning('存在上传失败的文件，请将其移除或重新上传后再发送消息');
            return;
        }
        if (typeof window === 'undefined') {Message.error('请在浏览器环境中使用'); return;}

        const userInfoStr = window.localStorage.getItem('userInfo');
        if (!userInfoStr) {
            Message.error('用户信息不存在，请先登录');
            router.push('/login');
            return;
        }

        const currentActiveKey = activeKey;
        fullResponseRef.current = '';
        aiMessageIdRef.current = '';
        const controller = new AbortController();
        abortControllerRef.current = controller;

        const userMessage: ChatMessage = {
            id: Date.now().toString(),
            role: 'user',
            content: text,
            isHistorical: false,
            isComplete: true,
        };

        setMessagesMap((prev) => ({
            ...prev,
            [currentActiveKey]: [...(prev[currentActiveKey] || []), userMessage],
        }));
        setInputValue('');
        setSending(true);

        try {
            let currentMemoryId = memoryIdMap[currentActiveKey];
            if (!currentMemoryId) {
                currentMemoryId = generateMemoryId();
                setMemoryIdMap((prev) => ({...prev, [currentActiveKey]: currentMemoryId}));
            }

            aiMessageIdRef.current = (Date.now() + 1).toString();
            setMessagesMap((prev) => ({
                ...prev,
                [currentActiveKey]: [
                    ...(prev[currentActiveKey] || []),
                    {id: aiMessageIdRef.current, role: 'assistant', content: '', thinking: true, isHistorical: false},
                ],
            }));

            const streamParams: ChatStreamParams = {
                memoryId: currentMemoryId,
                prompt: text,
                knowledgeIds: activeKnowledgeIds
            };
            await streamChat(streamParams, handleChatChunk, controller.signal);

            setMessagesMap((prev) => {
                const msgs = [...(prev[currentActiveKey] || [])];
                const last = msgs[msgs.length - 1];
                if (last?.id === aiMessageIdRef.current) {
                    msgs[msgs.length - 1] = {...last, thinking: false, isComplete: true};
                }
                return {...prev, [currentActiveKey]: msgs};
            });

            await fetchHistoryList();
            setPendingItem(null);

        } catch (err) {
            if ((err as Error).name === 'AbortError') {
                setMessagesMap((prev) => {
                    const msgs = [...(prev[currentActiveKey] || [])];
                    const last = msgs[msgs.length - 1];
                    if (last?.role === 'assistant') {
                        msgs[msgs.length - 1] = {...last, thinking: false, isComplete: true};
                    }
                    return {...prev, [currentActiveKey]: msgs};
                });
            } else {
                console.error('Stream chat failed:', err);
                Message.error('Failed to get AI response.');
                setMessagesMap((prev) => {
                    const msgs = prev[currentActiveKey] || [];
                    return {...prev, [currentActiveKey]: msgs.slice(0, -1)};
                });
            }
        } finally {
            setSending(false);
            abortControllerRef.current = null;
        }
    };

    const handleStop = () => {
        if (abortControllerRef.current) {
            abortControllerRef.current.abort();
            setSending(false);
            Message.info('Stopped generating.');
        }
    };

    const handleRetry = () => {
        const msgs = messagesMap[activeKey] || [];
        const lastUserMsg = [...msgs].reverse().find((m) => m.role === 'user');
        if (lastUserMsg) handleSend(lastUserMsg.content);
    };

    const handleCopy = useCallback((messageId: string) => {
        if (typeof window === 'undefined') return;
        const currentMsgs = messagesMap[activeKey] || [];
        const targetMsg = currentMsgs.find(msg => msg.id === messageId);
        if (!targetMsg) {
            Message.error('未找到可复制的消息内容');
            return;
        }
        const cleanContent = targetMsg.content
            .replace(/```\w+\n/g, '')
            .replace(/```/g, '')
            .trim();
        navigator.clipboard.writeText(cleanContent)
            .then(() => Message.success('复制成功!'))
            .catch(() => Message.error('复制失败!'));
    }, [messagesMap, activeKey]);

    const currentMessages = messagesMap[activeKey] || [];

    const mainVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: { duration: 0.5, ease: [0.19, 1, 0.22, 1], delay: 0.1 }
        }
    };

    return (
        <>
            <Global
                styles={css`
                    :root {
                        @keyframes spin {
                            from { transform: rotate(0deg); }
                            to { transform: rotate(360deg); }
                        }
                    }
                `}
            />
            <div className={styles.container}>
                {/* ======================== 左侧边栏 ======================== */}
                <AnimatePresence mode="wait">
                    {superView === 'chatSystem' && !showDocPanel &&(
                        <motion.div
                            className={styles.sideContainer}
                            initial="collapsed"
                            animate="expanded"
                            exit="collapsed"
                            variants={{
                                expanded: { width: 280, opacity: 1, transition: { duration: 0.4 } },
                                collapsed: { width: 0, opacity: 0, transition: { duration: 0.35 } },
                            }}
                            style={{ overflow: 'hidden' }}
                        >
                            <div className={styles.logoWrapper}>
                                <Image src={logoImage} className={styles.logo} width={56} height={56} alt="logo"/>
                                <span className={styles.logoText}>教学辅助平台</span>
                                {getUserClientRole() === "1" &&
                                    <Tooltip title='进入工作台'>
                                        <Button icon={<AppstoreOutlined />} onClick={openWorkspace} />
                                    </Tooltip>
                                }
                                {getUserClientRole() === "0" &&
                                    <Tooltip title='进入学习台'>
                                        <Button icon={<AppstoreOutlined />} onClick={openWorkspace} />
                                    </Tooltip>
                                }
                            </div>

                            <div className={styles.convContainer}>
                                <div className={styles.convFixed}>
                                    <Conversations
                                        className={styles.convFixedInner}
                                        creation={{ label: '新建对话', onClick: newChatClick }}
                                        shortcutKeys={{ creation: ['Meta', KeyCode.K], items: ['Alt', 'number'] }}
                                        groupable={{
                                            sort: (a, b) => (GROUP_ORDER[a] ?? 99) - (GROUP_ORDER[b] ?? 99),
                                            label: (group) => {
                                                if (group === 'More Features') {
                                                    return (
                                                        <Flex gap="small">
                                                            <CodeSandboxOutlined />
                                                            {group}
                                                        </Flex>
                                                    );
                                                }
                                                return <span>{group}</span>;
                                            },
                                            collapsible: (group) => group !== 'More Features',
                                        }}
                                        items={agentItems.map(item => ({
                                            ...item,
                                            ...(item.key === 'coding' && currentView === 'ppt'
                                                ? { style: { backgroundColor: 'rgba(22, 93, 255, 0.08)', borderRadius: 8 } }
                                                : {}),
                                        }))}
                                        activeKey={currentView === 'ppt' ? 'coding' : ''}
                                        onActiveChange={(key) => {
                                            if (key === 'coding') {
                                                setCurrentView('ppt');
                                                setKnowledgeVisible(false);
                                                setActiveKey('');
                                            } else if (key === 'knowledge') {
                                                setKnowledgeVisible(!knowledgeVisible);
                                            }
                                        }}
                                    />
                                </div>

                                <div className={styles.convScroll}>
                                    {showUseMode()}
                                    <Conversations
                                        className={styles.convScrollInner}
                                        creation={false}
                                        activeKey={currentView === 'chat' ? activeKey : ''}
                                        onActiveChange={handleActiveChange}
                                        shortcutKeys={{ items: ['Alt', 'number'] }}
                                        groupable={{
                                            sort: (a, b) => (GROUP_ORDER[a] ?? 99) - (GROUP_ORDER[b] ?? 99),
                                            label: (group) => <span>{group}</span>,
                                            collapsible: (group) => group !== '今天' && group !== '昨天',
                                        }}
                                        items={pendingItem ? [pendingItem, ...(historicalItems || [])] : historicalItems}
                                    />
                                </div>
                            </div>

                            <div className={styles.userWrapper}>
                                <Dropdown
                                    menu={{ items: menuItems }}
                                    placement="top"
                                    arrow={{ pointAtCenter: true }}
                                >
                                    <div className={styles.userDropdownTrigger}>
                                        {/* 修复：Avatar src 为空警告 */}
                                        <Avatar
                                            src={userInfo?.clientAvator && userInfo.clientAvator !== '' ? userInfo.clientAvator : undefined}
                                            alt="用户头像"
                                            size={28}
                                            icon={<UserOutlined />}
                                        />
                                        <span className={styles.userText}>
                                          {userInfo?.nickname || '未登录用户'}
                                        </span>
                                        {showUserRole()}
                                    </div>
                                </Dropdown>
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>

                {/* ======================== 右侧主区域 ======================== */}
                <motion.div
                    style={{ display: 'flex', flex: 1, position: 'relative', overflow: 'hidden' }}
                    initial="hidden"
                    animate="visible"
                    variants={mainVariants}
                >
                    <AnimatePresence mode="wait">
                        {/* 工作台 */}
                        {superView === 'workspace' && (
                            <motion.div
                                key="workspace"
                                variants={viewVariants}
                                initial="hidden"
                                animate="visible"
                                exit="exit"
                                style={{ width: '100%', height: '100%', background: '#F5F7FA' }}
                            >
                                {userRole === 'teacher' ? (
                                    <Workspace onBack={backToChatSystem} logoImage={logoImage} />
                                ) : (
                                    <StudentWorkspace onBack={backToChatSystem} logoImage={logoImage} studentId={getUserId()} />
                                )}
                            </motion.div>
                        )}

                        {/* 聊天系统（chat + ppt） */}
                        {superView === 'chatSystem' && (
                            <motion.div
                                key="chat-system"
                                variants={viewVariants}
                                initial="hidden"
                                animate="visible"
                                exit="exit"
                                style={{ width: '100%', height: '100%' }}
                            >
                                {/* PPT 视图 */}
                                {currentView === 'ppt' && (
                                    <motion.div
                                        key="ppt-view"
                                        style={{ width: '100%', height: '100%', background: '#F5F7FA' }}
                                    >
                                        <PptGeneratePage />
                                    </motion.div>
                                )}
                                {/*图像生成*/}
                                {currentView === 'image' && (
                                    <motion.div
                                        key="image-view"
                                        style={{ width: '100%', height: '100%', background: '#F5F7FA'}}
                                    >
                                       <ImageGeneratePage/>
                                    </motion.div>
                                )}

                                {/*视频生成*/}
                                {currentView === 'video' && (
                                    <motion.div
                                        key="video-view"
                                        style={{ width: '100%', height: '100%', background: '#F5F7FA'}}
                                    >
                                        <VideoGeneratePage/>
                                    </motion.div>
                                )}

                                {/* 聊天视图 */}
                                {currentView === 'chat' && (
                                    <motion.div
                                        key="chat-view"
                                        style={{ display: 'flex', flex: 1, width: '100%', height: '100%' }}
                                    >
                                        {/* ======== 外层分栏容器 ======== */}
                                        <div style={{ display: 'flex', width: '100%', height: '100%' }}>
                                            {/* 左侧：AI 对话区 */}
                                            <div style={{
                                                flex: showDocPanel ? '0 0 50%' : '1',
                                                transition: 'flex 0.35s ease',
                                                display: 'flex',
                                                position: 'relative',
                                                overflow: 'hidden',
                                            }}>
                                                <div className={styles.rightContain} style={{ flex: 1 }}>
                                                    <div className={styles.messagesScrollContainer} ref={messagesScrollContainerRef} >
                                                        <div className={styles.messagesArea}>
                                                            {currentMessages.length === 0 ? (
                                                                <motion.div initial={{opacity: 0, y: 16, scale: 0.98}} animate={{opacity: 1, y: 0, scale: 1}} transition={{duration: 0.5}} style={{textAlign: 'center', color: '#86868b', paddingTop: 80}} >
                                                                    <Welcome variant="borderless" title="今天我能帮你什么？" description="下方按钮可切换问答模式：通用模式：按需选择对应知识库；专属模式：自动加载该学科全部知识库进行智能问答。" />
                                                                    <HotQueryBubbles onQuestionClick={handleQuestionClick}/>
                                                                </motion.div>
                                                            ) : (
                                                                <AnimatePresence mode="wait">
                                                                    <motion.div key={animContainerKey}>
                                                                        {currentMessages.map((msg, index) => (
                                                                            <motion.div key={msg.id} custom={index} variants={msg.isHistorical ? historicalMsgVariants : newMsgVariants} initial="hidden" animate="visible" style={{ marginBottom: 12, width: '100%', display: 'flex', justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start', }} >
                                                                                <Bubble
                                                                                    placement={msg.role === 'user' ? 'end' : 'start'}
                                                                                    avatar={
                                                                                        <Avatar
                                                                                            icon={msg.role === 'user' ? <UserOutlined/> : <RobotOutlined/>}
                                                                                            style={{
                                                                                                backgroundColor: msg.role === 'user' ? '#1a1a1a' : '#f2f2f7',
                                                                                                color: msg.role === 'user' ? '#fff' : '#5e5e66',
                                                                                                border: msg.role === 'user' ? 'none' : '1px solid #eef0f2',
                                                                                                boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)'
                                                                                            }}
                                                                                        />
                                                                                    }
                                                                                    content={
                                                                                        <MessageContent
                                                                                            content={msg.content}
                                                                                            thinking={msg.thinking}
                                                                                            isHistorical={msg.isHistorical}
                                                                                            isComplete={msg.isComplete ?? false}
                                                                                        />
                                                                                    }
                                                                                    footer={() => (
                                                                                        <Actions
                                                                                            items={msg.role === 'user' ? userActionItems : assistantActionItems}
                                                                                            onClick={({key}) => {
                                                                                                if (key === 'retry' && msg.role === 'user') handleRetry();
                                                                                                else if (key === 'copy') handleCopy(msg.id);
                                                                                            }}
                                                                                        />
                                                                                    )}
                                                                                />
                                                                            </motion.div>
                                                                        ))}
                                                                    </motion.div>
                                                                </AnimatePresence>
                                                            )}
                                                            <div ref={messagesEndRef}/>
                                                        </div>
                                                    </div>
                                                    <div className={styles.senderWrapper}>
                                                        <Sender
                                                            header={headerNode}
                                                            prefix={
                                                                <Button type="text" style={{ fontSize: 16 }} icon={<PaperClipOutlined />} onClick={() => setOpen(!open)} />
                                                            }
                                                            loading={sending}
                                                            value={inputValue}
                                                            onChange={setInputValue}
                                                            onSubmit={() => handleSend(inputValue)}
                                                            onCancel={handleStop}
                                                            placeholder="Message AI智学教学辅助平台..."
                                                            autoSize={{minRows: 1, maxRows: 6}}
                                                            allowSpeech={{ recording, onRecordingChange: handleRecordingChange }}
                                                            // 修复：移除可能导致 components 报错的复杂逻辑，简化 footer
                                                            footer={() => (
                                                               <>
                                                                   <Button size="small" onClick={openWorkspaceModal} color="purple" variant="filled" shape="round" icon={<SwapOutlined />} >
                                                                       模式切换
                                                                   </Button>
                                                                   <Divider orientation="vertical" />
                                                                   <Button
                                                                       icon={<FileImageOutlined />}
                                                                       size='small'
                                                                       color="cyan"
                                                                       variant="filled"
                                                                       shape="round"
                                                                       onClick={() => {
                                                                           setCurrentView('image'); // 切换到图片生成视图
                                                                           setKnowledgeVisible(false);
                                                                           setActiveKey('');
                                                                       }}
                                                                   >
                                                                       图像生成
                                                                   </Button>
                                                                   <Divider orientation="vertical" />
                                                                   <Button size='small'
                                                                           icon={<VideoCameraAddOutlined />}
                                                                           color="default"
                                                                           variant="filled"
                                                                           shape="round"
                                                                           onClick={() => {
                                                                               setCurrentView('video'); // 切换到图片生成视图
                                                                               setKnowledgeVisible(false);
                                                                               setActiveKey('');
                                                                           }}>
                                                                       视频生成
                                                                   </Button>
                                                               </>
                                                            )}
                                                        />
                                                    </div>
                                                </div>

                                                {/* 知识库面板 */}
                                                <AnimatePresence>
                                                    {knowledgeVisible && (
                                                        <motion.div
                                                            initial={{ x: 320, opacity: 0 }}
                                                            animate={{ x: 0, opacity: 1 }}
                                                            exit={{ x: 320, opacity: 0 }}
                                                            transition={{ duration: 0.35 }}
                                                            style={{
                                                                width: '40%',
                                                                background: '#fcfcfd',
                                                                borderLeft: '1px solid #eef0f2',
                                                                padding: '32px 24px',
                                                                height: '100%',
                                                                overflowY: 'auto',
                                                                position: 'absolute',
                                                                right: 0,
                                                                top: 0,
                                                                zIndex: 10,
                                                                boxShadow: '-4px 0 24px rgba(0, 0, 0, 0.04)'
                                                            }}
                                                        >
                                                            <div style={{fontSize: '18px', fontWeight: 600, marginBottom: '24px'}}>知识库</div>
                                                            <Spin spinning={knowledgeLoading}>
                                                                <Collapse defaultActiveKey={knowledgeList.map(i => i.id)} bordered={false}>
                                                                    {knowledgeList
                                                                        .filter(cate => cate.repoDTOS && cate.repoDTOS.length > 0)
                                                                        .map(cate => (
                                                                            <Collapse.Panel
                                                                                key={cate.id}
                                                                                header={
                                                                                    <Flex align="center" gap={8}>
                                                                                        <DatabaseOutlined style={{ color: '#86868b' }} />
                                                                                        <span style={{ fontWeight: 500 }}>{cate.repoCategoryName}</span>
                                                                                    </Flex>
                                                                                }
                                                                                style={{ marginBottom: '8px', borderRadius: '12px' }}
                                                                            >
                                                                                {cate.repoDTOS.map(file => (
                                                                                    <div key={file.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 8px' }}>
                                                                                        <Flex align="center" gap={8}>
                                                                                            <FileTextOutlined style={{ color: '#86868b' }} />
                                                                                            <span style={{ fontSize: '14px' }}>{file.repoTitle}</span>
                                                                                        </Flex>
                                                                                        <Switch
                                                                                            checked={activeKnowledgeIds.includes(file.id)}
                                                                                            onChange={() => toggleKnowledge(file.id)}
                                                                                        />
                                                                                    </div>
                                                                                ))}
                                                                            </Collapse.Panel>
                                                                        ))}
                                                                </Collapse>
                                                            </Spin>
                                                        </motion.div>
                                                    )}
                                                </AnimatePresence>
                                            </div>

                                            {/* ======== 右侧：文档预览区 ======== */}
                                            <AnimatePresence>
                                                {showDocPanel && (
                                                    <motion.div
                                                        key="doc-panel"
                                                        initial={{ width: 0, opacity: 0 }}
                                                        animate={{ width: '50%', opacity: 1, transition: { duration: 0.35 } }}
                                                        exit={{ width: 0, opacity: 0, transition: { duration: 0.25 } }}
                                                        style={{ overflow: 'hidden', height: '100%', flexShrink: 0 }}
                                                    >
                                                        <DocPreviewPanel content={docContent} title="教学文档" onClose={() => setShowDocPanel(false)} />
                                                    </motion.div>
                                                )}
                                            </AnimatePresence>
                                        </div>
                                    </motion.div>
                                )}
                            </motion.div>
                        )}
                    </AnimatePresence>

                    {/* Modal */}
                    <Modal
                        open={workspaceVisible}
                        onCancel={closeWorkspaceModal}
                        footer={null}
                        destroyOnHidden // 修复：替换 destroyOnClose
                        width="70%"
                        style={{
                            top: '50%',
                            transform: 'translateY(-50%)',
                            maxWidth: '100vw',
                        }}
                        // 修复：替换 bodyStyle 和 maskStyle
                        styles={{
                            body: {
                                height: '60vh',
                                overflow: 'auto',
                                padding: 0,
                            },
                            mask: {
                                backdropFilter: 'blur(4px)'
                            }
                        }}
                        centered={false}
                    >
                        <WorkspaceModal
                            onBack={closeWorkspaceModal}
                            logoImage={logoImage}
                            onCourseSelect={handleCourseSelect}
                        />
                    </Modal>
                </motion.div>
            </div>
        </>
    );
};

export default Chat;
export const dynamic = 'force-dynamic';