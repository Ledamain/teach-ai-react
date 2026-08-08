export interface ChatStreamParams {
    memoryId: string;
    prompt: string;
    knowledgeIds: string;
    // modeStatus: string;
}

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;