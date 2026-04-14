import { request } from '@/utils/request';
import {VideoChatParam} from "@/types/video/VideoType";

export default {

    createVideo(data:VideoChatParam) {
        return request.post<string>('/video/create',data);
    },
    getVideoResult(taskId: string, userId: number) {
        return request.get<string>(`/video/result`,{
            params: {
                taskId: taskId,
                userId: userId
            }
        });
    },
};