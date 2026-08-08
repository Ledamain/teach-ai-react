import {request} from "@/utils/request";
import {CLassesType} from "@/types/classes/ClassesType";

export default {
    /**
     * 获取教师列表
     * @param params 分页参数
     * @returns 教师列表
     */
    getClassesListByRepoCategoryId(repoCategoryId: number) {
        return request.get<CLassesType[]>('/classes/list-by-repo-category',{
            params:{
                repoCategoryId: repoCategoryId,
            }
        });
    },
}