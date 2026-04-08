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
  InboxOutlined,
  CodeOutlined
} from '@ant-design/icons';
import {
  deleteKnowledgeFolder,
} from '@/api/repo/index';
import KnowLedgeApi from '@/api/repo/index'
import WorkspaceApi from '@/api/workspace/index'
import styles from '@/styles/workspace/courseDetail.module.css';
import {
  KnowledgeFile,
  KnowledgeFolder,
  KnowledgeFolderDetail,
  KnowLedgeFolderParams, KnowledgeFolderUploadParams
} from "@/types/repo/RepoType";
import {
  deleteKnowledgeFile,
} from "@/api/repo";
import {Course} from "@/types/workspace/WorkspaceType";
import {UserInfo} from "@/types/login/LoginType";

const { TextArea } = Input;
const { Dragger } = Upload;

interface KnowledgePanelProps {
  courseId: number;
}

// 根据文件类型获取图标
const getFileIcon = (type: string | undefined | null) => {
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
        repoCategoryId: Number(courseId)
      }
  )
  const [currentFolder, setCurrentFolder] = useState<KnowledgeFolder | null>(null);

  // 文件夹详情弹窗状态
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [folderDetail, setFolderDetail] = useState<KnowledgeFolderDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // 上传文件弹窗状态
  const [uploadModalVisible, setUploadModalVisible] = useState(false);
  const [repoDesp, setRepoDesp] = useState('');
  const [uploadLoading, setUploadLoading] = useState(false);

  // 存储上传后返回的文件地址
  const [fileUrl, setFileUrl] = useState('');

  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewUrl, setPreviewUrl] = useState('');

  const [loadingIds, setLoadingIds] = useState<(string | number)[]>([]);

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
      await KnowLedgeApi.createKnowledgeFolder(folderForm);

      message.success('创建成功');
      setCreateModalVisible(false);

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

  const handleOpenUploadModal = () => {
    setUploadModalVisible(true);
  };

  // ==============================================
  // 🔥 核心修改：最终提交上传（文件地址 + 描述 + ID）
  // ==============================================
  const handleUpload = async () => {
    if (!fileUrl) {
      message.warning('请先上传文件');
      return;
    }
    if (!folderDetail) {
      message.warning('未找到文件夹信息');
      return;
    }

    setUploadLoading(true);
    try {
      // 调用后端保存接口
      const splitArr = fileUrl.split('/');
      const title = splitArr[splitArr.length - 1];
      const submitData: KnowledgeFolderUploadParams =  {
        repoTitle: title,
        repoCategoryId: courseId,
        repoGroupId: Number(folderDetail.id),
        repoDesp: repoDesp,
        fileUrl: fileUrl,
      }
      await KnowLedgeApi.uploadKnowledgeFileWithDesc(submitData); // 这里不需要接收 newFile

      // 前端刷新列表 —— 直接用接口返回的完整数据
      setDetailLoading(true);
      try {
        const detail = await KnowLedgeApi.getKnowledgeFolderDetail(courseId, folderDetail.id);
        setFolderDetail(detail); // 这一行就够了
      } catch (error) {
        console.error('获取文件夹详情失败:', error);
        message.error('获取文件夹详情失败');
      } finally {
        setDetailLoading(false);
      }

      message.success('上传保存成功');
      setUploadModalVisible(false);
      setFileUrl('');
      setRepoDesp('');
      fetchFolders();
    } catch (error) {
      console.error('保存失败:', error);
      message.error('保存失败');
    } finally {
      setUploadLoading(false);
    }
  };

  const handleDeleteFile = async (fileId: string) => {
    if (!folderDetail) return;

    try {
      await deleteKnowledgeFile(courseId, folderDetail.id, fileId);
      setFolderDetail({
        ...folderDetail,
        repoList: folderDetail.repoList.filter(f => f.id !== fileId),
        fileCount: folderDetail.fileCount - 1,
      });
      message.success('删除成功');
      fetchFolders();
    } catch (error) {
      console.error('删除文件失败:', error);
      message.error('删除失败');
    }
  };

  const handleDownload = (file: KnowledgeFile) => {
    message.info(`正在下载: ${file.repoTitle}`);
  };

  // ==============================================
  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    accept: '.doc,.docx,.ppt,.pptx,.txt,.md',
    // 启用自动上传
    action: `${process.env.NEXT_PUBLIC_API_URL_LOC}/admin-api/infra/file/upload`,
    // 上传成功后拿到文件地址
    onChange(info) {
      if (info.file.status === 'done') {
        // 假设后端返回 { url: "xxx" }
        const resUrl = info.file.response?.data;
        if (resUrl) {
          setFileUrl(resUrl);
          message.success('文件上传成功');
        }
      } else if (info.file.status === 'error') {
        message.error('文件上传失败');
      }
    },
    // 移除文件清空地址
    onRemove() {
      setFileUrl('');
    },
  };

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
              icon={<CodeOutlined />}
              style={{ padding: 0}}
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
            <Tooltip title={record.repoStatus === '1' ? "关闭文件" : "开启文件"}>
              <Switch
                  checked={record.repoStatus === '1'}
                  loading={loadingIds.includes(record.id)}
                  disabled={loadingIds.includes(record.id)}
                  onChange={async (checked) => {
                    setLoadingIds([...loadingIds, record.id]);
                    try {
                      const newStatus = checked ? '1' : '0';
                      await KnowLedgeApi.changeRepoStatus(Number(record.id), newStatus);
                      if (folderDetail) {
                        setFolderDetail({
                          ...folderDetail,
                          repoList: folderDetail.repoList.map(f =>
                              f.id === record.id ? { ...f, repoStatus: newStatus } : f
                          )
                        });
                      }
                      message.success(`文件 ${record.repoTitle} 已${checked ? "开启" : "关闭"}`);
                    } catch (err) {
                      console.error(err);
                      message.error("操作失败，请重试");
                    } finally {
                      setLoadingIds(loadingIds.filter(id => id !== record.id));
                    }
                  }}
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
              setFileUrl('');
              setRepoDesp('');
            }}
            okText="确定保存"
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

            {/* 显示已上传的文件地址 */}
            {fileUrl && (
                <div style={{ marginTop: 10, color: '#52c41a' }}>
                  文件上传完成：{fileUrl}
                </div>
            )}
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