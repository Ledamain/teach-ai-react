'use client';
import {
    CodeOutlined,
    CodeSandboxOutlined,
    FileImageOutlined,
    FileSearchOutlined,
    SignatureOutlined,
    CopyOutlined,
    RedoOutlined,
    UserOutlined,
    RobotOutlined,
    OpenAIOutlined,
    SyncOutlined, LogoutOutlined, SettingOutlined,
    CloudUploadOutlined, PaperClipOutlined, UploadOutlined,
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
    Typography, UploadProps, Upload
} from 'antd';
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
import {$URL} from "ufo";

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;

type MessageRole = 'user' | 'assistant';

interface ChatMessage {
    id: string;
    role: MessageRole;
    content: string;
    thinking?: boolean;
    isHistorical?: boolean;
}

interface HistoryListItem {
    memoryId: string;
    messageTitle: string;
    messagesJson: string;
    createTime: number;
    updateTime: number;
}

type MessagesMap = Record<string, ChatMessage[]>;

const VALID_LANGUAGES = new Set([
    'javascript', 'js', 'typescript', 'ts', 'python', 'py', 'java', 'c', 'cpp', 'csharp', 'cs',
    'php', 'ruby', 'go', 'rust', 'kotlin', 'swift', 'objective-c', 'objc', 'scala', 'groovy',
    'html', 'xml', 'css', 'scss', 'sass', 'less', 'json', 'yaml', 'yml', 'toml', 'ini', 'cfg',
    'sql', 'mysql', 'postgresql', 'mongodb', 'bash', 'shell', 'sh', 'zsh', 'fish', 'powershell',
    'r', 'matlab', 'lua', 'perl', 'haskell', 'clojure', 'elixir', 'erlang', 'lisp', 'scheme',
    'dart', 'flutter', 'vim', 'diff', 'patch', 'makefile', 'dockerfile', 'gradle', 'maven',
    'cmake', 'nginx', 'apache', 'regex', 'markdown', 'md', 'latex', 'tex', 'asciidoc', 'adoc',
    'plaintext', 'text', 'log', 'properties', 'gradle', 'xml', 'svg', 'graphql', 'gql'
]);

const userActionItems = [
    {key: 'retry', icon: <RedoOutlined/>, label: 'Retry'},
    {key: 'copy', icon: <CopyOutlined/>, label: 'Copy'},
];
const assistantActionItems = [
    {key: 'copy', icon: <CopyOutlined/>, label: 'Copy'},
];

const agentItems: GetProp<ConversationsProps, 'items'> = [
    {key: 'coding', label: 'PPT 生成', icon: <CodeOutlined/>},
    {key: 'createImage', label: 'Create Image', icon: <FileImageOutlined/>},
    // {key: 'deepSearch', label: 'Deep Search', icon: <FileSearchOutlined/>},
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

// ========================
// 消息内容渲染组件
// ========================
const MessageContent: React.FC<{
    content: string;
    thinking?: boolean;
    isHistorical?: boolean;
}> = ({ content, thinking, isHistorical }) => {
    const [displayedContent, setDisplayedContent] = useState<string>(
        isHistorical ? content : ''
    );
    const [isTyping, setIsTyping] = useState<boolean>(!isHistorical);

    useEffect(() => {
        if (isHistorical) {
            setDisplayedContent(content);
            setIsTyping(false);
            return;
        }
        if (thinking) {
            setIsTyping(false);
            return;
        }
        if (!content) {
            setDisplayedContent('');
            setIsTyping(false);
            return;
        }
        if (displayedContent === content) {
            setIsTyping(false);
            return;
        }
        const timer = setTimeout(() => {
            setDisplayedContent((prev) => {
                const next = content.substring(0, prev.length + 1);
                if (next === content) setIsTyping(false);
                return next;
            });
        }, 10);
        return () => clearTimeout(timer);
    }, [content, thinking, displayedContent, isHistorical]);

    if (thinking) {
        return (
            <Think
                title="AI智学教学辅助平台 is thinking..."
                loading={<SyncOutlined spin style={{ fontSize: 12 }} />}
                icon={<OpenAIOutlined />}
            >
                Waiting for response...
            </Think>
        );
    }

    // 只匹配标准格式：```lang\ncode\n```
    const codeBlockRegex = /```([\w#+]*)\n([\s\S]*?)```/gm;
    const parts: Array<{ type: 'text' | 'code'; content: string; lang?: string }> = [];
    let lastIndex = 0;
    let match;

    while ((match = codeBlockRegex.exec(displayedContent)) !== null) {
        const [full, langRaw, code] = match;
        const start = match.index;

        // 前面的文本
        if (start > lastIndex) {
            const t = displayedContent.slice(lastIndex, start);
            if (t.trim()) parts.push({ type: 'text', content: t });
        }

        // 语言标准化
        const lang = langRaw?.toLowerCase().trim() || 'plaintext';

        parts.push({
            type: 'code',
            content: code,
            lang: VALID_LANGUAGES.has(lang) ? lang : 'plaintext',
        });

        lastIndex = start + full.length;
    }

    // 最后一段文本
    if (lastIndex < displayedContent.length) {
        const t = displayedContent.slice(lastIndex);
        if (t.trim()) parts.push({ type: 'text', content: t });
    }

    return (
        <div style={{ lineHeight: 1.8, fontSize: 14, whiteSpace: 'pre-wrap' }}>
            {parts.map((part, i) =>
                part.type === 'text' ? (
                    <div key={i} style={{ marginBottom: 8 }}>
                        {part.content}
                    </div>
                ) : (
                    <div key={i} style={{ margin: '12px 0', position: 'relative' }}>
                        <div
                            style={{
                                position: 'absolute',
                                top: 6,
                                right: 10,
                                background: 'rgba(0,0,0,0.3)',
                                color: '#fff',
                                padding: '2px 6px',
                                borderRadius: 4,
                                fontSize: 12,
                                zIndex: 2,
                            }}
                        >
                            {part.lang}
                        </div>
                        <SyntaxHighlighter
                            language={part.lang}
                            style={atomDark}
                            wrapLongLines
                            customStyle={{
                                borderRadius: 8,
                                padding: 16,
                                fontSize: 13,
                                margin: 0,
                                whiteSpace: 'pre-wrap',
                                wordBreak: 'break-word',
                            }}
                        >
                            {part.content}
                        </SyntaxHighlighter>
                    </div>
                )
            )}
        </div>
    );
};

// ========================
// 历史消息动画变体
// ========================
const historicalMsgVariants = {
    hidden: {opacity: 0, y: -28, scale: 0.97},
    visible: (i: number) => ({
        opacity: 1,
        y: 0,
        scale: 1,
        transition: {
            delay: i * 0.07,
            duration: 0.45,
            ease: [0.22, 1, 0.36, 1],
        },
    }),
};

const newMsgVariants = {
    hidden: {opacity: 0, y: 16},
    visible: {
        opacity: 1,
        y: 0,
        transition: {duration: 0.3, ease: 'easeOut'},
    },
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

    const messagesEndRef = useRef<HTMLDivElement>(null);
    // ★ 新增：直接引用滚动容器，用于精确控制 scrollTop
    const messagesScrollContainerRef = useRef<HTMLDivElement>(null);
    // ★ 新增：记录当前是否需要在历史消息动画结束后执行滚动
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

    const uploadProps: UploadProps = useMemo(() => ({
        name: 'file',
        action: `${process.env.NEXT_PUBLIC_API_URL_LOC}/admin-api/infra/file/upload`,
        headers: {
            fileflag: `authorization-${encodeURIComponent(currentMemoryId)}`,
        },
        // ★ 新增：将受控组件状态绑定到属性
        fileList,
        onChange(info) {
            // ★ 新增：无论状态怎样改变，都同步更新组件的 fileList 状态
            setFileList(info.fileList);

            if (info.file.status !== 'uploading') {
                console.log(info.file, info.fileList);
            }
            if (info.file.status === 'done') {
                message.success(`${info.file.name} 文件上传成功`);
            } else if (info.file.status === 'error') {
                message.error(`${info.file.name} 文件上传失败`);
            }
        },
    }), [currentMemoryId, fileList]);

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

                {/* ★ 修改：传入 fileList，Ant Design 会自动展示上传的文件名列表 */}
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

    const fetchHistoryList = useCallback(async () => {
        const userId = getUserId();
        if (!userId) return;
        try {
            const res = await getHistoryList(userId);
            let list: HistoryListItem[] | null = null;

            if (Array.isArray(res)) {
                list = res;
            } else if (Array.isArray(res?.data)) {
                list = res.data;
            } else if (Array.isArray(res?.data?.data)) {
                list = res.data.data;
            }

            if (!list) {
                console.warn('[fetchHistoryList] 无法解析列表，res 结构：', res);
                return;
            }

            const items: GetProp<ConversationsProps, 'items'> = list.map((item: HistoryListItem) => ({
                key: item.memoryId,
                label: item.messageTitle || item.memoryId,
                group: getTimeGroup(item.createTime),
            }));

            setHistoricalItems(items);
            setHistoryKeySet(new Set(list.map((item: HistoryListItem) => item.memoryId)));
        } catch (e) {
            console.error('[fetchHistoryList] 请求失败:', e);
        }
    }, []);

    useEffect(() => {
        if (mounted) {
            fetchHistoryList();

            // ★ 新增：如果当前没有激活的任务，且没有历史记录被选中，则自动创建一个“待定”的新对话
            if (!activeKey) {
                const newKey = `new-${Date.now()}`;
                const newMemoryId = generateMemoryId(); // 确保你有这个函数

                setMemoryIdMap(prev => ({ ...prev, [newKey]: newMemoryId }));
                setMessagesMap(prev => ({ ...prev, [newKey]: [] }));
                setActiveKey(newKey);
                setAnimContainerKey(newKey);

                // 设置左侧侧边栏的临时显示项
                setPendingItem({
                    key: newKey,
                    label: '新对话',
                    group: '今天'
                });
            }
        }
    }, [mounted]); // 注意：只在 mounted 变为 true 时执行一次

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

    // ========================
    // ★ 统一的滚动到底部函数
    // 优先直接操控滚动容器的 scrollTop，更可靠
    // ========================
    const scrollToBottom = useCallback((behavior: ScrollBehavior = 'smooth') => {
        // 方案1：直接设置滚动容器的 scrollTop（最可靠）
        if (messagesScrollContainerRef.current) {
            const container = messagesScrollContainerRef.current;
            container.scrollTo({top: container.scrollHeight, behavior});
        } else {
            // 方案2：降级到 scrollIntoView
            messagesEndRef.current?.scrollIntoView({behavior});
        }
    }, []);

    // 新消息到来时自动滚到底（非历史会话）
    useEffect(() => {
        if (historyKeySet.has(activeKey)) return;
        scrollToBottom();
    }, [messagesMap[activeKey]]); // eslint-disable-line react-hooks/exhaustive-deps

    // ========================
    // 切换会话
    // ========================
    const handleActiveChange = useCallback(async (key: string) => {
        // 清理上一次还未触发的历史滚动定时器
        if (pendingHistoryScrollRef.current) {
            clearTimeout(pendingHistoryScrollRef.current);
            pendingHistoryScrollRef.current = null;
        }

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
                    console.warn('[getHistory] 无法解析响应结构:', res);
                    Message.error('历史消息格式异常');
                    return;
                }

                const rawMessages: Array<{
                    id: string; role: string; content: string; thinking: boolean;
                }> = data.messages ?? [];

                if (rawMessages.length > 0) {
                    const msgs: ChatMessage[] = rawMessages.map((m) => ({
                        id: m.id,
                        role: (m.role === 'user' ? 'user' : 'assistant') as MessageRole,
                        content: m.content,
                        thinking: false,
                        isHistorical: true,
                    }));

                    setMessagesMap(prev => ({...prev, [key]: msgs}));
                    setMemoryIdMap(prev => ({...prev, [key]: key}));

                    // ★ 修复核心：
                    // AnimatePresence mode="wait" 会先执行退出动画（~200ms），
                    // 再挂载新内容并执行入场动画。
                    // 所以等待时间 = 退出动画缓冲(250ms) + 入场动画总时长 + 余量(150ms)
                    // 入场动画最后一条: (msgs.length - 1) * 70ms delay + 450ms duration
                    const exitBuffer = 250;
                    const lastMsgAnimEnd = (msgs.length - 1) * 70 + 450;
                    const totalWait = exitBuffer + lastMsgAnimEnd + 150;

                    pendingHistoryScrollRef.current = setTimeout(() => {
                        // ★ 用 requestAnimationFrame 确保浏览器已完成本轮绘制
                        requestAnimationFrame(() => {
                            scrollToBottom('smooth');
                        });
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

    // 组件卸载时清理定时器
    useEffect(() => {
        return () => {
            if (pendingHistoryScrollRef.current) {
                clearTimeout(pendingHistoryScrollRef.current);
            }
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
                <div>
                    <div className={styles.userLabel}>
                        <SettingOutlined/>
                        <span onClick={inSettings}>个人设置</span>
                    </div>
                    <Divider size='small'/>
                </div>
            ),
        },
        {
            key: '2',
            label: (
                <div>
                    <div className={styles.userLabel}>
                        <LogoutOutlined/>
                        <span onClick={logout}>退出登录</span>
                    </div>
                    <Divider size='small'/>
                </div>
            ),
        },
    ];

    // ========================
    // 新建会话
    // ========================
    const newChatClick = () => {
        if (activeKey && !historyKeySet.has(activeKey)) {
            const currentMsgs = messagesMap[activeKey] || [];
            if (currentMsgs.length === 0) {
                message.warning('请先在当前对话中发起一次提问，再新建对话');
                return;
            }
        }
        const newKey = `new-${Date.now()}`;
        const newMemoryId = generateMemoryId();
        setMemoryIdMap(prev => ({...prev, [newKey]: newMemoryId}));
        setMessagesMap(prev => ({...prev, [newKey]: []}));
        setActiveKey(newKey);
        setAnimContainerKey(newKey);
        setPendingItem({key: newKey, label: '新对话', group: '今天'});

        // ★ 新增：清空上传文件列表状态
        setFileList([]);
    };

    // ========================
    // 流式回调
    // ========================
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
    }, []);

    // ========================
    // 发送消息
    // ========================
    const handleSend = async (text: string) => {
        if (!text.trim() || sending) return;

        // ★ 新增：检查文件上传状态拦截
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

            const streamParams: ChatStreamParams = {memoryId: currentMemoryId, prompt: text};
            await streamChat(streamParams, handleChatChunk, controller.signal);

            setMessagesMap((prev) => {
                const msgs = [...(prev[currentActiveKey] || [])];
                const last = msgs[msgs.length - 1];
                if (last?.id === aiMessageIdRef.current) {
                    msgs[msgs.length - 1] = {...last, thinking: false};
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
                        msgs[msgs.length - 1] = {...last, thinking: false};
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

        // 从当前会话中查找对应消息
        const currentMsgs = messagesMap[activeKey] || [];
        const targetMsg = currentMsgs.find(msg => msg.id === messageId);

        if (!targetMsg) {
            Message.error('未找到可复制的消息内容');
            return;
        }

        // 清理代码块标记
        const cleanContent = targetMsg.content
            .replace(/```\w+\n/g, '')
            .replace(/```/g, '')
            .trim();

        navigator.clipboard.writeText(cleanContent)
            .then(() => Message.success('Copied to clipboard!'))
            .catch(() => Message.error('Failed to copy content'));
    }, [messagesMap, activeKey]);

    const currentMessages = messagesMap[activeKey] || [];

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
                <div className={styles.sideContainer}>
                    <div className={styles.logoWrapper}>
                        <Image src={logoImage} className={styles.logo} width={75} height={75} alt="logo"/>
                        <span className={styles.logoText}>教学辅助平台</span>
                    </div>

                    {/* 拆分会话列表为固定区域和滚动区域 */}
                    <div className={styles.convContainer}>
                        {/* 固定的功能项区域 */}
                        <div className={styles.convFixed}>
                            <Conversations
                                className={styles.convFixedInner}
                                creation={{
                                    label: '新建对话',
                                    onClick: newChatClick
                                }}
                                shortcutKeys={{
                                    creation: ['Meta', KeyCode.K],
                                    items: ['Alt', 'number'],
                                }}
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
                                items={agentItems}
                                activeKey=""
                                onActiveChange={() => {}}
                            />
                        </div>

                        {/* 可滚动的历史会话区域 */}
                        <div className={styles.convScroll}>
                            <Conversations
                                className={styles.convScrollInner}
                                creation={false} // 隐藏 New chat 按钮
                                activeKey={activeKey}
                                onActiveChange={handleActiveChange}
                                shortcutKeys={{
                                    items: ['Alt', 'number'],
                                }}
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
                            {/* 整个区域包裹，让点击范围全覆盖 */}
                            <div className={styles.userDropdownTrigger}>
                                <Avatar
                                    src={userInfo?.clientAvator || ''}
                                    alt="用户头像"
                                    size={28}
                                    fallback={<UserOutlined />}
                                />
                                <span className={styles.userText}>
        {userInfo?.nickname || '未登录用户'}
      </span>
                            </div>
                        </Dropdown>
                    </div>
                </div>

                <div className={styles.rightContain}>
                    {/* ★ 加上 ref，用于精确控制滚动位置 */}
                    <div
                        className={styles.messagesScrollContainer}
                        ref={messagesScrollContainerRef}
                    >
                        <div className={styles.messagesArea}>
                            {currentMessages.length === 0 ? (
                                <motion.div
                                    initial={{opacity: 0, y: 20}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{duration: 0.5, ease: 'easeOut'}}
                                    style={{textAlign: 'center', color: token.colorTextSecondary, paddingTop: 40}}
                                >
                                    <Welcome
                                        variant="borderless"
                                        title="Hello, I'm AI智学教学辅助平台"
                                        description="Base on Ant Design, AGI product interface solution, create a better intelligent vision~"
                                    />
                                </motion.div>
                            ) : (
                                <AnimatePresence mode="wait">
                                    <motion.div key={animContainerKey}>
                                        {currentMessages.map((msg, index) => (
                                            <motion.div
                                                key={msg.id}
                                                custom={index}
                                                variants={msg.isHistorical ? historicalMsgVariants : newMsgVariants}
                                                initial="hidden"
                                                animate="visible"
                                                style={{marginBottom: 12}}
                                            >
                                                <Bubble
                                                    placement={msg.role === 'user' ? 'end' : 'start'}
                                                    avatar={
                                                        <Avatar
                                                            icon={msg.role === 'user' ? <UserOutlined/> : <RobotOutlined/>}
                                                            style={{
                                                                backgroundColor: msg.role === 'user' ? token.colorPrimary : '#f0f0f0',
                                                                color: msg.role === 'user' ? '#fff' : undefined,
                                                            }}
                                                        />
                                                    }
                                                    content={
                                                        <MessageContent
                                                            content={msg.content}
                                                            thinking={msg.thinking}
                                                            isHistorical={msg.isHistorical}
                                                        />
                                                    }
                                                    footer={() => ( // 移除 content 参数
                                                        <Actions
                                                            items={msg.role === 'user' ? userActionItems : assistantActionItems}
                                                            onClick={({key}) => {
                                                                if (key === 'retry' && msg.role === 'user') handleRetry();
                                                                else if (key === 'copy') handleCopy(msg.id); // 传递消息ID
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
                                <Button
                                    type="text"
                                    style={{ fontSize: 16 }}
                                    icon={<PaperClipOutlined />}
                                    onClick={() => {
                                        setOpen(!open);
                                    }}
                                />
                            }
                            loading={sending}
                            value={inputValue}
                            onChange={setInputValue}
                            onSubmit={() => handleSend(inputValue)}
                            onCancel={handleStop}
                            placeholder="Message AI智学教学辅助平台..."
                            autoSize={{minRows: 1, maxRows: 6}}
                            allowSpeech={{
                                recording,
                                onRecordingChange: handleRecordingChange,
                            }}
                        />
                    </div>
                </div>
            </div>
        </>
    );
};

export default Chat;
export const dynamic = 'force-dynamic';