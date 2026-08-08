// ========================
// 消息内容渲染组件（修改版）
// ========================
import React, { useEffect, useRef, useState, useMemo, useCallback } from "react";
import { Think } from "@ant-design/x";
import {
    CheckOutlined,
    CodeOutlined,
    CopyOutlined,
    OpenAIOutlined,
    PlayCircleOutlined,
    SyncOutlined,
    BulbOutlined,
    BookOutlined,
    EditOutlined,
    FullscreenOutlined
} from "@ant-design/icons";
import { Button, Progress } from "antd";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vs } from "react-syntax-highlighter/dist/esm/styles/prism";
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

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

// 辅助函数：分离语言标识和代码
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

export const MessageContent: React.FC<{
    content: string;
    thinking?: boolean;
    isHistorical?: boolean;
    isComplete?: boolean;
}> = ({ content, thinking, isHistorical, isComplete = false }) => {
    const [viewModes, setViewModes] = useState<Record<number, 'code' | 'preview'>>({});
    const [copiedIndex, setCopiedIndex] = useState<number | null>(null);
    const [previewSnapshots, setPreviewSnapshots] = useState<Record<number, string>>({});

    // 【优化】将解析后的 parts 存入 state，避免重复计算且保证引用稳定
    const [parts, setParts] = useState<Array<{ type: 'text' | 'code'; content: string; lang: string }>>([]);

    // 新增：思考状态的本地逻辑
    const [currentStageIndex, setCurrentStageIndex] = useState(0);
    const timerRef = useRef<NodeJS.Timeout | null>(null);

    const THINKING_STEPS = [
        { icon: <BulbOutlined />, text: "正在分析题目意图..." },
        { icon: <BookOutlined />, text: "正在检索教学知识库..." },
        { icon: <SyncOutlined spin />, text: "正在梳理解题逻辑..." },
        { icon: <EditOutlined />, text: "正在生成最终解答..." },
    ];

// 控制显示到第几步
    const [showSteps, setShowSteps] = useState(0);

// 清理定时器
    const clearTimer = () => {
        if (timerRef.current) {
            clearInterval(timerRef.current);
            timerRef.current = null;
        }
    };

// 自动依次播放思考步骤，到达最后一步后保持不动
    useEffect(() => {
        if (isHistorical) return;

        // 不思考 → 清空步骤
        if (!thinking) {
            setShowSteps(0);
            clearTimer();
            return;
        }

        // 开始思考 → 从头播放
        setShowSteps(0);
        clearTimer();

        timerRef.current = setInterval(() => {
            setShowSteps((prev) => {
                // 已经到最后一步 → 保持不动
                if (prev >= THINKING_STEPS.length) {
                    return prev;
                }
                // 否则继续下一步
                return prev + 1;
            });
        }, 1200);

        return () => clearTimer();
    }, [thinking, isHistorical]);

    // 【优化】核心逻辑：每当 content 变化时，重新解析 parts
    useEffect(() => {
        const fixedContent = content
            .replace(/\\n/g, '\n')
            .replace(/\\r/g, '\r')
            .replace(/\r\n/g, '\n')
            .replace(/\r/g, '\n');

        const codeBlockRegex = /```([a-zA-Z#]+)([\s\S]*?)```/gm;
        const newParts: Array<{ type: 'text' | 'code'; content: string; lang: string }> = [];
        let lastIndex = 0;
        let match;

        while ((match = codeBlockRegex.exec(fixedContent)) !== null) {
            const [full, rawLang, rawCode] = match;
            const start = match.index;

            if (start > lastIndex) {
                const t = fixedContent.slice(lastIndex, start);
                if (t.trim()) newParts.push({ type: 'text', content: t, lang: 'plaintext' });
            }

            const { lang, code } = splitLanguageAndCode(rawLang, rawCode);
            newParts.push({
                type: 'code',
                content: code,
                lang: VALID_LANGUAGES.has(lang) ? lang : 'plaintext'
            });
            lastIndex = start + full.length;
        }

        if (lastIndex < fixedContent.length) {
            const t = fixedContent.slice(lastIndex);
            if (t.trim()) newParts.push({ type: 'text', content: t, lang: 'plaintext' });
        }

        setParts(newParts);
    }, [content]);

    // 【优化】核心逻辑：自动同步快照
    // 当用户切换到预览模式，或者代码内容更新且当前处于预览模式时，自动更新快照
    useEffect(() => {
        Object.entries(viewModes).forEach(([indexStr, mode]) => {
            const index = Number(indexStr);
            if (mode === 'preview' && parts[index]) {
                const currentCode = parts[index].content;
                setPreviewSnapshots(prev => {
                    if (prev[index] !== currentCode) {
                        return { ...prev, [index]: currentCode };
                    }
                    return prev;
                });
            }
        });
    }, [viewModes, parts]); // 依赖 viewModes 和 parts

    // 【简化】切换视图，不再需要传递 htmlContent
    const toggleViewMode = useCallback((index: number) => {
        setViewModes(prev => {
            const current = prev[index] || 'code';
            return { ...prev, [index]: current === 'preview' ? 'code' : 'preview' };
        });
    }, []);

    const handleCopyCode = (text: string, index: number) => {
        navigator.clipboard.writeText(text).then(() => {
            setCopiedIndex(index);
            setTimeout(() => setCopiedIndex(null), 1000);
        });
    };

    const handleFullscreen = (index: number) => {
        const htmlContent = previewSnapshots[index] || '';
        const newWindow = window.open('', '_blank');
        if (newWindow) {
            newWindow.document.write(htmlContent);
            newWindow.document.close();
        }
    };

    // --- 【修改】渲染思考状态：仅当 thinking 为 true 且未完成所有步骤时展示 ---
    if (!isHistorical && thinking) {
        return (
            <Think
                title="智汇伴学教学辅助平台 is thinking..."
                loading={<SyncOutlined spin style={{ fontSize: 12 }} />}
                icon={<OpenAIOutlined />}
            >
                <div style={{ marginTop: 8, display: "flex", flexDirection: "column", gap: 6 }}>
                    {THINKING_STEPS.map((step, idx) => {
                        const isActive = showSteps > idx;
                        return (
                            <div
                                key={idx}
                                style={{
                                    display: "flex",
                                    alignItems: "center",
                                    gap: 8,
                                    fontSize: 13,
                                    color: isActive ? "#165DFF" : "#999",
                                    whiteSpace: "nowrap",
                                    transition: "all 0.3s ease",
                                    opacity: isActive ? 1 : 0.3,
                                }}
                            >
                                {step.icon}
                                {step.text}
                            </div>
                        );
                    })}
                </div>
            </Think>
        );
    }


    // --- 渲染主内容 ---
    return (
        <div style={{ lineHeight: 1.8, fontSize: 14 }}>
            {parts.map((part, i) => {
                if (part.type === 'text') {
                    return (
                        <div key={i} style={{ marginBottom: 8 }}>
                            <ReactMarkdown
                                remarkPlugins={[remarkGfm]}
                                components={{
                                    // 代码行内样式
                                    code({ node, className, children, ...props }) {
                                        return (
                                            <code
                                                style={{
                                                    background: '#f0f0f0',
                                                    padding: '1px 3px',
                                                    borderRadius: 4,
                                                    fontSize: 13,
                                                    fontFamily: 'Consolas, Monaco, monospace'
                                                }}
                                                {...props}
                                            >
                                                {children}
                                            </code>
                                        );
                                    },
                                    // 表格样式
                                    table({ children }) {
                                        return (
                                            <table style={{
                                                borderCollapse: 'collapse',
                                                width: '100%',
                                                margin: '8px 0'
                                            }}>
                                                {children}
                                            </table>
                                        );
                                    },
                                    td({ children }) {
                                        return (
                                            <td style={{ border: '1px solid #d9d9d9', padding: '6px 12px' }}>
                                                {children}
                                            </td>
                                        );
                                    },
                                    th({ children }) {
                                        return (
                                            <th style={{
                                                border: '1px solid #d9d9d9',
                                                padding: '3px 6px',
                                                background: '#fafafa'
                                            }}>
                                                {children}
                                            </th>
                                        );
                                    },
                                }}
                            >
                                {part.content}
                            </ReactMarkdown>
                        </div>
                    );
                }

                // 代码未完成时的简单渲染
                if (!isComplete && !isHistorical) {
                    return (
                        <pre key={i} style={{
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
                                <Button
                                    size="small"
                                    type="primary"
                                    ghost={currentMode === 'preview'}
                                    icon={currentMode === 'code' ? <PlayCircleOutlined /> : <CodeOutlined />}
                                    onClick={() => toggleViewMode(i)}
                                    disabled={!isComplete}
                                >
                                    {currentMode === 'code' ? '运行预览' : '查看代码'}
                                </Button>
                            )}
                            <Button
                                size="small"
                                type="text"
                                icon={copiedIndex === i ? <CheckOutlined style={{ color: '#52c11a' }} /> : <CopyOutlined style={{ color: '#bfbfbf' }} />}
                                onClick={() => handleCopyCode(part.content, i)}
                            />
                            <div style={{ background: 'rgba(0,0,0,0.3)', color: '#fff', padding: '2px 6px', borderRadius: 4, fontSize: 12 }}>{part.lang}</div>
                        </div>

                        {isHtml && currentMode === 'preview' ? (
                            <div style={{ border: '1px solid #d9d9d9', borderRadius: 8, background: '#fff', overflow: 'hidden' }}>
                                <div style={{
                                    background: '#f5f5f5',
                                    padding: '4px 8px',
                                    fontSize: 12,
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    borderBottom: '1px solid #e8e8e8'
                                }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                        <Button
                                            size="small"
                                            type="text"
                                            icon={<FullscreenOutlined />}
                                            onClick={() => handleFullscreen(i)}
                                            style={{ height: '22px', padding: '0 4px' }}
                                        >
                                            全屏
                                        </Button>
                                        <span style={{ color: '#666' }}>预览窗口</span>
                                    </div>
                                </div>
                                {/* 【增强】sandbox 增加 allow-same-origin，确保复杂页面正常运行 */}
                                <iframe
                                    key={`preview-${i}-${previewSnapshots[i]?.length ?? 0}`}
                                    srcDoc={previewSnapshots[i] || ''}
                                    style={{ width: '100%', minHeight: 500, border: 0 }}
                                    sandbox="allow-scripts allow-same-origin allow-popups"
                                    title={`HTML Preview ${i}`}
                                    onLoad={(e) => {
                                        const iframe = e.target as HTMLIFrameElement;
                                        const doc = iframe.contentDocument;
                                        if (!doc) return;

                                        const scripts = doc.querySelectorAll('script');
                                        scripts.forEach((oldScript) => {
                                            const newScript = doc.createElement('script');
                                            Array.from(oldScript.attributes).forEach((attr) => {
                                                newScript.setAttribute(attr.name, attr.value);
                                            });
                                            newScript.textContent = oldScript.textContent;
                                            oldScript.parentNode?.replaceChild(newScript, oldScript);
                                        });
                                    }}
                                />
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
                                  style={vs}
                                  wrapLines={false}
                                  wrapLongLines={true}
                                  customStyle={{
                                      margin: 0,
                                      padding: '16px',
                                      fontSize: 13,
                                      whiteSpace: 'pre-wrap',
                                      wordBreak: 'break-all',
                                      borderRadius: 8,
                                      maxWidth: '100%',
                                      overflowX: 'hidden'
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