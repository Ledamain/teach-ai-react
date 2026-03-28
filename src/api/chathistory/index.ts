import {request} from "@/utils/request";
import {ChatListType, ChatMessageType} from "@/types/chatHistory/ChatHistoryType";

export async function getHistoryList(userId: string) {
    return request.get<ChatListType>('/get-chat-history-list', {
        params: {
            userId: userId
        }
    })
}


export async function getHistory(memoryId: string) {
    return request.get<ChatMessageType>('/get-chat-history', {
        params: {
            memoryId: memoryId
        }
    })
}