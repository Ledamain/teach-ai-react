'use client'

import { useState, useEffect } from 'react'
import { Download, ZoomIn, ZoomOut, Loader2, AlertCircle } from 'lucide-react'
import styles from '@/styles/aicourse/documentViewer/index.module.css'

interface DocumentViewerProps {
  lessonId: string
}

interface DocumentData {
  content: string
  title: string
  lastModified?: string
}

// API接口预留 - 你可以根据实际后端实现修改这个函数
async function fetchDocumentContent(lessonId: string): Promise<DocumentData> {
  // TODO: 替换为实际的API调用
  // 示例：const response = await fetch(`/api/documents/${lessonId}`)
  // return response.json()
  
  // 模拟API响应 - 实际使用时请替换为真实的API调用
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        title: `课程文档 - ${lessonId}`,
        content: getPlaceholderContent(lessonId),
        lastModified: new Date().toISOString(),
      })
    }, 500)
  })
}

function getPlaceholderContent(lessonId: string): string {
  const contentMap: Record<string, string> = {
    'lesson-1-1': `
      <h1>通用模式下利用对应知识库实现点对点问答</h1>
      
      <h2>1. 学习目标</h2>
      <p>通过本节课的学习，您将掌握如何在通用模式下使用AI知识库进行高效的问答交互。</p>
      
      <h2>2. 知识要点</h2>
      <ul>
        <li>了解通用模式的基本概念和应用场景</li>
        <li>掌握知识库的构建方法</li>
        <li>学会使用点对点问答技术</li>
        <li>理解AI问答系统的工作原理</li>
      </ul>
      
      <h2>3. 实践步骤</h2>
      <p>首先，我们需要准备相关的知识库数据...</p>
      <p>接下来，配置AI模型的参数...</p>
      <p>最后，测试问答系统的响应效果...</p>
      
      <h2>4. 注意事项</h2>
      <p>在使用过程中，请注意以下几点：</p>
      <ol>
        <li>确保知识库数据的准确性和完整性</li>
        <li>合理设置问答系统的响应阈值</li>
        <li>定期更新和维护知识库内容</li>
      </ol>
      
      <h2>5. 课后练习</h2>
      <p>请尝试构建一个简单的知识库，并测试其问答效果。</p>
    `,
    'lesson-1-2': `
      <h1>专属模式下利用专属知识库实现面对面问答</h1>
      
      <h2>1. 学习目标</h2>
      <p>掌握专属模式的配置方法，学会创建和管理专属知识库。</p>
      
      <h2>2. 专属模式介绍</h2>
      <p>专属模式是一种定制化的AI交互方式，允许用户创建私有的知识库...</p>
      
      <h2>3. 知识库构建</h2>
      <p>构建专属知识库需要以下步骤：</p>
      <ul>
        <li>收集和整理相关文档</li>
        <li>进行数据预处理</li>
        <li>导入知识库系统</li>
        <li>配置检索参数</li>
      </ul>
    `,
  }
  
  return contentMap[lessonId] || `
    <h1>课程文档</h1>
    <p>该课程的详细文档内容将在这里显示。</p>
    <p>请通过API接口加载实际的Word文档内容。</p>
    
    <h2>文档加载说明</h2>
    <p>您可以通过以下方式加载Word文档：</p>
    <ol>
      <li>调用后端API获取文档内容</li>
      <li>使用文档解析库转换Word格式</li>
      <li>将解析后的HTML内容渲染到此处</li>
    </ol>
    
    <h2>API接口示例</h2>
    <pre>GET /api/documents/{lessonId}</pre>
    <p>返回格式：</p>
    <pre>
{
  "title": "文档标题",
  "content": "HTML格式的文档内容",
  "lastModified": "2024-01-01T00:00:00Z"
}
    </pre>
  `
}

export function DocumentViewer({ lessonId }: DocumentViewerProps) {
  const [document, setDocument] = useState<DocumentData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [zoom, setZoom] = useState(100)

  useEffect(() => {
    async function loadDocument() {
      setLoading(true)
      setError(null)
      
      try {
        const data = await fetchDocumentContent(lessonId)
        setDocument(data)
      } catch (err) {
        setError('文档加载失败，请稍后重试')
        console.error('Failed to load document:', err)
      } finally {
        setLoading(false)
      }
    }
    
    loadDocument()
  }, [lessonId])

  const handleZoomIn = () => {
    setZoom((prev) => Math.min(prev + 10, 150))
  }

  const handleZoomOut = () => {
    setZoom((prev) => Math.max(prev - 10, 70))
  }

  const handleDownload = () => {
    // TODO: 实现文档下载功能
    // 可以调用API下载原始Word文档
    console.log('Download document:', lessonId)
  }

  if (loading) {
    return (
      <div className={styles.loadingWrapper}>
        <div className={styles.loadingContent}>
          <Loader2 className={styles.loadingIcon} />
          <span className={styles.loadingText}>加载文档中...</span>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className={styles.errorWrapper}>
        <div className={styles.errorContent}>
          <AlertCircle className={styles.errorIcon} />
          <span className={styles.errorText}>{error}</span>
          <button 
            onClick={() => window.location.reload()}
            className={styles.retryButton}
          >
            点击重试
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className={styles.container}>
      {/* Toolbar */}
      <div className={styles.toolbar}>
        <div className={styles.zoomControls}>
          <button
            onClick={handleZoomOut}
            className={styles.zoomButton}
            title="缩小"
          >
            <ZoomOut className={styles.zoomButtonIcon} />
          </button>
          <span className={styles.zoomValue}>
            {zoom}%
          </span>
          <button
            onClick={handleZoomIn}
            className={styles.zoomButton}
            title="放大"
          >
            <ZoomIn className={styles.zoomButtonIcon} />
          </button>
        </div>
        
        <button
          onClick={handleDownload}
          className={styles.downloadButton}
          title="下载文档"
        >
          <Download className={styles.downloadButtonIcon} />
          <span>下载</span>
        </button>
      </div>

      {/* Document Content */}
      <div className={styles.documentContent}>
        <div 
          className={styles.documentContentInner}
          style={{ fontSize: `${zoom}%` }}
          dangerouslySetInnerHTML={{ 
            __html: document?.content || '' 
          }}
        />
      </div>
    </div>
  )
}
