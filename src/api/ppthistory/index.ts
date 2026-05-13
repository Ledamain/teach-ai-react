import { request } from '@/utils/request';
import {PptHistoryType} from "@/types/ppthistory/PptHistoryType";

export default {

    getHistoryList(userId: number) {
        return request.get<PptHistoryType>(`/ppt-history/list`,{
            params: {
                userId: userId
            }
        });
    },
};