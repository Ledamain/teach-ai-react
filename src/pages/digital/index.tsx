'use client';

import { useEffect, useRef, useState } from 'react';
import { Button, Spin, message } from 'antd';
import { PhoneOutlined, LoadingOutlined } from '@ant-design/icons';
import DigitalApi from '@/api/digital';
import {router} from "next/client";
import {DigitalHumanRespType} from "@/types/digital/DigitalHumanRespType";

// 移除全局 let，改用 Ref 存储实例
// let Duix: any = null;

const DigitalHumanPage = () => {
    // 状态管理
    const [isClient, setIsClient] = useState(false);
    const [loading, setLoading] = useState(true);
    const [callActive, setCallActive] = useState(false);
    const [sessionData, setSessionData] = useState<DigitalHumanRespType | null>(null);
    // 【新增】用于触发 React 重新渲染的状态
    const [sdkReady, setSdkReady] = useState(false);

    // Refs
    const duixInstanceRef = useRef<any>(null);
    const DuixClassRef = useRef<any>(null); // 【新增】用 Ref 存储 SDK 构造函数
    const subtitleRef = useRef<HTMLDivElement>(null);
    const containerRef = useRef<HTMLDivElement>(null);

    // --- 1. 初始化环境 ---
    useEffect(() => {
        setIsClient(true);
        // 动态加载浏览器端库
        import('duix-guiji-light').then((mod) => {
            console.log("SDK Loaded Successfully");
            DuixClassRef.current = mod.default;
            setSdkReady(true); // 【关键】设置状态，触发重渲染
        }).catch(err => {
            console.error("Failed to load SDK", err);
            message.error("SDK 加载失败");
            setLoading(false);
        });
    }, []);

    // --- 2. 当 SDK 和客户端准备好后，自动创建会话 ---
    useEffect(() => {
        // 增加调试日志
        console.log("Checking conditions: isClient=", isClient, "sdkReady=", sdkReady);

        if (!isClient || !sdkReady) return;

        const startSession = async () => {
            try {
                setLoading(true);
                console.log("Fetching session data...");
                // 调用后端 API 获取 Sign 和 ConversationId
                const resp = await DigitalApi.createDigitalHumanChat();
                console.log("API Response:", resp);
                setSessionData(resp);
                initDigitalHuman(resp);
            } catch (error) {
                message.error('加载数字人教师对话失败');
                console.error(error);
                setLoading(false);
            }
        };

        startSession();
    }, [isClient, sdkReady]); // 【修改】依赖 sdkReady 而不是 Duix 变量

    // --- 3. 初始化数字人逻辑 ---
    const initDigitalHuman = (data: DigitalHumanRespType) => {
        if (!containerRef.current || !DuixClassRef.current) return;

        try {
            const Duix = DuixClassRef.current;
            const duix = new Duix();
            duixInstanceRef.current = duix;

            // 事件监听
            duix.on('error', (err: any) => {
                console.error('Duix Error:', err);
                message.error('数字人教师服务出错');
                setLoading(false);
            });

            duix.on('initialSuccess', async () => {
                console.log('Digital Human Connected');
                setLoading(false);
                setCallActive(true);
                duix.start({ openAsr: true });
                await duix.answer({
                    question: "你是一名专业、耐心、温柔专业的AI 教学数字人老师，具备全科教学能力，擅长中小学及大学通识课程、知识点讲解、习题解析、答疑解惑、学习方法指导，全程以真人教师口吻自然对话，语气亲切稳重、逻辑清晰、通俗易懂。请用中文介绍一下你自己",
                    interrupt: true
                });
            });

            duix.on('show', () => {
                if (subtitleRef.current) subtitleRef.current.style.visibility = 'visible';
            });

            duix.on('asrStart', () => {
                if (subtitleRef.current) subtitleRef.current.innerText = '';
            });

            duix.on('speakSection', (data: { text: string }) => {
                if (subtitleRef.current) subtitleRef.current.innerText = data.text;
            });

            // 启动
            duix.init({
                sign: data.sign,
                containerLable: containerRef.current, // 注意：确认 SDK 参数是 Lable 还是 Label (拼写)
                conversationId: data.conversationId,
                platform: 'duix.com',
            });

            duix.createCamera();

        } catch (error) {
            console.error('Init failed:', error);
            setLoading(false);
        }
    };

    // --- 4. 挂断逻辑 ---
    const handleHangup = async () => {
        setCallActive(false);
        setLoading(true);

        if (duixInstanceRef.current) {
            // 如果 SDK 有关闭方法建议调用
            duixInstanceRef.current.destroyCamera();
            duixInstanceRef.current.stop();
            router.push('/chat')
        }

        if (sessionData) {
            try {
                // 这里也建议改成使用 DigitalApi
                // await DigitalApi.closeDigitalHumanChat(sessionData.sign);
                message.success('数字人教师问答结束');
            } catch (err) {
                console.error('Close session failed:', err);
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div style={{
            width: '100vw',
            height: '100vh',
            backgroundColor: '#000',
            position: 'relative',
            overflow: 'hidden',
            margin: 0,
            padding: 0
        }}>
            <div ref={containerRef} style={{ width: '100%', height: '100%' }} />

            {loading && (
                <div style={{
                    position: 'absolute',
                    top: 0, left: 0, right: 0, bottom: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    backgroundColor: 'rgba(0,0,0,0.9)',
                    zIndex: 100
                }}>
                    <Spin indicator={<LoadingOutlined style={{ fontSize: 48, color: '#10a37f' }} spin />} />
                    <div style={{ color: '#fff', marginTop: 20, fontFamily: 'sans-serif' }}>
                        加载数字人教师中...
                    </div>
                </div>
            )}

            <div
                ref={subtitleRef}
                style={{
                    position: 'absolute',
                    bottom: 100,
                    left: '50%',
                    transform: 'translateX(-50%)',
                    padding: '12px 24px',
                    backgroundColor: 'rgba(255,255,255,0.9)',
                    borderRadius: '24px',
                    visibility: 'hidden',
                    maxWidth: '60%',
                    textAlign: 'center',
                    color: '#111',
                    fontSize: '16px',
                    fontWeight: 500,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                }}
            />

            <div style={{
                position: 'absolute',
                bottom: 40,
                left: 0,
                right: 0,
                display: 'flex',
                justifyContent: 'center',
                zIndex: 50
            }}>
                <Button
                    shape="circle"
                    size="large"
                    icon={<PhoneOutlined style={{ fontSize: '24px', color: '#fff' }} />}
                    danger
                    style={{
                        width: 64,
                        height: 64,
                        backgroundColor: callActive ? '#ef4444' : '#666',
                        border: 'none',
                        boxShadow: '0 4px 12px rgba(239, 68, 68, 0.4)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}
                    onClick={handleHangup}
                    disabled={loading}
                />
            </div>
        </div>
    );
};

export default DigitalHumanPage;