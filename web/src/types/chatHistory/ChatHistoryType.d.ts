interface ChatHistoryItem {
    memoryId: string;
    messageTitle: string | null;
    messagesJson: string;
    createTime: number | string;
    updateTime: number | string;
}

interface ChatMessageType {
    messages: Array<{
        id: string;
        role: string;
        content: string;
        thinking: boolean;
    }>;
    createTime: string;
    updateTime: string;
}

interface HistoryListItem {
    memoryId: string;
    messageTitle: string;
    messagesJson: string;
    createTime: number;
    updateTime: number;
}