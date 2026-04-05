const getApiUrl = (path: string): string => {
    if (typeof window === 'undefined') return path;
    const base = process.env.NEXT_PUBLIC_API_URL_LOC?.replace(/\/$/, '');
    if (!base) return path.startsWith('/') ? path : `/api/${path.replace(/^\//, '')}`;
    return `${base}${path.startsWith('/') ? path : `/${path}`}`;
};

function getToken(): string {
    if (typeof window === 'undefined') return '';
    return (
        localStorage.getItem('token') ??
        sessionStorage.getItem('token') ??
        document.cookie.match(/(?:^|;\s*)token=([^;]*)/)?.[1] ??
        ''
    );
}

export interface PptOutlineChunk {
    taskId?: string;
    text?: string;
}

export interface PptQuery {
    query: string;
    fileName: string;
    pptMemoryId: string
}

/**
 * 流式生成 PPT 大纲
 * @param query        - PPT 主题
 * @param onChunk      - 每收到一段大纲时的回调 (data, isFinal)
 * @param signal       - AbortSignal，用于取消请求
 */
export async function streamPptOutline(
    query: PptQuery,
    onChunk: (data: PptOutlineChunk, isFinal: boolean) => void,
    signal?: AbortSignal
): Promise<void> {
    if (typeof window === 'undefined') {
        throw new Error('streamPptOutline can only be used in the browser');
    }

    const controller = new AbortController();
    signal?.addEventListener('abort', () => controller.abort());

    const token = getToken();
    const url = getApiUrl(
        `/client-api/client-system/ppt/runPptOutlineGeneration`
    );

    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'text/event-stream',
            'Content-Type': 'application/json',
            ...(token ? { 'Client-Authorization': token } : {}),
        },
        body: JSON.stringify(query),
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
    const decoder = new TextDecoder('utf-8');
    let buffer = '';

    try {
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });

            const messages = buffer.split('\n\n');
            buffer = messages.pop() ?? '';

            for (const msg of messages) {
                if (!msg.trim()) continue;

                const eventMatch = msg.match(/^event:\s*(.+)$/m);
                const dataMatch  = msg.match(/^data:\s*(.+)$/m);
                const event   = eventMatch?.[1]?.trim();
                const dataStr = dataMatch?.[1]?.trim();

                if (!dataStr) continue;

                try {
                    const data: PptOutlineChunk = JSON.parse(dataStr);

                    if (event === 'task-outline-generate') {
                        onChunk(data, false);
                    }

                    if (event === 'task-outline-generate-end') {
                        onChunk(data, true);
                    }
                } catch {
                    // JSON parse error, skip
                }
            }
        }
    } catch (error) {
        if ((error as Error).name === 'AbortError') {
            console.log('streamPptOutline was aborted');
            return;
        }
        throw error;
    } finally {
        reader.releaseLock();
    }
}