import {
    KnowledgeCategory,
    KnowLedgeCourseParams,
    KnowledgeFile,
    KnowledgeFolder,
    KnowledgeFolderDetail, KnowLedgeFolderParams, KnowledgeFolderUploadParams
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
     * 启用/关闭文件
     * @returns 知识库状态
     */
    changeRepoStatus(id: number | null,status: string) {
        return request.get<boolean>('/repo/change-status',{
            params: {
                id: id,
                status: status
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
    /**
     * 上传文件到知识库（带描述）
     * @param courseId 课程ID
     * @param folderId 文件夹ID
     * @param file 文件
     * @param repoDesp 知识库描述
     * @param repoCategoryId 学科ID
     * @param repoGroupId 学科文件夹ID
     */
    uploadKnowledgeFileWithDesc(data: KnowledgeFolderUploadParams){
        return request.post('/repo/create-repo',data)
    }
}