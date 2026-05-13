import { request } from '@/utils/request';
import {DigitalHumanRespType} from "@/types/digital/DigitalHumanRespType";

export default {

    // 创建数字人聊天
    createDigitalHumanChat() {
        return request.get<DigitalHumanRespType>('/digital-human/create');
    },
    // 关闭数字人聊天
    closeDigitalHumanChat(sign: string) {
        return request.get<boolean>('/digital-human/close',{
            params: {
                sign: sign
            }
        });
    },
};