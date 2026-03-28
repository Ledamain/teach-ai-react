import { useState, useRef } from "react";
import Image from "next/image";
import { Geist, Geist_Mono } from "next/font/google";
import { Button, Form, Input, Typography } from "antd";
import styles from "../../styles/login/index.module.css";
import { useRouter } from "next/router";
import { login } from "@/api/login";
// 引入新的烟花组件
import { Fireworks } from '@fireworks-js/react'
import type { FireworksHandlers } from '@fireworks-js/react'
import Link from "next/link";

const { Title, Text } = Typography;

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

export default function Home() {
    const router = useRouter();
    // 控制登录成功的状态，用于切换界面显示
    const [isSuccess, setIsSuccess] = useState(false);
    // 用于持有烟花实例的 ref
    const fireworksRef = useRef<FireworksHandlers>(null);

    // 触发烟花效果的函数
    const fireFireworks = () => {
        if (fireworksRef.current) {
            // 检查实例是否存在并启动
            if (!fireworksRef.current.isRunning) {
                fireworksRef.current.start();
            }
        }
    };

    const handleFinish = async (values: { username: string; password: string }) => {
        try {
            // 1. 调用 API
            await login(values);

            // 2. 登录成功，改变状态
            setIsSuccess(true);

            // 3. 立即触发烟花特效
            // 注意：由于 Fireworks 组件在 !isSuccess 时未渲染，这里需要稍微延迟等待 DOM 更新
            setTimeout(() => {
                fireFireworks();
            }, 50);

            // 注意：您的注释说封装函数已弹成功提示，所以这里不再手动 message.success

            // 4. 延迟跳转，让用户看完动画 (烟花效果比较壮观，建议保持 3 秒左右)
            setTimeout(() => {
                router.push("/chat");
            }, 2000);

        } catch (error) {
            // 失败：封装函数/拦截器已弹错误提示
            console.log('登录失败：', error);
            setIsSuccess(false);
        }
    };

    return (
        <div className={`${geistSans.className} ${geistMono.className} ${styles.pageWrapper}`}>

            {/* 当登录成功时，渲染烟花组件，覆盖全屏 */}
            {isSuccess && (
                <Fireworks
                    ref={fireworksRef}
                    options={{
                        opacity: 0.9,
                        acceleration: 1.05,
                        friction: 0.98,
                        gravity: 1.5,
                        particles: 150,
                        traceLength: 3,
                        traceSpeed: 10,
                        explosion: 6, // 爆炸烈度
                        intensity: 30, // 强度
                        flickering: 50,
                        lineStyle: 'round',
                        hue: { min: 0, max: 360 }, // 全彩色
                        delay: { min: 30, max: 60 },
                        rocketsPoint: { min: 50, max: 50 }, // 火箭发射点在底部中间
                        lineWidth: { explosion: { min: 1, max: 3 }, trace: { min: 1, max: 2 } },
                        brightness: { min: 50, max: 80 },
                        decay: { min: 0.015, max: 0.03 },
                        mouse: { click: false, move: false, max: 1 } // 禁用鼠标交互
                    }}
                    style={{
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        position: 'fixed',
                        background: 'rgba(0, 0, 0, 0.1)', // 淡淡的遮罩感
                        zIndex: 999, // 保证在最顶层
                        pointerEvents: 'none' // 鼠标事件穿透
                    }}
                />
            )}

            <div className={styles.loginCard}>
                <div className={styles.header}>
                    {/* 去除了所有颜文字 */}
                    <Title level={2} className={styles.title}>
                        {isSuccess ? "登录成功" : "登录到 AI智学教学辅助平台"}
                    </Title>
                    <Text type="secondary">
                        {isSuccess ? "正在为您跳转到对话页面..." : "欢迎回来，请开启您的 AI 对话之旅"}
                    </Text>
                </div>

                {/* 登录成功时，隐藏表单 */}
                {!isSuccess && (
                    <Form
                        onFinish={handleFinish}
                        layout="vertical"
                        size="large"
                        className={styles.form}
                    >
                        <Form.Item
                            label="账号"
                            name='username'
                            rules={[{ required: true, message: "请输入账号" }]}
                        >
                            <Input placeholder="您的账号" className={styles.input} />
                        </Form.Item>

                        <Form.Item
                            label="密码"
                            name='password'
                            rules={[{ required: true, message: "请输入密码" }]}
                        >
                            <Input.Password placeholder="您的密码" className={styles.input} />
                        </Form.Item>

                        <Form.Item className={styles.buttonItem}>
                            <Button
                                type="primary"
                                htmlType="submit"
                                block
                                className={styles.submitBtn}
                            >
                                立即登录
                            </Button>
                            <span className={styles.footerText}>
                                没有账号？
                                <Link href="/register">立即注册</Link>
                            </span>
                        </Form.Item>
                    </Form>
                )}

                {/* 登录成功后的占位，保持卡片高度 */}
                {isSuccess && <div style={{ height: '210px' }}></div>}

                <div className={styles.footer}>
                    <Text className={styles.footerText}>© 2024 AI智学智能辅助平台. All rights reserved.</Text>
                </div>
            </div>
        </div>
    );
}
