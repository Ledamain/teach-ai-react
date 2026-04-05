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
    id: string;
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
