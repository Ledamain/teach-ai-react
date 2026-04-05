import {
    KnowledgeCategory,
    KnowLedgeCourseParams,
    KnowledgeFile,
    KnowledgeFolder,
    KnowledgeFolderDetail, KnowLedgeFolderParams
} from "@/types/repo/RepoType";
import {request} from "@/utils/request";
import {Course} from "@/types/workspace/WorkspaceType";
import {InitiatePptCreationResp} from "@/types/ppt/PptType";

export async function getKnowledgeList() {
    return request.get<KnowledgeCategory>('/repo/list', {
        params: {
            pageReqVO: null
        },
    })
}
export async function getKnowledgeArray(param: KnowLedgeCourseParams) {
    return request.get<string[]>('/repo/getRepoArray', {
        params: param
    })
}

/**
 * 删除知识库文件夹
 * @param courseId 课程ID
 * @param folderId 文件夹ID
 */
export const deleteKnowledgeFolder = async (courseId: string, folderId: string): Promise<void> => {
    // TODO: 替换为实际 API 调用
    // await fetch(`${API_BASE_URL}/courses/${courseId}/knowledge/folders/${folderId}`, {
    //   method: 'DELETE',
    // });

    console.log('删除文件夹:', courseId, folderId);
};

/**
 * 更新知识库文件夹状态（开关）
 * @param courseId 课程ID
 * @param folderId 文件夹ID
 * @param enabled 开启状态
 */
export const updateKnowledgeFolderStatus = async (
    courseId: string,
    folderId: string,
    enabled: boolean
): Promise<void> => {
    // TODO: 替换为实际 API 调用
    // await fetch(`${API_BASE_URL}/courses/${courseId}/knowledge/folders/${folderId}/status`, {
    //   method: 'PATCH',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ enabled }),
    // });

    console.log('更新文件夹状态:', courseId, folderId, enabled);
};

/**
 * 上传文件到知识库（带描述）
 * @param courseId 课程ID
 * @param folderId 文件夹ID
 * @param file 文件
 * @param repoDesp 知识库描述
 * @param repoCategoryId 学科ID
 * @param repoGroupId 学科文件夹ID
 */
export const uploadKnowledgeFileWithDesc = async (
    courseId: string,
    folderId: string,
    file: File,
    repoDesp: string,
    repoCategoryId: string,
    repoGroupId: string
): Promise<KnowledgeFile> => {
    // TODO: 替换为实际 API 调用
    // const formData = new FormData();
    // formData.append('file', file);
    // formData.append('repoDesp', repoDesp);
    // formData.append('repoCategoryId', repoCategoryId);
    // formData.append('repoGroupId', repoGroupId);
    // const response = await fetch(
    //   `${API_BASE_URL}/courses/${courseId}/knowledge/folders/${folderId}/files`,
    //   { method: 'POST', body: formData }
    // );
    // return response.json();

    console.log('上传文件:', { courseId, folderId, repoDesp, repoCategoryId, repoGroupId });
    return {
        id: Date.now().toString(),
        folderId,
        name: file.name,
        type: file.name.split('.').pop() || 'unknown',
        size: file.size,
        uploadedAt: new Date().toISOString(),
        url: `/files/${Date.now()}.${file.name.split('.').pop()}`,
    };
};

/**
 * 删除知识库文件
 * @param courseId 课程ID
 * @param folderId 文件夹ID
 * @param fileId 文件ID
 */
export const deleteKnowledgeFile = async (
    courseId: string,
    folderId: string,
    fileId: string
): Promise<void> => {
    // TODO: 替换为实际 API 调用
    // await fetch(`${API_BASE_URL}/courses/${courseId}/knowledge/folders/${folderId}/files/${fileId}`, {
    //   method: 'DELETE',
    // });

    console.log('删除文件:', courseId, folderId, fileId);
};

/**
 * 上传文件到知识库
 * @param courseId 课程ID
 * @param folderId 文件夹ID
 * @param file 文件
 */
export const uploadKnowledgeFile = async (
    courseId: string,
    folderId: string,
    file: File
): Promise<KnowledgeFile> => {
    // TODO: 替换为实际 API 调用
    // const formData = new FormData();
    // formData.append('file', file);
    // const response = await fetch(
    //   `${API_BASE_URL}/courses/${courseId}/knowledge/folders/${folderId}/files`,
    //   { method: 'POST', body: formData }
    // );
    // return response.json();

    return {
        id: Date.now().toString(),
        folderId,
        name: file.name,
        type: file.name.split('.').pop() || 'unknown',
        size: file.size,
        uploadedAt: new Date().toISOString(),
    };
};
export default {
    /**
     * 获取知识库文件夹列表
     * @param params 分页参数
     * @returns 知识库文件夹列表
     */
    getKnowledgeFolders(courseId: number | null) {
        return request.get<KnowledgeFolder[]>('/repo/getRepoArrayByCourseId',{
            params: {
                courseId: courseId
            }
        });
    },

    /**
     * 获取知识库文件夹详情
     * @param courseId 课程ID
     * @param folderId 文件夹ID
     * @returns 文件夹详情（含文件列表）
     */
    getKnowledgeFolderDetail(courseId: number | null,folderId: number | null) {
        return request.get<KnowledgeFolderDetail[]>('/repo/get-repo-list-by-course-id-and-repo-group-id',{
            params: {
                repoCategoryId: courseId,
                repoGroupId: folderId
            }
        });
    },
    /**
     * 创建知识库文件夹
     * @param data 参数
     */
    createKnowledgeFolder(data: KnowLedgeFolderParams) {
        return request.post<number>('/repo/create', data);
    },
    /**
     * 修改知识库文件夹
     * @param data 参数
     */
    updateKnowledgeFolder(data: KnowLedgeFolderParams) {
        return request.put<boolean>('/repo/update', data);
    },
}