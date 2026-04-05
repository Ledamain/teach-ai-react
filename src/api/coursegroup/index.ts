import {request} from "@/utils/request";
import {CourseGroupType} from "@/types/coursegroup/CourseGroupType";

export default {
    /**
     * 获取教师列表
     * @param params 分页参数
     * @returns 教师列表
     */
    getCourseGroupList() {
        return request.get<CourseGroupType[]>('/course-group/list');
    },
}