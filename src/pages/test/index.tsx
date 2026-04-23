'use client';

import React, { useState } from 'react';

interface GeneratedVideo {
    url: string;
    width: number;
    height: number;
    duration: number;
}

export default function GifExportTestPage() {
    // ✅ 直接填入一个可访问的视频 URL 来测试
    const [videoUrl, setVideoUrl] = useState('https://dashscope-a717.oss-accelerate.aliyuncs.com/1d/99/20260416/e90eb603/d66a2c07-4d21-42ae-a5bb-94d7130db557.mp4?Expires=1776392474&OSSAccessKeyId=LTAI5tPxpiCM2hjmWrFXrym1&Signature=8UWnDO6NArsGt8Q4TNt9q0OWfdg%3D');
    const [duration, setDuration] = useState('5');
    const [generatedVideo, setGeneratedVideo] = useState<GeneratedVideo | null>(null);
    const [isExportingGif, setIsExportingGif] = useState(false);
    const [log, setLog] = useState<string[]>([]);

    const addLog = (msg: string) => {
        setLog(prev => [`[${new Date().toLocaleTimeString()}] ${msg}`, ...prev]);
    };

    const loadScript = (src: string): Promise<void> => {
        return new Promise((resolve, reject) => {
            if ((window as any).GIF) return resolve();
            const script = document.createElement('script');
            script.src = src;
            script.onload = () => resolve();
            script.onerror = reject;
            document.body.appendChild(script);
        });
    };

    // 加载视频，读取真实宽高
    const handleLoadVideo = () => {
        if (!videoUrl.trim()) return;
        const v = document.createElement('video');
        v.src = videoUrl.trim();
        v.crossOrigin = 'anonymous';
        v.onloadedmetadata = () => {
            setGeneratedVideo({
                url: videoUrl.trim(),
                width: v.videoWidth || 1280,
                height: v.videoHeight || 720,
                duration: Math.floor(v.duration) || parseInt(duration),
            });
            addLog(`视频加载成功：${v.videoWidth}x${v.videoHeight}，时长 ${Math.floor(v.duration)}s`);
        };
        v.onerror = () => addLog('❌ 视频加载失败，请检查 URL 或 CORS');
    };

    const handleExportGif = async () => {
        if (!generatedVideo || isExportingGif) return;
        setIsExportingGif(true);
        addLog('开始加载 gif.js...');

        try {
            await loadScript('https://cdn.jsdelivr.net/npm/gif.js@0.2.0/dist/gif.min.js');
            addLog('gif.js 加载完成');

            const { width, height, url } = generatedVideo;
            const targetDuration = parseInt(duration);

            const video = document.createElement('video');
            video.muted = true;
            video.crossOrigin = 'anonymous';
            video.src = url;

            await new Promise<void>((resolve, reject) => {
                video.onloadedmetadata = () => resolve();
                video.onerror = () => reject(new Error('视频加载失败'));
            });
            addLog(`视频元数据加载完成，准备截帧`);

            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            const ctx = canvas.getContext('2d')!;

            const fps = 8;
            const totalFrames = fps * targetDuration;
            addLog(`总帧数：${totalFrames}（${fps}fps × ${targetDuration}s）`);

            const gif = new (window as any).GIF({
                workers: 2,
                quality: 10,
                width,
                height,
                repeat: 0,
                // ⚠️ workerScript 如果导出失败可换为本地路径
                workerScript: '/gif.worker.js',
            });

            for (let i = 0; i < totalFrames; i++) {
                const targetTime = i / fps;

                await new Promise<void>((resolve, reject) => {
                    const timeout = setTimeout(() => reject(new Error(`Seek 超时 frame=${i}`)), 5000);
                    video.onseeked = () => {
                        clearTimeout(timeout);
                        resolve();
                    };
                    video.currentTime = targetTime;
                });

                ctx.drawImage(video, 0, 0, width, height);
                gif.addFrame(canvas, { delay: 1000 / fps, copy: true });

                if (i % fps === 0) {
                    addLog(`截帧进度：${i + 1} / ${totalFrames}`);
                }
            }

            addLog('所有帧截取完成，开始渲染 GIF...');

            gif.on('progress', (p: number) => {
                addLog(`GIF 渲染进度：${Math.round(p * 100)}%`);
            });

            gif.on('finished', (blob: Blob) => {
                addLog(`✅ GIF 渲染完成，大小：${(blob.size / 1024).toFixed(1)} KB`);
                const gifUrl = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = gifUrl;
                a.download = `test-gif-${Date.now()}.gif`;
                a.click();
                URL.revokeObjectURL(gifUrl);
                setIsExportingGif(false);
            });

            gif.render();

        } catch (err: any) {
            addLog(`❌ 导出失败：${err.message}`);
            console.error(err);
            setIsExportingGif(false);
        }
    };

    return (
        <div style={{ padding: 32, fontFamily: 'monospace', maxWidth: 800 }}>
            <h2 style={{ marginBottom: 16 }}>🧪 GIF 导出功能测试页</h2>

            {/* 视频 URL 输入 */}
            <div style={{ marginBottom: 12 }}>
                <label style={{ display: 'block', marginBottom: 4 }}>视频 URL（支持 mp4）</label>
                <input
                    value={videoUrl}
                    onChange={e => setVideoUrl(e.target.value)}
                    placeholder="粘贴视频直链，例如 https://xxx.com/test.mp4"
                    style={{ width: '100%', padding: '8px', boxSizing: 'border-box', marginBottom: 8 }}
                />
                <button onClick={handleLoadVideo} style={btnStyle('#4f46e5')}>
                    加载视频
                </button>
            </div>

            {/* 时长选择 */}
            <div style={{ marginBottom: 12 }}>
                <label style={{ display: 'block', marginBottom: 4 }}>导出时长（秒）</label>
                <div style={{ display: 'flex', gap: 8 }}>
                    {['3', '5', '10', '15'].map(d => (
                        <button
                            key={d}
                            onClick={() => setDuration(d)}
                            style={btnStyle(duration === d ? '#059669' : '#6b7280')}
                        >
                            {d}s
                        </button>
                    ))}
                </div>
            </div>

            {/* 视频预览 */}
            {generatedVideo && (
                <div style={{ marginBottom: 12 }}>
                    <video
                        src={generatedVideo.url}
                        controls
                        muted
                        style={{ width: '100%', maxHeight: 300, background: '#000' }}
                        crossOrigin="anonymous"
                    />
                    <p style={{ fontSize: 12, color: '#6b7280' }}>
                        {generatedVideo.width} × {generatedVideo.height} | 时长 {generatedVideo.duration}s
                    </p>
                </div>
            )}

            {/* 导出按钮 */}
            <button
                onClick={handleExportGif}
                disabled={!generatedVideo || isExportingGif}
                style={btnStyle(isExportingGif ? '#9ca3af' : '#dc2626')}
            >
                {isExportingGif ? '⏳ 导出中...' : '🎞 导出 GIF'}
            </button>

            {/* 日志输出 */}
            <div style={{
                marginTop: 20,
                background: '#1e1e1e',
                color: '#d4d4d4',
                padding: 12,
                borderRadius: 8,
                height: 240,
                overflowY: 'auto',
                fontSize: 12,
            }}>
                <strong style={{ color: '#9ca3af' }}>日志输出：</strong>
                {log.length === 0 && <p style={{ color: '#6b7280' }}>暂无日志</p>}
                {log.map((l, i) => <p key={i} style={{ margin: '2px 0' }}>{l}</p>)}
            </div>
        </div>
    );
}

function btnStyle(bg: string): React.CSSProperties {
    return {
        background: bg,
        color: '#fff',
        border: 'none',
        padding: '8px 16px',
        borderRadius: 6,
        cursor: 'pointer',
        marginRight: 8,
    };
}