import {request} from "@/utils/request";
import {RepoCategoryParams} from "@/types/repoCategory/RepoCategoryType";

export default {
    /**
     * 创建学科
     * @param data 参数
     */
    createRepoCategory(data: RepoCategoryParams) {
        return request.post<number>('/repo-category/create', data);
    },

    /**
     * 修改学科
     * @param data 参数
     */
    updateRepoCategory(data: RepoCategoryParams) {
        return request.post<number>('/repo-category/update', data);
    },
}