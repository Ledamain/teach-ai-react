'use client';

import React, { useEffect, useState, useCallback, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {Button, Modal, Input, message, Popconfirm, Empty, Switch, Table, Tooltip, Upload, Select} from 'antd';
import type { UploadProps } from 'antd';
import {
  FolderOutlined,
  PlusOutlined,
  EditOutlined,
  UploadOutlined,
  DeleteOutlined,
  FileTextOutlined,
  FileWordOutlined,
  FilePptOutlined,
  FileMarkdownOutlined,
  DownloadOutlined,
  CloseOutlined,
  InboxOutlined
} from '@ant-design/icons';
import {
  createKnowledgeFolder,
  renameKnowledgeFolder,
  deleteKnowledgeFolder,
} from '@/api/repo/index';
import KnowLedgeApi from '@/api/repo/index'
import WorkspaceApi from '@/api/workspace/index'
import styles from '@/styles/workspace/courseDetail.module.css';
import {
  KnowledgeFile,
  KnowledgeFolder,
  KnowledgeFolderDetail,
  KnowLedgeFolderParams
} from "@/types/repo/RepoType";
import {
  deleteKnowledgeFile,
  updateKnowledgeFolderStatus,
  uploadKnowledgeFileWithDesc
} from "@/api/repo";
import {Course} from "@/types/workspace/WorkspaceType";
import {UserInfo} from "@/types/login/LoginType";

const { TextArea } = Input;
const { Dragger } = Upload;

interface KnowledgePanelProps {
  courseId: string;
}

// 根据文件类型获取图标
const getFileIcon = (type: string | undefined | null) => {
  // 处理 undefined / null 情况
  const fileType = type?.toLowerCase() || '';

  switch (fileType) {
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

// 从文件名提取后缀
const getFileExtension = (filename: string) => {
  if (!filename) return '';
  return filename.split('.').pop()?.toLowerCase() || '';
};

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
};

const KnowledgePanel: React.FC<KnowledgePanelProps> = ({ courseId }) => {
  const [folders, setFolders] = useState<KnowledgeFolder[]>([]);
  const [loading, setLoading] = useState(true);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [renameModalVisible, setRenameModalVisible] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [folderForm, setFolderForm] = useState<KnowLedgeFolderParams>(
      {
        repoGroupName: '',
        repoGroupDescription: '',
        repoCategoryId: Number(courseId) // 课程ID转数字
      }
  )
  const [currentFolder, setCurrentFolder] = useState<KnowledgeFolder | null>(null);

  // 文件夹详情弹窗状态
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [folderDetail, setFolderDetail] = useState<KnowledgeFolderDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // 上传文件弹窗状态
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [repoDesp, setRepoDesp] = useState('');
  const [uploadLoading, setUploadLoading] = useState(false);

  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewUrl, setPreviewUrl] = useState('');

  const getUserId = (): number | null => {
    if (typeof window === 'undefined') return null;
    const userInfoStr = window.localStorage.getItem('userInfo');
    if (!userInfoStr) return null;
    try {
      const userInfo: UserInfo = JSON.parse(userInfoStr);
      return Number(userInfo.id);
    } catch {
      return null;
    }
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
  }, [courseId]);

  useEffect(() => {
    fetchFolders();
  }, [fetchFolders]);

  useEffect(() => {
    setFolderForm(prev => ({
      ...prev,
      repoCategoryId: Number(courseId),
    }));
  }, [courseId]);

  const handleCreateFolder = async () => {
    if (!folderForm.repoGroupName.trim()) {
      message.warning('请输入文件夹名称');
      return;
    }
    if (!folderForm.repoGroupDescription.trim()) {
      message.warning('请输入文件夹描述');
      return;
    }

    try {
      // ✅ 直接传递 folderForm
      await KnowLedgeApi.createKnowledgeFolder(folderForm);

      message.success('创建成功');
      setCreateModalVisible(false);

      // 重置表单
      setFolderForm({
        repoGroupName: '',
        repoGroupDescription: '',
        repoCategoryId: Number(courseId),
        id: undefined,
      });

      fetchFolders();
    } catch (error) {
      message.error('创建失败');
    }
  };

  const handleRenameFolder = async () => {
    if (!currentFolder || !folderForm.repoGroupName.trim()) {
      message.warning('请输入文件夹名称');
      return;
    }

    if (!currentFolder || !folderForm.repoGroupDescription.trim()) {
      message.warning('请输入文件夹描述');
      return;
    }

    try {
      await KnowLedgeApi.updateKnowledgeFolder(folderForm);

      message.success('修改成功');
      setRenameModalVisible(false);
      fetchFolders();
    } catch (error) {
      message.error('修改失败');
    }
  };

  const handleDeleteFolder = async (folderId: string) => {
    try {
      await deleteKnowledgeFolder(courseId, folderId);
      message.success('删除成功');
      fetchFolders();
    } catch (error) {
      console.error('删除失败:', error);
      message.error('删除失败');
    }
  };

  const openRenameModal = (folder: KnowledgeFolder, e: React.MouseEvent) => {
    e.stopPropagation();
    setCurrentFolder(folder);

    setFolderForm({
      repoGroupName: folder.repoGroupName,
      repoGroupDescription: folder.repoGroupDescription || '',
      repoCategoryId: Number(courseId),
      id: folder.id,
    });

    setRenameModalVisible(true);
  };

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

  // 切换开启状态
  const handleToggleStatus = async (checked: boolean) => {
    if (!folderDetail) return;

    try {
      await updateKnowledgeFolderStatus(courseId, folderDetail.id, checked);
      setFolderDetail({ ...folderDetail, enabled: checked });
      message.success(checked ? '已开启' : '已关闭');
    } catch (error) {
      console.error('更新状态失败:', error);
      message.error('更新状态失败');
    }
  };

  // 打开上传文件弹窗
  const handleOpenUploadModal = () => {
    setUploadModalVisible(true);
  };

  // 处理文件上传
  const handleUpload = async () => {
    if (!uploadFile || !folderDetail) {
      message.warning('请选择要上传的文件');
      return;
    }

    setUploadLoading(true);
    try {
      const newFile = await uploadKnowledgeFileWithDesc(
          courseId,
          folderDetail.id,
          uploadFile,
          repoDesp,
          courseId, // repoCategoryId - 学科ID
          folderDetail.id // repoGroupId - 学科文件夹ID
      );

      // 更新文件列表
      setFolderDetail({
        ...folderDetail,
        files: [...folderDetail.repoList, newFile],
        fileCount: folderDetail.fileCount + 1,
      });

      message.success('上传成功');
      setUploadModalVisible(false);
      setUploadFile(null);
      setRepoDesp('');
      fetchFolders(); // 刷新文件夹列表以更新文件数
    } catch (error) {
      console.error('上传失败:', error);
      message.error('上传失败');
    } finally {
      setUploadLoading(false);
    }
  };

  // 删除文件
  const handleDeleteFile = async (fileId: string) => {
    if (!folderDetail) return;

    try {
      await deleteKnowledgeFile(courseId, folderDetail.id, fileId);
      setFolderDetail({
        ...folderDetail,
        files: folderDetail.repoList.filter(f => f.id !== fileId),
        fileCount: folderDetail.fileCount - 1,
      });
      message.success('删除成功');
      fetchFolders();
    } catch (error) {
      console.error('删除文件失败:', error);
      message.error('删除失败');
    }
  };

  // 下载文件
  const handleDownload = (file: KnowledgeFile) => {
    // 模拟下载
    message.info(`正在下载: ${file.repoTitle}`);
    // 实际实现：window.open(file.url, '_blank');
  };

  // 上传组件配置
  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    accept: '.doc,.docx,.ppt,.pptx,.txt,.md',
    beforeUpload: (file) => {
      setUploadFile(file);
      return false; // 阻止自动上传
    },
    onRemove: () => {
      setUploadFile(null);
    },
    fileList: uploadFile ? [{ uid: '-1', name: uploadFile.name, status: 'done' }] : [],
  };

  // 文件列表表格列配置
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
      width: 180,
      render: (_: unknown, record: KnowledgeFile) => (
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <Tooltip title={record.enabled ? "关闭文件" : "开启文件"}>
              <Switch
                  checked={!!record.enabled}
                  onChange={async (checked) => {
                    try {
                      console.log("当前文件 ID：", record.id);
                      console.log("当前文件：", record);
                      message.success(`文件 ${record.id} 已${checked ? "开启" : "关闭"}`);

                      if (folderDetail) {
                        setFolderDetail({
                          ...folderDetail,
                          files: folderDetail.repoList.map(f =>
                              f.id === record.id ? { ...f, enabled: checked } : f
                          )
                        });
                      }
                    } catch (err) {
                      message.error("操作失败");
                    }
                  }}
                  style={{ background: record.enabled ? '#1a1a1a' : undefined }}
                  size="small"
              />
            </Tooltip>

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

            <Popconfirm
                title="确定要删除该文件吗？"
                onConfirm={() => handleDeleteFile(record.id)}
                okText="确定"
                cancelText="取消"
            >
              <Button
                  type="text"
                  size="small"
                  danger
                  icon={<DeleteOutlined />}
              />
            </Popconfirm>
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
          <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setCreateModalVisible(true)}
              style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
          >
            新建文件夹
          </Button>
        </motion.div>

        {/* 文件夹列表 */}
        {loading ? (
            <div>加载中...</div>
        ) : folders.length === 0 ? (
            <Empty
                description="暂无知识库文件夹"
                image={<FileTextOutlined style={{ fontSize: 64, color: '#d9d9d9' }} />}
            >
              <Button type="primary" onClick={() => setCreateModalVisible(true)} style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}>
                创建第一个文件夹
              </Button>
            </Empty>
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
                            {folder.fileCount} 个文件 · 更新于 {folder.updateTime[0] ? new Date(folder.updateTime[0]).toLocaleDateString() : ''}
                          </div>
                        </div>
                      </div>
                      <div className={styles.folderActions}>
                        <button
                            className={`${styles.folderActionBtn} ${styles.edit}`}
                            onClick={(e) => openRenameModal(folder, e)}
                        >
                          <EditOutlined /> 重命名
                        </button>
                        <Popconfirm
                            title="确定要删除该文件夹吗？"
                            description="删除后文件夹内的所有文件也将被删除"
                            onConfirm={(e) => {
                              e?.stopPropagation();
                              handleDeleteFolder(folder.id);
                            }}
                            onCancel={(e) => e?.stopPropagation()}
                            okText="确定"
                            cancelText="取消"
                        >
                          <button
                              className={`${styles.folderActionBtn} ${styles.delete}`}
                              onClick={(e) => e.stopPropagation()}
                          >
                            <DeleteOutlined /> 删除
                          </button>
                        </Popconfirm>
                      </div>
                    </motion.div>
                ))}
              </AnimatePresence>
            </div>
        )}

        {/* 创建文件夹弹窗 */}
        <Modal
            title="新建文件夹"
            open={createModalVisible}
            onOk={handleCreateFolder}
            onCancel={() => {
              setCreateModalVisible(false);
              setNewFolderName('');
              setFolderForm(prev => ({ ...prev, id: undefined }));
            }}
            okText="创建"
            cancelText="取消"
        >
          <Input
              placeholder="请输入文件夹名称"
              value={folderForm.repoGroupName}
              onChange={(e) => setFolderForm({
                ...folderForm,
                repoGroupName: e.target.value
              })}
              onPressEnter={handleCreateFolder}
              prefix={<FolderOutlined />}
              style={{ marginBottom: 12 }}
          />

          {/* 文件夹描述 */}
          <Input
              placeholder="请输入文件夹描述"
              value={folderForm.repoGroupDescription}
              onChange={(e) => setFolderForm({
                ...folderForm,
                repoGroupDescription: e.target.value
              })}
              onPressEnter={handleCreateFolder}
              prefix={<FileTextOutlined />}
          />
        </Modal>

        {/* 重命名弹窗 */}
        <Modal
            title="修改文件夹信息"
            open={renameModalVisible}
            onOk={handleRenameFolder}
            onCancel={() => {
              setRenameModalVisible(false);
              setCurrentFolder(null);
              setFolderForm(prev => ({ ...prev, id: undefined }));
            }}
            okText="确定"
            cancelText="取消"
        >
          {/* 文件夹名称 */}
          <Input
              placeholder="请输入新的文件夹名称"
              value={folderForm.repoGroupName}
              onChange={(e) => setFolderForm({
                ...folderForm,
                repoGroupName: e.target.value
              })}
              onPressEnter={handleRenameFolder}
              prefix={<FolderOutlined />}
              style={{ marginBottom: 12 }}
          />

          {/* 文件夹描述 */}
          <Input
              placeholder="请输入文件夹描述"
              value={folderForm.repoGroupDescription}
              onChange={(e) => setFolderForm({
                ...folderForm,
                repoGroupDescription: e.target.value
              })}
              onPressEnter={handleRenameFolder}
              prefix={<FileTextOutlined />}
          />
        </Modal>

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
            className={styles.folderDetailModal}
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
                <div style={{
                  display: 'flex',
                  alignItems: 'flex-start',
                  justifyContent: 'space-between',
                  marginBottom: 24,
                  paddingBottom: 20,
                  borderBottom: '1px solid #eef0f2'
                }}>
                  <div>
                    <h2 style={{
                      fontSize: 20,
                      fontWeight: 600,
                      color: '#1a1a1a',
                      margin: 0,
                      marginBottom: 8
                    }}>
                      <FolderOutlined style={{ marginRight: 10, color: '#5e5e66' }} />
                      {folderDetail.repoGroupName}
                    </h2>
                    {folderDetail.repoGroupDescription && (
                        <p style={{
                          fontSize: 14,
                          color: '#86868b',
                          margin: 0,
                          marginLeft: 28
                        }}>
                          {folderDetail.repoGroupDescription}
                        </p>
                    )}
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                    <Button
                        type="primary"
                        icon={<UploadOutlined />}
                        onClick={handleOpenUploadModal}
                        style={{ background: '#1a1a1a', borderColor: '#1a1a1a' }}
                    >
                      上传文件
                    </Button>
                  </div>
                </div>

                {/* 文件列表 */}
                {folderDetail.repoList.length === 0 ? (
                    <Empty
                        description="暂无文件，点击上方按钮上传"
                        style={{ padding: '60px 0' }}
                    />
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

        {/* 上传文件弹窗 */}
        <Modal
            title="上传文件"
            open={uploadModalVisible}
            onOk={handleUpload}
            onCancel={() => {
              setUploadModalVisible(false);
              setUploadFile(null);
              setRepoDesp('');
            }}
            okText="确定"
            cancelText="取消"
            confirmLoading={uploadLoading}
            okButtonProps={{ style: { background: '#1a1a1a', borderColor: '#1a1a1a' } }}
        >
          <div style={{ marginBottom: 20 }}>
            <Dragger {...uploadProps}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined style={{ color: '#1a1a1a' }} />
              </p>
              <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
              <p className="ant-upload-hint">
                支持 docx、doc、ppt、pptx、txt、md 格式文件
              </p>
            </Dragger>
          </div>

          <div>
            <div style={{ marginBottom: 8, fontSize: 14, color: '#1a1a1a' }}>知识库描述</div>
            <TextArea
                placeholder="请输入知识库描述（选填）"
                value={repoDesp}
                onChange={e => setRepoDesp(e.target.value)}
                rows={3}
                showCount
                maxLength={200}
            />
          </div>
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

export default KnowledgePanel;