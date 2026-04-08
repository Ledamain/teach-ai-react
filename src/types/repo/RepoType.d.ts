interface KnowledgeCategory {
    id: string;
    repoCategoryName: string;
    repoDTOS: KnowledgeFile[];
}

interface KnowLedgeCourseParams {
    repoTitle?: string;
    repoFile?: string;
    repoDesp?: string;
    repoCategoryId?: number;
    repoStatus?: string;
}

// 知识库文件夹类型
export interface KnowledgeFolder {
    id: number;
    repoGroupName: string;
    repoGroupDescription?: string;
    fileCount: number;
    createTime: string;
    updateTime: string;
}

// 知识库文件类型
export interface KnowledgeFile {
    id: string;
    repoGroupId: string;
    repoCategoryId: string;
    repoTitle: string;
    type: string; // pdf, doc, ppt, txt, md, docx, pptx etc.
    size: number;
    createTime: string;
    repoFile?: string; // 文件下载地址
    repoStatus: string
}

// 知识库文件夹详情类型
export interface KnowledgeFolderDetail {
    id: string;
    repoGroupName: string;
    repoGroupDescription?: string; // 知识库描述
    fileCount: number;
    enabled: boolean; // 开启状态
    createTime: string;
    updateTime: string;
    repoList: KnowledgeFile[];
}

export interface KnowLedgeFolderParams {
    id?: number;
    repoCategoryId: number;
    repoGroupDescription: string;
    repoGroupName: string;
}

// 知识库文件夹上传参数
export interface KnowledgeFolderUploadParams {
    repoTitle: string;
    fileUrl: string;    // 文件地址
    repoDesp: string;   // 描述
    repoCategoryId: number;   // 学科ID
    repoGroupId: number; // 文件夹ID
}