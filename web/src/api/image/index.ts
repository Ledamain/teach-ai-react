import { request } from '@/utils/request';
import {ImageParam} from "@/types/image/ImageType";

export default {

    // 模板列表
    createImage(data:ImageParam) {
        return request.post<string>('/image/create',data);
    },
    getImageResult(taskId: string, userId: number) {
        return request.get<string>(`/image/result`,{
            params: {
                taskId: taskId,
                userId: userId
            }
        });
    },
};