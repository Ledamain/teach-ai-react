import {request} from "@/utils/request";
import {TeacherType} from "@/types/user/UserType";

export default {
    /**
     * 获取教师列表
     * @param params 分页参数
     * @returns 教师列表
     */
    getTeacherList() {
        return request.get<TeacherType[]>('/user-teacher/get-teacher-list');
    },
}