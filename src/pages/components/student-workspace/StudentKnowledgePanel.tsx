'use client';

import React, { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {Modal, Empty, Table, Tooltip, Button, message, Switch, Popconfirm} from 'antd';
import {
  FolderOutlined,
  FileTextOutlined,
  FileWordOutlined,
  FilePptOutlined,
  FileMarkdownOutlined,
  DownloadOutlined,
  CloseOutlined, DeleteOutlined,
} from '@ant-design/icons';
import KnowLedgeApi from '@/api/repo/index'
import {
  KnowledgeFile,
  KnowledgeFolder,
  KnowledgeFolderDetail,
  KnowLedgeFolderParams
} from "@/types/repo/RepoType";
import styles from '@/styles/studentWorkspace/index.module.css';

interface StudentKnowledgePanelProps {
  courseId: number;
  studentId: string;
}

// 根据文件类型获取图标
const getFileIcon = (type: string) => {
  switch (type.toLowerCase()) {
    case 'doc':
    case 'docx':
      return <FileWordOutlined style={{ color: '#2b579a', fontSize: 20 }} />;
    case 'ppt':
    case 'pptx':
      return <FilePptOutlined style={{ color: '#d24726', fontSize: 20 }} />;
    case 'md':
      return <FileMarkdownOutlined style={{ color: '#1a1a1a', fontSize: 20 }} />;
    case 'txt':
    default:
      return <FileTextOutlined style={{ color: '#86868b', fontSize: 20 }} />;
  }
};

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
};

const StudentKnowledgePanel: React.FC<StudentKnowledgePanelProps> = ({ courseId, studentId }) => {
  const [folders, setFolders] = useState<KnowledgeFolder[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentFolder, setCurrentFolder] = useState<KnowledgeFolder | null>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [folderDetail, setFolderDetail] = useState<KnowledgeFolderDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewUrl, setPreviewUrl] = useState('');

  // 从文件名提取后缀
  const getFileExtension = (filename: string) => {
    if (!filename) return '';
    return filename.split('.').pop()?.toLowerCase() || '';
  };

  const fetchFolders = useCallback(async () => {
    try {
      setLoading(true);
      const result = await KnowLedgeApi.getKnowledgeFolders(courseId);
      setFolders(result);
    } catch (error) {
      console.error('获取知识库列表失败:', error);
      message.error('获取知识库列表失败');
    } finally {
      setLoading(false);
    }
  }, [courseId, studentId]);

  useEffect(() => {
    fetchFolders();
  }, [fetchFolders]);

  // 点击文件夹卡片，打开详情弹窗
  const handleFolderClick = async (folder: KnowledgeFolder) => {
    setCurrentFolder(folder);
    setDetailLoading(true);
    setDetailModalVisible(true);

    try {
      const detail = await KnowLedgeApi.getKnowledgeFolderDetail(courseId, folder.id);
      setFolderDetail(detail);
    } catch (error) {
      console.error('获取文件夹详情失败:', error);
      message.error('获取文件夹详情失败');
    } finally {
      setDetailLoading(false);
    }
  };

  // 下载文件
  const handleDownload = (file: KnowledgeFile) => {
    // 模拟下载
    message.info(`正在下载: ${file.repoTitle}`);
    // 实际实现：window.open(file.url, '_blank');
  };

  // 文件列表表格列配置（学生只读，只能下载）
  const fileColumns = [
    {
      title: '文件名',
      dataIndex: 'repoTitle',
      key: 'repoTitle',
      render: (name: string, record: KnowledgeFile) => (
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            {getFileIcon(record.type || record.repoFile?.split('.').pop())}
            <span style={{ color: '#1a1a1a', fontWeight: 500 }}>{name}</span>
          </div>
      ),
    },
    {
      title: '类型',
      dataIndex: 'repoTitle',
      key: 'type',
      width: 80,
      render: (name: string) => {
        const ext = getFileExtension(name);
        return <span style={{ color: '#86868b', textTransform: 'uppercase' }}>{ext}</span>;
      },
    },
    {
      title: '预览',
      key: 'preview',
      width: 100,
      render: (_: any, record: KnowledgeFile) => (
          <Button
              type="link"
              style={{ padding: 0 }}
              onClick={(e) => {
                e.stopPropagation();
                const url = `https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(record.repoFile)}`;
                setPreviewUrl(url);
                setPreviewVisible(true);
              }}
          >
            在线预览
          </Button>
      ),
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160,
      render: (time: string) => (
          <span style={{ color: '#86868b' }}>{time}</span>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_: unknown, record: KnowledgeFile) => (
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>

            <Tooltip title="下载">
              <Button
                  icon={<DownloadOutlined />}
                  size="small"
                  type="link"
                  style={{ padding: 0, color: '#1a1a1a' }}
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (record.repoFile) {
                      window.open(record.repoFile, '_blank');
                    } else {
                      message.warning('暂无预览地址');
                    }
                  }}
              />
            </Tooltip>
          </div>
      ),
    },
  ];

  return (
    <div className={styles.knowledgeContainer}>
      {/* 工具栏 */}
      <motion.div
        className={styles.knowledgeToolbar}
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <div className={styles.knowledgeTitle}>
          <FolderOutlined style={{ marginRight: 8 }} />
          知识库文件夹
        </div>
        <div className={styles.folderCount}>{folders.length} 个文件夹</div>
      </motion.div>

      {/* 文件夹列表 */}
      {loading ? (
        <div className={styles.loadingContainer}>加载中...</div>
      ) : folders.length === 0 ? (
        <Empty
          description="暂无可访问的知识库"
          image={<FileTextOutlined style={{ fontSize: 64, color: '#d9d9d9' }} />}
        />
      ) : (
        <div className={styles.folderGrid}>
          <AnimatePresence>
            {folders.map((folder, index) => (
              <motion.div
                key={folder.id}
                className={styles.folderCard}
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.9 }}
                transition={{ duration: 0.2, delay: index * 0.05 }}
                layout
                onClick={() => handleFolderClick(folder)}
              >
                <div className={styles.folderHeader}>
                  <div className={styles.folderIcon}>
                    <FolderOutlined />
                  </div>
                  <div className={styles.folderInfo}>
                    <div className={styles.folderName}>{folder.repoGroupName}</div>
                    <div className={styles.folderMeta}>
                      {folder.fileCount} 个文件 · 更新于 {folder.updateTime}
                    </div>
                  </div>
                </div>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      )}

      {/* 文件夹详情弹窗 */}
      <Modal
        title={null}
        open={detailModalVisible}
        onCancel={() => {
          setDetailModalVisible(false);
          setFolderDetail(null);
          setCurrentFolder(null);
        }}
        footer={null}
        width={800}
        centered
        closeIcon={<CloseOutlined />}
      >
        {detailLoading ? (
          <div style={{ textAlign: 'center', padding: 60 }}>加载中...</div>
        ) : folderDetail ? (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25 }}
          >
            {/* 头部信息 */}
            <div
              style={{
                display: 'flex',
                alignItems: 'flex-start',
                justifyContent: 'space-between',
                marginBottom: 24,
                paddingBottom: 20,
                borderBottom: '1px solid #eef0f2',
              }}
            >
              <div>
                <h2
                  style={{
                    fontSize: 20,
                    fontWeight: 600,
                    color: '#1a1a1a',
                    margin: 0,
                    marginBottom: 8,
                  }}
                >
                  <FolderOutlined style={{ marginRight: 10, color: '#5e5e66' }} />
                  {folderDetail.repoGroupName}
                </h2>
                {folderDetail.repoGroupDescription && (
                  <p
                    style={{
                      fontSize: 14,
                      color: '#86868b',
                      margin: 0,
                      marginLeft: 28,
                    }}
                  >
                    {folderDetail.repoGroupDescription}
                  </p>
                )}
              </div>
              <div
                style={{
                  padding: '4px 12px',
                  background: '#f2f2f7',
                  borderRadius: 6,
                  fontSize: 13,
                  color: '#5e5e66',
                }}
              >
                {folderDetail.fileCount} 个文件
              </div>
            </div>

            {/* 文件列表 */}
            {folderDetail.repoList.length === 0 ? (
              <Empty description="暂无文件" style={{ padding: '60px 0' }} />
            ) : (
              <Table
                dataSource={folderDetail.repoList}
                columns={fileColumns}
                rowKey="id"
                pagination={false}
                size="middle"
                style={{
                  background: '#fff',
                  borderRadius: 8,
                }}
              />
            )}
          </motion.div>
        ) : null}
      </Modal>
      <Modal
          title="文件预览"
          open={previewVisible}
          onCancel={() => setPreviewVisible(false)}
          footer={null}
          width={900}
      >
        <iframe
            src={previewUrl}
            style={{ width: '100%', height: '500px', border: 'none' }}
        />
      </Modal>
    </div>
  );
};

export default StudentKnowledgePanel;
