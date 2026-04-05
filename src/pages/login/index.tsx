import { useState, useRef } from "react";
import Image from "next/image";
import { Geist, Geist_Mono } from "next/font/google";
import { Button, Form, Input, Typography } from "antd";
import { CheckOutlined } from "@ant-design/icons";
import styles from "../../styles/login/index.module.css";
import { useRouter } from "next/router";
import { login } from "@/api/login";
import { Fireworks } from '@fireworks-js/react'
import type { FireworksHandlers } from '@fireworks-js/react'
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";

const { Title, Text } = Typography;

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

// 动画变体
const containerVariants = {
    hidden: { opacity: 0, y: 20, scale: 0.98 },
    visible: {
        opacity: 1,
        y: 0,
        scale: 1,
        transition: {
            duration: 0.5,
            ease: [0.19, 1, 0.22, 1],
            staggerChildren: 0.08
        }
    },
    exit: {
        opacity: 0,
        y: -10,
        transition: { duration: 0.3 }
    }
};

const itemVariants = {
    hidden: { opacity: 0, y: 16 },
    visible: {
        opacity: 1,
        y: 0,
        transition: { duration: 0.4, ease: [0.19, 1, 0.22, 1] }
    }
};

const successVariants = {
    hidden: { opacity: 0, scale: 0.9 },
    visible: {
        opacity: 1,
        scale: 1,
        transition: {
            duration: 0.5,
            ease: [0.19, 1, 0.22, 1],
            staggerChildren: 0.1
        }
    }
};

const checkmarkVariants = {
    hidden: { scale: 0, rotate: -180 },
    visible: {
        scale: 1,
        rotate: 0,
        transition: {
            type: "spring",
            stiffness: 200,
            damping: 15,
            delay: 0.2
        }
    }
};

export default function Home() {
    const router = useRouter();
    const [isSuccess, setIsSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const fireworksRef = useRef<FireworksHandlers>(null);

    const fireFireworks = () => {
        if (fireworksRef.current) {
            if (!fireworksRef.current.isRunning) {
                fireworksRef.current.start();
            }
        }
    };

    const handleFinish = async (values: { username: string; password: string }) => {
        setLoading(true);
        try {
            await login(values);
            setIsSuccess(true);

            setTimeout(() => {
                fireFireworks();
            }, 50);

            setTimeout(() => {
                router.push("/chat");
            }, 2000);

        } catch (error) {
            console.log('登录失败：', error);
            setIsSuccess(false);
            setLoading(false);
        }
    };

    return (
        <div className={`${geistSans.className} ${geistMono.className} ${styles.pageWrapper}`}>

            {/* 烟花效果 */}
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
                        explosion: 6,
                        intensity: 30,
                        flickering: 50,
                        lineStyle: 'round',
                        hue: { min: 0, max: 360 },
                        delay: { min: 30, max: 60 },
                        rocketsPoint: { min: 50, max: 50 },
                        lineWidth: { explosion: { min: 1, max: 3 }, trace: { min: 1, max: 2 } },
                        brightness: { min: 50, max: 80 },
                        decay: { min: 0.015, max: 0.03 },
                        mouse: { click: false, move: false, max: 1 }
                    }}
                    style={{
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        position: 'fixed',
                        background: 'rgba(0, 0, 0, 0.1)',
                        zIndex: 999,
                        pointerEvents: 'none'
                    }}
                />
            )}

            <motion.div
                className={styles.loginCard}
                initial="hidden"
                animate="visible"
                variants={containerVariants}
            >
                <AnimatePresence mode="wait">
                    {!isSuccess ? (
                        <motion.div
                            key="login-form"
                            variants={containerVariants}
                            initial="hidden"
                            animate="visible"
                            exit="exit"
                        >
                            <motion.div className={styles.header} variants={itemVariants}>
                                <Title level={2} className={styles.title}>
                                    登录到 AI智学教学辅助平台
                                </Title>
                                <Text className={styles.subtitle}>
                                    欢迎回来，请开启您的 AI 对话之旅
                                </Text>
                            </motion.div>

                            <Form
                                onFinish={handleFinish}
                                layout="vertical"
                                size="large"
                                className={styles.form}
                            >
                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="账号"
                                        name='username'
                                        rules={[{ required: true, message: "请输入账号" }]}
                                    >
                                        <Input placeholder="您的账号" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="密码"
                                        name='password'
                                        rules={[{ required: true, message: "请输入密码" }]}
                                    >
                                        <Input.Password placeholder="您的密码" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item className={styles.buttonItem}>
                                        <Button
                                            type="primary"
                                            htmlType="submit"
                                            block
                                            className={styles.submitBtn}
                                            loading={loading}
                                        >
                                            立即登录
                                        </Button>
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants} className={styles.registerLink}>
                                    <span>没有账号？</span>
                                    <Link href="/register">立即注册</Link>
                                </motion.div>
                            </Form>
                        </motion.div>
                    ) : (
                        <motion.div
                            key="login-success"
                            className={styles.successWrapper}
                            variants={successVariants}
                            initial="hidden"
                            animate="visible"
                        >
                            <motion.div className={styles.successIcon} variants={checkmarkVariants}>
                                <CheckOutlined />
                            </motion.div>
                            <motion.div className={styles.successTitle} variants={itemVariants}>
                                登录成功
                            </motion.div>
                            <motion.div className={styles.successSubtitle} variants={itemVariants}>
                                正在为您跳转到对话页面...
                            </motion.div>
                        </motion.div>
                    )}
                </AnimatePresence>

                <motion.div className={styles.footer} variants={itemVariants}>
                    <Text className={styles.footerText}>© 2024 AI智学智能辅助平台. All rights reserved.</Text>
                </motion.div>
            </motion.div>
        </div>
    );
}
