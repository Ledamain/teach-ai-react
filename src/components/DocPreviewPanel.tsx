'use client';
import React, { useState, useEffect } from 'react';
import { Button, Flex, Typography, Input } from 'antd';
import { CloseOutlined, DownloadOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons';
import {
    Document,
    Packer,
    Paragraph,
    TextRun,
    HeadingLevel,
    Table,
    TableRow,
    TableCell,
    WidthType,
    AlignmentType,
    BorderStyle
} from 'docx';
import { saveAs } from 'file-saver';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import remarkBreaks from 'remark-breaks';
import { unified } from 'unified';
import remarkParse from 'remark-parse';

// 样式保持不变
const markdownCustomStyles = `
.markdown-wrapper {
  line-height: 1.9;
  font-size: 14px;
  color: #333;
}
.markdown-wrapper h1 { 
  font-size: 22px !important; 
  font-weight: 700 !important; 
  margin: 20px 0 12px 0 !important;
  line-height: 1.4 !important;
}
.markdown-wrapper h2 { 
  font-size: 18px !important; 
  font-weight: 700 !important; 
  margin: 18px 0 10px 0 !important;
  line-height: 1.4 !important;
}
.markdown-wrapper h3 { 
  font-size: 16px !important; 
  font-weight: 600 !important; 
  margin: 16px 0 8px 0 !important;
  line-height: 1.4 !important;
}
.markdown-wrapper table {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  overflow-x: auto;
  display: block;
}
.markdown-wrapper table th,
.markdown-wrapper table td {
  border: 1px solid #d9d9d9;
  padding: 10px 14px;
  text-align: left;
  vertical-align: top;
}
.markdown-wrapper table th {
  background-color: #f5f7fa;
  font-weight: 600;
  white-space: nowrap;
}
.markdown-wrapper table tr {
  border-bottom: 1px solid #d9d9d9;
}
.markdown-wrapper p { margin: 6px 0; }
.markdown-wrapper pre { background: #f5f5f5; padding: 12px; border-radius: 6px; overflow-x: auto; margin: 8px 0; }
.markdown-wrapper pre code { background: transparent; padding: 0; font-size: 13px; }
.markdown-wrapper code { background: #f5f5f5; padding: 2px 6px; border-radius: 4px; font-size: 13px; }
.markdown-wrapper ul, .markdown-wrapper ol { padding-left: 24px; margin: 8px 0; }
.markdown-wrapper li { margin-bottom: 4px; }
.markdown-wrapper blockquote { border-left: 4px solid #d9d9d9; padding-left: 16px; margin: 8px 0; color: #666; }
`;

interface DocPreviewPanelProps {
    content: string;
    title?: string;
    onClose?: () => void;
}

const DocPreviewPanel: React.FC<DocPreviewPanelProps> = ({
                                                             content,
                                                             title = '教学文档',
                                                             onClose
                                                         }) => {
    const [editedContent, setEditedContent] = useState('');
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        const rawContent = content || `#高等数学（理工类 I）教学大纲## 一、课程基本信息
课程名称：高等数学（理工类I）
英文名称：Advanced Mathematics forScience and Engineering I
-课程代码：MATH1001
学分/学时：5学分 / 80 学时（理论讲授72学时，习题课与辅导8 学时）
适用专业：计算机科学与技术、电子信息工程、机械工程、自动化、土木工程、物理学、材料科学、航空航天等理工科本科一年级专业
先修课程：高中数学（含函数、三角函数、数列、不等式、解析几何） -后续课程：高等数学 II、线性代数、概率论与数理统计、大学物理、工程数学等
##二、课程目标 通过本课程的学习，学生应能够：
1.系统掌握一元函数微积分的基本概念、理论框架与核心方法；
熟练进行极限、导数、微分、不定积分与定积分的规范计算；
运用微积分思想分析并解决几何、物理及工程中的建模与计算问题；
4.培养严谨的逻辑推理能力、抽象思维能力和初步的数学应用意识； 5.为后续专业课程学习和工程实践奠定坚实的数学基础。
##二、课程安排
| 节次 | 时间 | 课程内容 | 授课方式 | 
| :--- | :--- | :--- | :--- | 
| 第1节 | 08:00 - 08:45 | 高等数学 - 极限引入 | 讲授 | 
| 第2节 | 08:55 - 09:40 | 高等数学 - 极限计算 | 例题演练 | 
| 第3节 | 10:00 - 10:45 | 课堂练习与答疑 | 互动 |

##三、注意事项
表格每一行必须单独占一行
表头与内容之间必须有分隔行`;

        const strippedContent = rawContent
            .replace(/^```[a-zA-Z]*\n?/m, '')
            .replace(/\n?```$/m, '')
            .trim();

        const fixedContent = fixMarkdownFormat(strippedContent);
        console.log('✅ 最终修复后的Markdown：\n', fixedContent);
        setEditedContent(fixedContent);
    }, [content]);

    // Markdown 格式修复函数保持不变
    const fixMarkdownFormat = (text: string): string => {
        if (!text) return '';
        let result = text;

        result = result.replace(/\r\n/g, '\n');
        result = result.replace(/(#+[^#\n]+)(#+)/g, '$1\n$2');
        result = result.replace(/^(#{1,6})([^#\s].*)$/gm, '$1 $2');

        result = result.replace(/(\|)\s*(\|)/g, (match, p1, p2, offset, fullStr) => {
            const preStr = fullStr.slice(0, offset);
            const codeBlockCount = (preStr.match(/```/g) || []).length;
            if (codeBlockCount % 2 !== 0) return match;
            return `${p1}\n${p2}`;
        });

        const lines = result.split('\n');
        const processedLines: string[] = [];
        let inTableBlock = false;
        let tableColumnCount = 0;

        for (let i = 0; i < lines.length; i++) {
            const line = lines[i];
            const trimmedLine = line.trim();

            if (!trimmedLine) {
                if (inTableBlock) inTableBlock = false;
                continue;
            }

            const isInCodeBlock = (processedLines.join('\n').match(/```/g) || []).length % 2 !== 0;
            if (isInCodeBlock) {
                processedLines.push(line);
                continue;
            }

            const isTableLine = trimmedLine.startsWith('|') && trimmedLine.endsWith('|');
            const isTableSeparator = /^\|(\s*[:-]+\s*\|)+$/.test(trimmedLine);
            const isTitleLine = /^#{1,6}\s/.test(trimmedLine);

            if (isTableLine) {
                if (!inTableBlock) {
                    if (processedLines.length > 0) {
                        const lastLine = processedLines[processedLines.length - 1];
                        if (lastLine && !lastLine.startsWith('|') && lastLine !== '') {
                            processedLines.push('');
                        }
                    }
                    tableColumnCount = trimmedLine.split('|').length - 2;
                    inTableBlock = true;
                }

                const currentColumnCount = trimmedLine.split('|').length - 2;
                if (currentColumnCount === tableColumnCount || isTableSeparator) {
                    processedLines.push(trimmedLine);
                } else {
                    inTableBlock = false;
                    processedLines.push('');
                    processedLines.push(trimmedLine);
                }
                continue;
            }

            if (isTitleLine) {
                if (processedLines.length > 0 && processedLines[processedLines.length - 1] !== '') {
                    processedLines.push('');
                }
                processedLines.push(trimmedLine);
                processedLines.push('');
                inTableBlock = false;
                continue;
            }

            if (inTableBlock) {
                processedLines.push('');
                inTableBlock = false;
            }
            processedLines.push(trimmedLine);
        }

        result = processedLines.join('\n').replace(/\n{3,}/g, '\n\n');
        return result.trim();
    };

    // ==========================================================================
    // 🔧 核心重写：Markdown AST 解析器 + DOCX 元素生成器
    // ==========================================================================
    const parseMarkdownToDocx = async (markdown: string) => {
        const file = await unified()
            .use(remarkParse)
            .use(remarkGfm)
            .parse(markdown);

        const children: any[] = [];

        // 递归遍历 AST 节点
        const traverse = (node: any, listLevel: number = 0) => {
            if (!node) return;

            switch (node.type) {
                case 'root':
                    node.children?.forEach((child: any) => traverse(child));
                    break;

                case 'heading':
                    const headingLevel = node.depth;
                    let docxHeadingLevel;
                    switch (headingLevel) {
                        case 1: docxHeadingLevel = HeadingLevel.HEADING_1; break;
                        case 2: docxHeadingLevel = HeadingLevel.HEADING_2; break;
                        case 3: docxHeadingLevel = HeadingLevel.HEADING_3; break;
                        default: docxHeadingLevel = HeadingLevel.HEADING_4;
                    }
                    children.push(
                        new Paragraph({
                            heading: docxHeadingLevel,
                            children: [new TextRun(getInlineText(node))],
                            spacing: { before: 200, after: 100 }
                        })
                    );
                    break;

                case 'paragraph':
                    children.push(
                        new Paragraph({
                            children: [new TextRun(getInlineText(node))],
                            spacing: { before: 100, after: 100 }
                        })
                    );
                    break;

                case 'list':
                    const isOrdered = node.ordered;
                    let start = node.start || 1;
                    node.children?.forEach((listItem: any, index: number) => {
                        const listItemText = getInlineText(listItem);
                        const prefix = isOrdered ? `${start + index}. ` : '• ';
                        children.push(
                            new Paragraph({
                                children: [new TextRun(prefix + listItemText)],
                                indent: { left: 720 * (listLevel + 1) },
                                spacing: { before: 50, after: 50 }
                            })
                        );
                    });
                    break;

                case 'table':
                    const tableRows: TableRow[] = [];
                    node.children?.forEach((row: any, rowIndex: number) => {
                        const tableCells: TableCell[] = [];
                        row.children?.forEach((cell: any) => {
                            tableCells.push(
                                new TableCell({
                                    children: [
                                        new Paragraph({
                                            children: [new TextRun(getInlineText(cell))],
                                            spacing: { before: 50, after: 50 }
                                        })
                                    ],
                                    shading: rowIndex === 0 ? { fill: 'F5F7FA' } : undefined,
                                    borders: {
                                        top: { style: BorderStyle.SINGLE, size: 1, color: 'D9D9D9' },
                                        bottom: { style: BorderStyle.SINGLE, size: 1, color: 'D9D9D9' },
                                        left: { style: BorderStyle.SINGLE, size: 1, color: 'D9D9D9' },
                                        right: { style: BorderStyle.SINGLE, size: 1, color: 'D9D9D9' },
                                    }
                                })
                            );
                        });
                        tableRows.push(new TableRow({ children: tableCells }));
                    });

                    children.push(
                        new Table({
                            rows: tableRows,
                            width: { size: 100, type: WidthType.PERCENTAGE },
                            alignment: AlignmentType.LEFT,
                            spacing: { before: 200, after: 200 }
                        })
                    );
                    break;

                case 'blockquote':
                    children.push(
                        new Paragraph({
                            children: [new TextRun(getInlineText(node))],
                            indent: { left: 720 },
                            shading: { fill: 'F5F5F5' },
                            spacing: { before: 100, after: 100 }
                        })
                    );
                    break;

                case 'code':
                    children.push(
                        new Paragraph({
                            children: [new TextRun(node.value || '')],
                            shading: { fill: 'F5F5F5' },
                            spacing: { before: 100, after: 100 }
                        })
                    );
                    break;
            }
        };

        // 辅助函数：提取内联文本
        const getInlineText = (node: any): string => {
            if (!node.children) return node.value || '';
            return node.children.map((child: any) => {
                if (child.type === 'text') return child.value || '';
                if (child.type === 'inlineCode') return child.value || '';
                if (child.type === 'emphasis') return getInlineText(child);
                if (child.type === 'strong') return getInlineText(child);
                if (child.type === 'link') return getInlineText(child);
                return getInlineText(child);
            }).join('');
        };

        traverse(file);
        return children;
    };

    // ==========================================================================
    // 🔧 重写：下载函数，使用 AST 解析生成 DOCX
    // ==========================================================================
    const handleDownload = async () => {
        try {
            const docChildren = await parseMarkdownToDocx(editedContent);

            const doc = new Document({
                sections: [{
                    properties: {},
                    children: docChildren
                }]
            });

            const blob = await Packer.toBlob(doc);
            saveAs(blob, `${title}.docx`);
        } catch (error) {
            console.error('DOCX 生成失败:', error);
            // 降级方案：如果 AST 解析失败，使用简单方式
            const lines = editedContent.split('\n').filter(Boolean);
            const docChildren = lines.map((line) => {
                if (line.trim().length > 20) {
                    return new Paragraph({ children: [new TextRun(line)] });
                }
                return new Paragraph({
                    heading: HeadingLevel.HEADING_2,
                    children: [new TextRun(line)],
                });
            });
            const doc = new Document({ sections: [{ children: docChildren }] });
            const blob = await Packer.toBlob(doc);
            saveAs(blob, `${title}.docx`);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%', borderLeft: '1px solid #e8e8e8', background: '#fff' }}>
            <style>{markdownCustomStyles}</style>

            <div style={{
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                padding: '12px 20px', borderBottom: '1px solid #e8e8e8', background: '#fafafa',
            }}>
                <Typography.Text strong>{title}</Typography.Text>
                <Flex gap={8}>
                    <Button
                        icon={isEditing ? <EyeOutlined /> : <EditOutlined />}
                        onClick={() => setIsEditing(!isEditing)}
                    >
                        {isEditing ? '预览' : '编辑'}
                    </Button>
                    <Button type="primary" icon={<DownloadOutlined />} onClick={handleDownload}>
                        下载 DOCX
                    </Button>
                    <Button icon={<CloseOutlined />} onClick={onClose} />
                </Flex>
            </div>

            <div style={{ flex: 1, overflow: 'auto', padding: '24px 32px' }}>
                {isEditing ? (
                    <Input.TextArea
                        value={editedContent}
                        onChange={(e) => setEditedContent(e.target.value)}
                        style={{
                            width: '100%',
                            minHeight: '100%',
                            lineHeight: 1.9,
                            fontSize: 14,
                            resize: 'none',
                            borderRadius: 6,
                        }}
                        autoSize={{ minRows: 20 }}
                        placeholder="请编辑文档内容..."
                    />
                ) : (
                    <div className="markdown-wrapper">
                        <ReactMarkdown
                            remarkPlugins={[remarkGfm, remarkBreaks]}
                            components={{
                                h1: ({node, ...props}) => <h1 {...props} />,
                                h2: ({node, ...props}) => <h2 {...props} />,
                                h3: ({node, ...props}) => <h3 {...props} />,
                                table: ({node, ...props}) => <table {...props} />,
                                thead: ({node, ...props}) => <thead {...props} />,
                                tbody: ({node, ...props}) => <tbody {...props} />,
                                tr: ({node, ...props}) => <tr {...props} />,
                                th: ({node, ...props}) => <th {...props} />,
                                td: ({node, ...props}) => <td {...props} />,
                            }}
                        >
                            {editedContent}
                        </ReactMarkdown>
                    </div>
                )}
            </div>
        </div>
    );
};

export default DocPreviewPanel;