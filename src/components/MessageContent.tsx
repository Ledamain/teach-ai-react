
// ========================
// 消息内容渲染组件
// ========================
import React, {useState} from "react";
import {Think} from "@ant-design/x";
import {
    CheckOutlined,
    CodeOutlined,
    CopyOutlined,
    OpenAIOutlined,
    PlayCircleOutlined,
    SyncOutlined
} from "@ant-design/icons";
import {Button} from "antd";
import {atomDark} from "react-syntax-highlighter/dist/esm/styles/prism";
import { oneLight } from "react-syntax-highlighter/dist/esm/styles/prism";
import { vs } from "react-syntax-highlighter/dist/esm/styles/prism";
import {Prism as SyntaxHighlighter} from 'react-syntax-highlighter';


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

export const MessageContent: React.FC<{
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