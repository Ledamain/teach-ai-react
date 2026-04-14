import {request} from "@/utils/request";

export default {
    /**
     * 获取热点词列表
     */
    getClassesListByRepoCategoryId() {
        return request.get<string[]>('/hot-query/get');
    },
}