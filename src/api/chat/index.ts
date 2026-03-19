import { ChatStreamParams } from '@/types/chat/ChatType';

/**
 * 流式调用 AI 聊天接口
 * @param params - { memoryId, prompt }
 * @param onChunk - 每收到一个文本块时的回调 (chunk, isFinal)
 * @param signal - AbortSignal，用于取消请求
 */

const getApiUrl = (path: string): string => {
    if (typeof window === 'undefined') {
        // 服务端调用时（虽然 streamChat 不应在服务端用，但防御性处理）
        return path.startsWith('/') ? path : `/api${path}`;
    }

    const base = process.env.NEXT_PUBLIC_API_URL_LOC?.replace(/\/$/, '');
    if (!base) {
        // 默认回退到当前域的 /api（适用于 Next.js 内置 API）
        return path.startsWith('/') ? path : `/api/${path.replace(/^\//, '')}`;
    }
    return `${base}${path.startsWith('/') ? path : `/${path}`}`;
};

export async function streamChat(
    params: ChatStreamParams,
    onChunk: (chunk: string, isFinal: boolean) => void,
    signal?: AbortSignal
): Promise<void> {
    if (typeof window === 'undefined') {
        throw new Error('streamChat can only be used in the browser');
    }

    const controller = new AbortController();
    signal?.addEventListener('abort', () => controller.abort());

    try {
        const token = typeof localStorage !== 'undefined'
            ? localStorage.getItem('token')
            : null;

        // 👇 使用动态 API 地址
        const url = getApiUrl('/client-api/client-system/stream-post');

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { 'Client-Authorization': `${token}` } : {}),
            },
            body: JSON.stringify(params),
            signal: controller.signal,
        });

        if (!response.ok) {
            const errorText = await response.text().catch(() => 'Unknown error');
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        if (!response.body) {
            throw new Error('Response body is empty');
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        try {
            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                const chunk = decoder.decode(value, { stream: true });
                if (chunk) {
                    onChunk(chunk, false); // 不是最终块
                }
            }
            onChunk('', true); // 标记结束
        } finally {
            reader.releaseLock();
        }
    } catch (error) {
        if ((error as Error).name === 'AbortError') {
            console.log('Stream chat request was aborted');
            return;
        }
        throw error; // 抛出其他错误供调用方处理
    }
}