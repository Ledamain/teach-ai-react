export interface ChatStreamParams {
    memoryId: string;
    prompt: string;
    knowledgeIds: string;
}

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;