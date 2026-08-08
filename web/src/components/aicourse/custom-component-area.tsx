'use client'

import { useState } from 'react'
import { Boxes,MessageSquare, Presentation, ImagePlus, Video } from 'lucide-react'
import styles from '@/styles/aicourse/customComponentArea/index.module.css'
import Chat from "@/pages/chat";
import PptGeneratePage from "@/pages/ppt";
import ImageGeneratePage from "@/pages/image";
import VideoGeneratePage from "@/pages/video";

interface CustomComponentAreaProps {
  lessonId: string
  lessonTitle: string
}

// 组件类型定义 - 你可以根据需要扩展
type ComponentType = 'chat' | 'ppt' | 'image' | 'video'

interface TabConfig {
  id: ComponentType
  label: string
  icon: React.ReactNode
}

const tabs: TabConfig[] = [
  { id: 'chat', label: '智能问答', icon: <MessageSquare className={styles.tabIcon} /> },
  { id: 'ppt', label: 'PPT生成', icon:  <Presentation className={styles.tabIcon} /> },
  { id: 'image', label: '图像生成', icon: <ImagePlus className={styles.tabIcon} /> },
  { id: 'video', label: '视频生成', icon: <Video className={styles.tabIcon} /> },
]

export function CustomComponentArea({ lessonId, lessonTitle }: CustomComponentAreaProps) {
  const [activeTab, setActiveTab] = useState<ComponentType>('chat')

  // 渲染不同类型的组件内容
  const renderContent = () => {
    switch (activeTab) {
      case 'chat':
        return <Chat lessonId={lessonId} />
      case 'ppt':
        return <PptGeneratePage lessonId={lessonId} />
      case 'image':
        return <ImageGeneratePage lessonId={lessonId} />
      case 'video':
        return <VideoGeneratePage lessonId={lessonId} lessonTitle={lessonTitle} />
      default:
        return <CustomPlaceholder lessonId={lessonId} lessonTitle={lessonTitle} />
    }
  }

  return (
    <div className={styles.container}>
      {/* Tab Navigation */}
      <div className={styles.tabNavigation}>
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={activeTab === tab.id ? styles.tabButtonActive : styles.tabButtonInactive}
          >
            {tab.icon}
            <span className={styles.tabLabel}>{tab.label}</span>
          </button>
        ))}
      </div>

      {/* Content Area */}
      <div className={styles.contentArea}>
        {renderContent()}
      </div>
    </div>
  )
}

// ==================== 预留的组件占位符 ====================
// 以下组件可以根据实际需求进行替换和扩展

function ChatComponent({ lessonId }: { lessonId: string }) {
  return (
    <div className={styles.chatContainer}>
      {/* Messages Area */}
      <div className={styles.chatMessages}>
        <div className={styles.chatMessagesInner}>
          <div className={styles.chatPlaceholder}>
            <div className={styles.chatPlaceholderIcon}>
              <MessageSquare className={styles.chatPlaceholderIconSvg} />
            </div>
            <p className={styles.chatPlaceholderTitle}>智能问答组件</p>
            <p className={styles.chatPlaceholderText}>在这里集成你的AI聊天组件</p>
            <p className={styles.chatPlaceholderLessonId}>
              课程ID: {lessonId}
            </p>
          </div>
        </div>
      </div>
      
      {/* Input Area Placeholder */}
      <div className={styles.chatInputArea}>
        <div className={styles.chatInputWrapper}>
          <input
            type="text"
            placeholder="输入你的问题..."
            className={styles.chatInput}
            disabled
          />
          <button className={styles.chatSendButton} disabled>
            发送
          </button>
        </div>
      </div>
    </div>
  )
}

function CodeEditorComponent({ lessonId }: { lessonId: string }) {
  return (
    <div className={styles.placeholderContainer}>
      <div className={styles.placeholderContent}>
        <div className={styles.placeholderIcon}>
          <Code className={styles.placeholderIconSvg} />
        </div>
        <p className={styles.placeholderTitle}>代码编辑器组件</p>
        <p className={styles.placeholderText}>在这里集成Monaco Editor或其他代码编辑器</p>
        <p className={styles.placeholderLessonId}>
          课程ID: {lessonId}
        </p>
      </div>
    </div>
  )
}

function PreviewComponent({ lessonId }: { lessonId: string }) {
  return (
    <div className={styles.placeholderContainer}>
      <div className={styles.placeholderContent}>
        <div className={styles.placeholderIcon}>
          <Play className={styles.placeholderIconSvg} />
        </div>
        <p className={styles.placeholderTitle}>预览组件</p>
        <p className={styles.placeholderText}>在这里展示代码运行结果或其他预览内容</p>
        <p className={styles.placeholderLessonId}>
          课程ID: {lessonId}
        </p>
      </div>
    </div>
  )
}

function CustomPlaceholder({ lessonId, lessonTitle }: { lessonId: string; lessonTitle: string }) {
  return (
    <div className={styles.placeholderContainer}>
      <div className={styles.placeholderContent}>
        <div className={styles.placeholderIcon}>
          <Boxes className={styles.placeholderIconSvg} />
        </div>
        <p className={styles.placeholderTitle}>自定义组件区域</p>
        <p className={styles.customPlaceholderText}>
          这是预留的自定义组件区域，你可以根据不同的课程内容放置不同的交互组件。
        </p>
        <div className={styles.lessonInfoCard}>
          <p className={styles.lessonInfoLabel}>当前课程：</p>
          <p className={styles.lessonInfoTitle}>{lessonTitle}</p>
          <p className={styles.lessonInfoId}>ID: {lessonId}</p>
        </div>
        <div className={styles.integrationHint}>
          <p className={styles.integrationHintLabel}>可以在此处集成：</p>
          <div className={styles.integrationTags}>
            <span className={styles.integrationTag}>AI对话组件</span>
            <span className={styles.integrationTag}>代码沙箱</span>
            <span className={styles.integrationTag}>图片生成器</span>
            <span className={styles.integrationTag}>文件上传</span>
          </div>
        </div>
      </div>
    </div>
  )
}
