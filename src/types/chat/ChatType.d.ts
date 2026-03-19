export interface ChatStreamParams {
    memoryId: string;
    prompt: string;
}

export type ChatChunkCallback = (chunk: string, isFinal: boolean) => void;