'use client';

import { useEffect, useRef, useState } from 'react';

// 1. 移除顶部 import，改为动态导入
let Duix: any = null;

const DuixDigitalHuman = () => {
    const signRef = useRef<HTMLInputElement>(null);
    const cidRef = useRef<HTMLInputElement>(null);
    const subtitleRef = useRef<HTMLDivElement>(null);
    const [isClient, setIsClient] = useState(false);

    // 2. 客户端挂载后再加载库
    useEffect(() => {
        setIsClient(true);
        // 动态导入浏览器端库
        import('duix-guiji-light').then((mod) => {
            Duix = mod.default;
        });
    }, []);

    // 初始化数字人
    const init = () => {
        // 3. 增加判断：必须客户端 + 库加载完成
        if (!isClient || !Duix) {
            alert('客户端加载中，请稍后再试');
            return;
        }

        const sign = signRef.current?.value.trim();
        const conversationId = cidRef.current?.value.trim();

        if (!sign || !conversationId) {
            alert('请输入 sign 和 conversationId');
            return;
        }

        try {
            const duix = new Duix();

            duix.on('error', (err: any) => console.error('Duix 错误：', err));

            duix.on('initialSuccess', () => {
                duix.start({ openAsr: true });
            });

            duix.on('show', () => {
                const modal = document.getElementById('modal');
                if (modal) modal.style.display = 'none';
                if (subtitleRef.current) subtitleRef.current.style.visibility = 'visible';
            });

            duix.on('asrStart', () => {
                if (subtitleRef.current) subtitleRef.current.innerText = '';
            });

            duix.on('speakSection', (data: { text: string }) => {
                if (subtitleRef.current) subtitleRef.current.innerText = data.text;
            });

            duix.init({
                sign,
                containerLable: '.remote-container',
                conversationId,
                platform: 'duix.com',
            });
        } catch (error) {
            console.error('初始化失败：', error);
        }
    };

    return (
        <div style={{ width: '100vw', height: '100vh', position: 'relative', margin: 0, padding: 0 }}>
            <div
                id="modal"
                style={{
                    position: 'absolute',
                    top: 20,
                    left: 20,
                    zIndex: 9999,
                    background: '#fff',
                    padding: '16px 20px',
                    borderRadius: 8,
                    boxShadow: '0 0 10px rgba(0,0,0,0.2)',
                }}
            >
                <input
                    ref={signRef}
                    placeholder="请输入 sign"
                    style={{ display: 'block', marginBottom: 10, padding: 6, width: 240 }}
                />
                <input
                    ref={cidRef}
                    placeholder="请输入 conversationId"
                    style={{ display: 'block', marginBottom: 10, padding: 6, width: 240 }}
                />
                <button onClick={init} style={{ padding: '8px 16px', cursor: 'pointer' }}>
                    启动数字人
                </button>
            </div>

            <div className="remote-container" style={{ width: '100%', height: '100%' }} />

            <div
                ref={subtitleRef}
                style={{
                    position: 'absolute',
                    bottom: 40,
                    left: '50%',
                    transform: 'translateX(-50%)',
                    padding: '12px 24px',
                    backgroundColor: 'rgba(255,255,255,0.85)',
                    borderRadius: 12,
                    visibility: 'hidden',
                    maxWidth: '80%',
                    textAlign: 'center',
                }}
            />
        </div>
    );
};

export default DuixDigitalHuman;