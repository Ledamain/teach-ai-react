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
    PlayCircleOutlined,CheckOutlined,
    DatabaseOutlined,
    FileTextOutlined,
    VerticalLeftOutlined,
    VerticalRightOutlined,
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
    Collapse, Switch, Spin
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
import {getKnowledgeList} from "@/api/repo";

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;

type MessageRole = 'user' | 'assistant';

interface ChatMessage {
    id: string;
    role: MessageRole;
    content: string;
    thinking?: boolean;
    isHistorical?: boolean;
    isComplete?: boolean;
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
    isComplete?: boolean;
}> = ({ content, thinking, isHistorical, isComplete = false }) => {
    const [viewModes, setViewModes] = useState<Record<number, 'code' | 'preview'>>({});
    const [copiedIndex, setCopiedIndex] = useState<number | null>(null);

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

    const fixedContent = content
        .replace(/\\n/g, '\n')
        .replace(/\\r/g, '\r')
        .replace(/\r\n/g, '\n')
        .replace(/\r/g, '\n');

    const codeBlockRegex = /```([a-zA-Z#]+)([\s\S]*?)```/gm;
    const parts: Array<{ type: 'text' | 'code'; content: string; lang: string }> = [];
    let lastIndex = 0;
    let match;

    while ((match = codeBlockRegex.exec(fixedContent)) !== null) {
        const [full, rawLang, rawCode] = match;
        const start = match.index;

        if (start > lastIndex) {
            const t = fixedContent.slice(lastIndex, start);
            if (t.trim()) parts.push({ type: 'text', content: t, lang: 'plaintext' });
        }

        const { lang, code } = splitLanguageAndCode(rawLang, rawCode);
        parts.push({
            type: 'code',
            content: code,
            lang: VALID_LANGUAGES.has(lang) ? lang : 'plaintext'
        });
        lastIndex = start + full.length;
    }

    if (lastIndex < fixedContent.length) {
        const t = fixedContent.slice(lastIndex);
        if (t.trim()) parts.push({ type: 'text', content: t, lang: 'plaintext' });
    }

    function splitLanguageAndCode(rawLang: string, rawCode: string) {
        const langList = ['java', 'python', 'js', 'javascript', 'html', 'css', 'cpp', 'c', 'sql', 'go', 'php'];
        const lowerLang = rawLang.toLowerCase();
        for (const lang of langList) {
            if (lowerLang.startsWith(lang)) {
                const rest = rawLang.slice(lang.length);
                return { lang, code: rest + rawCode };
            }
        }
        return { lang: 'plaintext', code: rawLang + rawCode };
    }

    const toggleViewMode = (index: number) => {
        setViewModes(prev => ({ ...prev, [index]: prev[index] === 'preview' ? 'code' : 'preview' }));
    };

    const handleCopyCode = (text: string, index: number) => {
        navigator.clipboard.writeText(text).then(() => {
            setCopiedIndex(index);
            setTimeout(() => setCopiedIndex(null), 1000);
        });
    };

    return (
        <div style={{ lineHeight: 1.8, fontSize: 14 }}>
            {parts.map((part, i) => {
                if (part.type === 'text') {
                    return <div key={i} style={{ marginBottom: 8, whiteSpace: 'pre-wrap' }}>{part.content}</div>;
                }

                if (!isComplete && !isHistorical) {
                    return (
                        <pre style={{
                            background: '#1e1e2f',
                            color: '#e0e0f0',
                            borderRadius: 8,
                            padding: 16,
                            margin: '12px 0',
                            whiteSpace: 'pre-wrap',
                            wordBreak: 'break-all',
                            fontFamily: 'Consolas, Monaco, monospace'
                        }}>
                            {part.content}
                        </pre>
                    );
                }

                const isHtml = part.lang === 'html';
                const currentMode = viewModes[i] || 'code';

                return (
                    <div key={i} style={{ margin: '12px 0', position: 'relative' }}>
                        <div style={{ position: 'absolute', top: 6, right: 10, display: 'flex', gap: 8, zIndex: 2 }}>
                            {isHtml && (
                                <Button size="small" type="primary" ghost={currentMode === 'preview'}
                                        icon={currentMode === 'code' ? <PlayCircleOutlined /> : <CodeOutlined />}
                                        onClick={() => toggleViewMode(i)}>
                                    {currentMode === 'code' ? '运行预览' : '查看代码'}
                                </Button>
                            )}
                            <Button size="small" type="text"
                                    icon={copiedIndex === i ? <CheckOutlined style={{ color: '#52c11a' }} /> : <CopyOutlined style={{ color: '#bfbfbf' }} />}
                                    onClick={() => handleCopyCode(part.content, i)} />
                            <div style={{ background: 'rgba(0,0,0,0.3)', color: '#fff', padding: '2px 6px', borderRadius: 4, fontSize: 12 }}>{part.lang}</div>
                        </div>

                        {isHtml && currentMode === 'preview' ? (
                            <div style={{ border: '1px solid #d9d9d9', borderRadius: 8, background: '#fff', overflow: 'hidden' }}>
                                <div style={{ background: '#f5f5f5', padding: '4px 12px', fontSize: 12 }}>预览窗口</div>
                                <iframe srcDoc={part.content} style={{ width: '100%', minHeight: 500, border: 0 }} sandbox="allow-scripts" />
                            </div>
                        ) : (
                            <pre style={{
                                margin: 0,
                                padding: 0,
                                whiteSpace: 'pre-wrap',
                                wordBreak: 'break-all'
                            }}>
                              <SyntaxHighlighter
                                  language={part.lang}
                                  style={atomDark}
                                  wrapLines={false}
                                  wrapLongLines={true}
                                  customStyle={{
                                      margin: 0,
                                      padding: '16px',
                                      fontSize: 13,
                                      whiteSpace: 'pre-wrap',
                                      wordBreak: 'break-all',
                                      borderRadius: 8
                                  }}
                              >
                                {part.content}
                              </SyntaxHighlighter>
                            </pre>
                        )}
                    </div>
                );
            })}
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

    // ========================
    // 知识库状态（预留接口）
    // ========================
    const [knowledgeVisible, setKnowledgeVisible] = useState(false);
    const [knowledgeList, setKnowledgeList] = useState<KnowledgeCategory[]>([]);
    const [activeKnowledgeIds, setActiveKnowledgeIds] = useState<string[]>([]);
    const [knowledgeLoading, setKnowledgeLoading] = useState(false);

    const uploadProps: UploadProps = useMemo(() => ({
        name: 'file',
        action: `${process.env.NEXT_PUBLIC_API_URL_LOC}/admin-api/infra/file/upload`,
        headers: {
            fileflag: `authorization-${encodeURIComponent(currentMemoryId)}`,
        },
        fileList,
        onChange(info) {
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

    const agentItems: GetProp<ConversationsProps, 'items'> = [
        { key: 'coding', label: 'PPT 生成', icon: <CodeOutlined /> },
        {
            key: 'knowledge',
            label: (
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    width: '100%'
                }}>
                    知识库
                    {knowledgeVisible ? (
                        <VerticalRightOutlined />
                    ) : (
                        <VerticalLeftOutlined />
                    )}
                </div>
            ),
            icon: <DatabaseOutlined />,
        },
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

    // ========================
    // 获取知识库列表接口
    // ========================
    const fetchKnowledgeList = useCallback(async () => {
        setKnowledgeLoading(true);
        try {

            // 模拟数据
                const res = await getKnowledgeList();
                setKnowledgeList(res);
                setKnowledgeLoading(false);
        } catch (err) {
            message.error('获取知识库失败');
            setKnowledgeLoading(false);
        }
    }, []);

    // ========================
    // 切换知识库启用状态
    // ========================
    const toggleKnowledge = (fileId: string) => {
        setActiveKnowledgeIds(prev =>
            prev.includes(fileId)
                ? prev.filter(id => id !== fileId)
                : [...prev, fileId]
        );
        // 预留接口：可在这里调用接口保存选中状态
        // await axios.post('/api/knowledge/toggle', { fileId });
    };

    // 打开知识库面板时加载数据
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

            if (!activeKey) {
                const newKey = `new-${Date.now()}`;
                const newMemoryId = generateMemoryId();

                setMemoryIdMap(prev => ({ ...prev, [newKey]: newMemoryId }));
                setMessagesMap(prev => ({ ...prev, [newKey]: [] }));
                setActiveKey(newKey);
                setAnimContainerKey(newKey);

                setPendingItem({
                    key: newKey,
                    label: '新对话',
                    group: '今天'
                });
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

    const handleActiveChange = useCallback(async (key: string) => {
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
                        isComplete: true,
                    }));

                    setMessagesMap(prev => ({...prev, [key]: msgs}));
                    setMemoryIdMap(prev => ({...prev, [key]: key}));

                    const exitBuffer = 250;
                    const lastMsgAnimEnd = (msgs.length - 1) * 70 + 450;
                    const totalWait = exitBuffer + lastMsgAnimEnd + 150;

                    pendingHistoryScrollRef.current = setTimeout(() => {
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

            // 发送时携带选中的知识库ID
            const streamParams: ChatStreamParams = {
                memoryId: currentMemoryId,
                prompt: text,
                knowledgeIds: activeKnowledgeIds // 已预留
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

                    <div className={styles.convContainer}>
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
                                onActiveChange={(key) => {
                                    if (key === 'knowledge') {
                                        setKnowledgeVisible(!knowledgeVisible);
                                    }
                                }}
                            />
                        </div>

                        <div className={styles.convScroll}>
                            <Conversations
                                className={styles.convScrollInner}
                                creation={false}
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

                {/* 右侧主区域 + 知识库侧边栏 */}
                <div style={{ display: 'flex', width: '100%', position: 'relative' }}>
                    <div className={styles.rightContain} style={{ flex: 1 }}>
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

                    {/* 知识库弹出面板 */}
                    <AnimatePresence>
                        {knowledgeVisible && (
                            <motion.div
                                initial={{ x: 300, opacity: 0 }}
                                animate={{ x: 0, opacity: 1 }}
                                exit={{ x: 300, opacity: 0 }}
                                transition={{ duration: 0.3, ease: 'easeInOut' }}
                                style={{
                                    width: '30%',
                                    background: '#fff',
                                    borderLeft: '1px solid #e5e7eb',
                                    padding: '16px',
                                    height: '100vh',
                                    overflowY: 'auto',
                                    position: 'absolute',
                                    right: 0,
                                    top: 0,
                                    zIndex: 10
                                }}
                            >
                                <div style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '16px' }}>
                                    知识库
                                </div>

                                <Spin spinning={knowledgeLoading}>
                                    <Collapse
                                        defaultActiveKey={knowledgeList.map(i => i.id)}
                                        bordered={false}
                                    >
                                        {knowledgeList.map(cate => (
                                            <Collapse.Panel
                                                key={cate.id}
                                                header={<Flex align="center" gap={4}><DatabaseOutlined />{cate.repoCategoryName}</Flex>}
                                            >
                                                <div style={{ paddingLeft: '8px' }}>
                                                    {cate.repoDTOS.map(file => (
                                                        <div
                                                            key={file.id}
                                                            style={{
                                                                display: 'flex',
                                                                justifyContent: 'space-between',
                                                                alignItems: 'center',
                                                                padding: '8px 0'
                                                            }}
                                                        >
                                                            <Flex align="center" gap={6}>
                                                                <FileTextOutlined style={{ color: '#666' }} />
                                                                <span style={{ fontSize: '14px' }}>{file.repoTitle}</span>
                                                            </Flex>
                                                            <Switch
                                                                checked={activeKnowledgeIds.includes(file.id)}
                                                                onChange={() => toggleKnowledge(file.id)}
                                                                checkedChildren="已启用"
                                                                unCheckedChildren="启用"
                                                            />
                                                        </div>
                                                    ))}
                                                </div>
                                            </Collapse.Panel>
                                        ))}
                                    </Collapse>
                                </Spin>

                                {knowledgeList.length === 0 && !knowledgeLoading && (
                                    <div style={{ textAlign: 'center', color: '#999', padding: '20px' }}>
                                        暂无知识库
                                    </div>
                                )}
                            </motion.div>
                        )}
                    </AnimatePresence>
                </div>
            </div>
        </>
    );
};

export default Chat;
export const dynamic = 'force-dynamic';