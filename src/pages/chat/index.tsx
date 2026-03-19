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
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { atomDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import KeyCode from '@rc-component/util/lib/KeyCode';
import {
    App,
    GetProp,
    message as Message,
    Flex,
    Avatar,
    theme,
    Button,
    Dropdown,
    MenuProps,
    Divider,
    message
} from 'antd';
import {css, Global} from '@emotion/react';
import React, {useState, useRef, useEffect, useCallback} from 'react';
import styles from '@/styles/chat/index.module.css';
import Image from 'next/image';
import logoImage from '@/assets/images/logo.jpg';
import {streamChat} from "@/api/chat";
import {router} from "next/client";
import {ChatStreamParams} from "@/types/chat/ChatType";
import {UserInfo} from "@/types/login/LoginType";

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;

// ========================
// 类型定义
// ========================
type MessageRole = 'user' | 'assistant';

interface ChatMessage {
    id: string;
    role: MessageRole;
    content: string;
    // 标记是否正在思考（AI消息专属）
    thinking?: boolean;
}

// 恢复原有简单的消息映射结构
type MessagesMap = Record<string, ChatMessage[]>;

// 代码块类型定义
interface CodeBlock {
    type: 'text' | 'code';
    content: string;
    lang?: string;
}

// 有效的编程语言列表
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

// ========================
// 配置项
// ========================
const userActionItems = [
    {key: 'retry', icon: <RedoOutlined/>, label: 'Retry'},
    {key: 'copy', icon: <CopyOutlined/>, label: 'Copy'},
];
const assistantActionItems = [
    {key: 'copy', icon: <CopyOutlined/>, label: 'Copy'},
];

const agentItems: GetProp<ConversationsProps, 'items'> = [
    {key: 'write', label: 'Help Me Write', icon: <SignatureOutlined/>},
    {key: 'coding', label: 'AI Coding', icon: <CodeOutlined/>},
    {key: 'createImage', label: 'Create Image', icon: <FileImageOutlined/>},
    {key: 'deepSearch', label: 'Deep Search', icon: <FileSearchOutlined/>},
    {key: 'inDepthResearch', label: 'In-depth research', group: 'More Features'},
    {key: 'vincentFigure', label: 'Vincent Figure', group: 'More Features'},
    {type: 'divider' as const},
];

// ========================
// 工具函数：生成memoryId（增加环境判断）
// ========================
const generateMemoryId = (): string => {
    if (typeof window === 'undefined') {
        return `temp-${Date.now().toString()}`;
    }

    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) {
        throw new Error('用户信息不存在，请先登录');
    }
    const userInfo: UserInfo = JSON.parse(userInfoStr);
    return '用户' + userInfo.id + '-' + Date.now().toString();
};

// ========================
// 消息内容渲染组件（优化版 + 打字机效果 + 代码高亮修复）
// ========================
const MessageContent: React.FC<{ content: string; thinking?: boolean }> = ({
                                                                               content,
                                                                               thinking
                                                                           }) => {
    const [displayedContent, setDisplayedContent] = useState<string>('');
    const [isTyping, setIsTyping] = useState<boolean>(true);

    useEffect(() => {
        if (thinking) {
            setIsTyping(false);
            return;
        }

        if (!content) {
            setDisplayedContent('');
            setIsTyping(false);
            return;
        }

        // 如果内容已经完全显示，不需要打字机效果
        if (displayedContent === content) {
            setIsTyping(false);
            return;
        }

        // 打字机效果：每次增加一个字符
        const timer = setTimeout(() => {
            setDisplayedContent((prev) => {
                const nextContent = content.substring(0, prev.length + 1);
                if (nextContent === content) {
                    setIsTyping(false);
                }
                return nextContent;
            });
        }, 20); // 20ms 的延迟，可根据需要调整速度

        return () => clearTimeout(timer);
    }, [content, thinking, displayedContent]);

    if (thinking) {
        return (
            <Think
                title="BullGPT is thinking..."
                loading={<SyncOutlined style={{ fontSize: 12, animation: 'spin 1s linear infinite' }} />}
                icon={<OpenAIOutlined/>}
            >
                Waiting for response...
            </Think>
        );
    }

    // 智能代码块解析：处理各种格式
    const parts: Array<{ type: 'text' | 'code'; content: string; lang?: string }> = [];
    
    // 匹配 ```语言标识 + 代码内容 + ```
    // 使用非贪婪匹配，支持语言标识后直接跟代码（无换行）
    const codeBlockRegex = /```([a-zA-Z0-9_+-]*)([\s\S]*?)```/g;
    let lastIndex = 0;
    let match;

    while ((match = codeBlockRegex.exec(displayedContent)) !== null) {
        const [fullMatch, langPart, codePart] = match;
        const startIndex = match.index;

        // 添加代码块前的文本
        if (startIndex > lastIndex) {
            const textBefore = displayedContent.substring(lastIndex, startIndex);
            if (textBefore.trim()) {
                parts.push({
                    type: 'text',
                    content: textBefore,
                });
            }
        }

        // 智能提取语言标识和代码内容
        let lang = '';
        let codeContent = codePart;

        // 检查 langPart 是否是有效的语言标识
        if (langPart && VALID_LANGUAGES.has(langPart.toLowerCase())) {
            lang = langPart.toLowerCase();
        } else if (langPart) {
            // langPart 不是有效语言，可能代码直接跟在 ``` 后面
            // 尝试从 codePart 的开头提取语言标识
            const firstLineMatch = codePart.match(/^([a-zA-Z0-9_+-]+)[\s\n]/);
            if (firstLineMatch && VALID_LANGUAGES.has(firstLineMatch[1].toLowerCase())) {
                lang = firstLineMatch[1].toLowerCase();
                codeContent = codePart.substring(firstLineMatch[0].length);
            } else {
                // 如果都不是，把 langPart 也当作代码的一部分
                codeContent = langPart + codePart;
                lang = 'javascript';
            }
        } else {
            lang = 'javascript';
        }

        // 清理代码内容
        const cleanedCode = codeContent.trim();

        // 添加代码块
        parts.push({
            type: 'code',
            content: cleanedCode,
            lang: lang,
        });

        lastIndex = startIndex + fullMatch.length;
    }

    // 添加剩余的文本
    if (lastIndex < displayedContent.length) {
        const textAfter = displayedContent.substring(lastIndex);
        if (textAfter.trim()) {
            parts.push({
                type: 'text',
                content: textAfter,
            });
        }
    }

    // 如果没有找到代码块，整个内容作为文本
    if (parts.length === 0 && displayedContent.trim()) {
        parts.push({
            type: 'text',
            content: displayedContent,
        });
    }

    return (
        <div style={{ lineHeight: '1.8', fontSize: '14px' }}>
            {parts.map((part, index) => {
                if (part.type === 'text') {
                    return (
                        <div key={index} style={{ whiteSpace: 'pre-wrap', marginBottom: '8px' }}>
                            {part.content.split('\n').map((line, lineIdx) => (
                                <p key={lineIdx} style={{ margin: '0 0 8px 0', lineHeight: '1.8' }}>
                                    {line}
                                </p>
                            ))}
                        </div>
                    );
                } else {
                    const lang = part.lang || 'javascript';
                    return (
                        <div key={index} style={{ margin: '12px 0', position: 'relative' }}>
                            <div style={{
                                position: 'absolute',
                                top: '8px',
                                right: '8px',
                                backgroundColor: 'rgba(255, 255, 255, 0.15)',
                                color: '#fff',
                                padding: '4px 8px',
                                borderRadius: '4px',
                                fontSize: '12px',
                                fontWeight: 500,
                                zIndex: 10,
                                textTransform: 'uppercase',
                                letterSpacing: '0.5px'
                            }}>
                                {lang}
                            </div>
                            <SyntaxHighlighter
                                language={lang}
                                style={atomDark}
                                customStyle={{
                                    borderRadius: '8px',
                                    padding: '16px',
                                    fontSize: '13px',
                                    lineHeight: '1.6',
                                    margin: 0,
                                    whiteSpace: 'pre-wrap',
                                    wordWrap: 'break-word',
                                    overflowWrap: 'break-word',
                                    wordBreak: 'break-word'
                                }}
                                wrapLongLines={true}
                                PreTag="div"
                            >
                                {part.content}
                            </SyntaxHighlighter>
                        </div>
                    );
                }
            })}
        </div>
    );
};

// ========================
// 主组件
// ========================
const Chat: React.FC = () => {
    const {token} = theme.useToken();
    const abortControllerRef = useRef<AbortController | null>(null);

    const [activeKey, setActiveKey] = useState<string>('write');
    const [messagesMap, setMessagesMap] = useState<MessagesMap>({write: []});
    const [memoryIdMap, setMemoryIdMap] = useState<Record<string, string>>({
        write: ''
    });
    const [inputValue, setInputValue] = useState<string>('');
    const [sending, setSending] = useState<boolean>(false);
    const [historicalItems, setHistoricalItems] = useState<GetProp<ConversationsProps, 'items'>>([
        {key: `item1`, label: 'Conversation Item 1', group: 'Today'},
    ]);

    const messagesEndRef = useRef<HTMLDivElement>(null);

    const logout = async () => {
        await router.push('/login');
        message.success("登出成功");
    };

    const menuItems: MenuProps['items'] = [
        {
            key: '1',
            label: (
                <div>
                    <div className={styles.userLabel}>
                        <SettingOutlined />
                        <span>个人设置</span>
                    </div>
                    <Divider size='small' />
                </div>
            ),
        },
        {
            key: '2',
            label: (
                <div>
                    <div className={styles.userLabel}>
                        <LogoutOutlined />
                        <span onClick={logout}>退出登录</span>
                    </div>
                    <Divider size='small' />
                </div>
            ),
        },
    ];

    // 自动滚动到底部
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({behavior: 'smooth'});
    };

    useEffect(() => {
        scrollToBottom();
    }, [messagesMap[activeKey]]);

    // 初始化新会话（仅客户端生成memoryId）
    const initConversation = useCallback((key: string) => {
        setMessagesMap((prev) => ({
            ...prev,
            [key]: prev[key] || [],
        }));

        if (typeof window !== 'undefined') {
            setMemoryIdMap((prev) => ({
                ...prev,
                [key]: prev[key] || generateMemoryId()
            }));
        }
    }, []);

    // 组件挂载后初始化默认会话的memoryId
    useEffect(() => {
        if (typeof window !== 'undefined') {
            setMemoryIdMap((prev) => ({
                ...prev,
                write: prev.write || generateMemoryId()
            }));
            initConversation(activeKey);
        }
    }, [activeKey, initConversation]);

    // 切换会话时初始化
    useEffect(() => {
        if (typeof window !== 'undefined') {
            initConversation(activeKey);
        }
    }, [activeKey, initConversation]);

    const items = [...agentItems, ...historicalItems];

    const newChatClick = () => {
        const newKey = `item${Date.now()}`;
        setHistoricalItems((ori) => [
            ...ori,
            {key: newKey, label: `New Chat ${ori.length + 1}`, group: 'Today'},
        ]);
        setActiveKey(newKey);
    };

    // 定义符合ChatChunkCallback类型的回调函数
    const handleChatChunk: ChatChunkCallback = (chunk, isFinal) => {
        if (isFinal) return;

        if (!fullResponseRef.current) {
            fullResponseRef.current = '';
        }
        fullResponseRef.current += chunk;

        setMessagesMap((prev) => {
            const msgs = [...(prev[activeKey] || [])];
            const last = msgs[msgs.length - 1];
            if (last?.id === aiMessageIdRef.current) {
                msgs[msgs.length - 1] = {
                    ...last,
                    content: fullResponseRef.current,
                    thinking: false
                };
            }
            return {...prev, [activeKey]: msgs};
        });
    };

    // 用ref存储fullResponse和aiMessageId，解决闭包问题
    const fullResponseRef = useRef<string>('');
    const aiMessageIdRef = useRef<string>('');

    // 发送消息（用户）
    const handleSend = async (text: string) => {
        if (!text.trim() || sending) return;

        if (typeof window === 'undefined') {
            Message.error('请在浏览器环境中使用');
            return;
        }

        const userInfoStr = window.localStorage.getItem('userInfo');
        if (!userInfoStr) {
            Message.error('用户信息不存在，请先登录');
            router.push('/login');
            return;
        }

        fullResponseRef.current = '';
        aiMessageIdRef.current = '';

        const controller = new AbortController();
        abortControllerRef.current = controller;

        const userMessage: ChatMessage = {
            id: Date.now().toString(),
            role: 'user',
            content: text,
        };

        setMessagesMap((prev) => ({
            ...prev,
            [activeKey]: [...(prev[activeKey] || []), userMessage],
        }));

        setInputValue('');
        setSending(true);

        try {
            let currentMemoryId = memoryIdMap[activeKey];
            if (!currentMemoryId) {
                currentMemoryId = generateMemoryId();
                setMemoryIdMap((prev) => ({
                    ...prev,
                    [activeKey]: currentMemoryId
                }));
            }

            aiMessageIdRef.current = Date.now().toString();

            setMessagesMap((prev) => ({
                ...prev,
                [activeKey]: [
                    ...(prev[activeKey] || []),
                    {
                        id: aiMessageIdRef.current,
                        role: 'assistant',
                        content: '',
                        thinking: true
                    },
                ],
            }));

            const streamParams: ChatStreamParams = {
                memoryId: currentMemoryId,
                prompt: text
            };

            await streamChat(
                streamParams,
                handleChatChunk,
                controller.signal
            );

            setMessagesMap((prev) => {
                const msgs = [...(prev[activeKey] || [])];
                const last = msgs[msgs.length - 1];
                if (last?.id === aiMessageIdRef.current) {
                    msgs[msgs.length - 1] = {...last, thinking: false};
                }
                return {...prev, [activeKey]: msgs};
            });

        } catch (err) {
            if ((err as Error).name === 'AbortError') {
                console.log('Generation was stopped by user');
                setMessagesMap((prev) => {
                    const msgs = [...(prev[activeKey] || [])];
                    const last = msgs[msgs.length - 1];
                    if (last?.role === 'assistant') {
                        msgs[msgs.length - 1] = {...last, thinking: false};
                    }
                    return {...prev, [activeKey]: msgs};
                });
            } else {
                console.error('Stream chat failed:', err);
                Message.error('Failed to get AI response.');
                setMessagesMap((prev) => {
                    const msgs = prev[activeKey] || [];
                    return {...prev, [activeKey]: msgs.slice(0, -1)};
                });
            }
        } finally {
            setSending(false);
            abortControllerRef.current = null;
        }
    };

    // 暂停生成按钮逻辑
    const handleStop = () => {
        if (abortControllerRef.current) {
            abortControllerRef.current.abort();
            setSending(false);
            Message.info('Stopped generating.');
        }
    };

    // 重试当前问题
    const handleRetry = () => {
        const msgs = messagesMap[activeKey] || [];
        const lastUserMsg = [...msgs].reverse().find((m) => m.role === 'user');
        if (lastUserMsg) {
            handleSend(lastUserMsg.content);
        }
    };

    // 优化复制功能，处理代码块内容
    const handleCopy = (content: string) => {
        if (typeof window === 'undefined') return;

        const cleanContent = content
            .replace(/```\w+\n/g, '')
            .replace(/```/g, '')
            .trim();

        navigator.clipboard.writeText(cleanContent).then(() => {
            Message.success('Copied to clipboard!');
        }).catch(() => {
            Message.error('Failed to copy content');
        });
    };

    const currentMessages = messagesMap[activeKey] || [];

    return (
        <>
            <Global
                styles={css`
                    :root {
                        @keyframes spin {
                            from {
                                transform: rotate(0deg);
                            }
                            to {
                                transform: rotate(360deg);
                            }
                        }
                    }
                `}
            />

            <div className={styles.container}>
                <div className={styles.sideContainer}>
                    <div className={styles.logoWrapper}>
                        <Image src={logoImage} className={styles.logo} width={60} height={30} alt="logo"/>
                        <span className={styles.logoText}>BullGPT</span>
                    </div>
                    <Conversations
                        className={styles.convFullHeight}
                        creation={{onClick: newChatClick}}
                        activeKey={activeKey}
                        onActiveChange={(key) => setActiveKey(key)}
                        shortcutKeys={{
                            creation: ['Meta', KeyCode.K],
                            items: ['Alt', 'number'],
                        }}
                        groupable={{
                            label: (group) => {
                                return group !== 'Today' ? (
                                    <Flex gap="small">
                                        <CodeSandboxOutlined/>
                                        {group}
                                    </Flex>
                                ) : (
                                    group
                                );
                            },
                            collapsible: (group) => group !== 'Today',
                        }}
                        items={items}
                    />
                    <div className={styles.userWrapper}>
                        <Dropdown
                            menu={{items: menuItems}}
                            placement="top"
                            arrow={{pointAtCenter: true}}
                        >
                            <div style={{display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer'}}>
                                <Avatar icon={<UserOutlined />} />
                                <span className={styles.userText}>用户12123</span>
                            </div>
                        </Dropdown>
                    </div>
                </div>

                <div className={styles.rightContain}>
                    <div className={styles.messagesScrollContainer}>
                        <div className={styles.messagesArea}>
                            {currentMessages.length === 0 ? (
                                <div style={{ textAlign: 'center', color: token.colorTextSecondary, paddingTop: 40 }}>
                                    <Welcome
                                        variant="borderless"
                                        icon="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp"
                                        title="Hello, I'm BullGPT"
                                        description="Base on Ant Design, AGI product interface solution, create a better intelligent vision~"
                                    />
                                </div>
                            ) : (
                                currentMessages.map((msg) => (
                                    <div key={msg.id} style={{marginBottom: 12}}>
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
                                            content={<MessageContent content={msg.content} thinking={msg.thinking} />}
                                            footer={(content) => (
                                                <Actions
                                                    items={msg.role === 'user' ? userActionItems : assistantActionItems}
                                                    onClick={({key}) => {
                                                        if (key === 'retry' && msg.role === 'user') {
                                                            handleRetry();
                                                        } else if (key === 'copy') {
                                                            handleCopy(content);
                                                        }
                                                    }}
                                                />
                                            )}
                                        />
                                    </div>
                                ))
                            )}
                            <div ref={messagesEndRef}/>
                        </div>
                    </div>

                    <div className={styles.senderWrapper}>
                        <Sender
                            loading={sending}
                            value={inputValue}
                            onChange={setInputValue}
                            onSubmit={() => handleSend(inputValue)}
                            onCancel={handleStop}
                            placeholder="Message BullGPT..."
                            autoSize={{minRows: 1, maxRows: 6}}
                        />
                    </div>
                </div>
            </div>
        </>
    );
};

export default Chat;
export const dynamic = 'force-static'; // 关闭服务端渲染