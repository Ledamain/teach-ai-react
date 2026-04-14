import {request} from "@/utils/request";

export async function getHistoryList(userId: string) {
    return request.get<HistoryListItem>('/get-chat-history-list', {
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