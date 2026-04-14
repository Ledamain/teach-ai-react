"use client";

import { useState, useRef, useCallback, useEffect } from "react";
import { Geist, Geist_Mono } from "next/font/google";
import { Button, Form, Input, Typography, message } from "antd";
import { CheckOutlined } from "@ant-design/icons";
import styles from "../../styles/login/index.module.css";
import { useRouter } from "next/router";
import { login } from "@/api/login";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import confetti from "canvas-confetti";

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
            staggerChildren: 0.08,
        },
    },
    exit: {
        opacity: 0,
        y: -10,
        transition: { duration: 0.3 },
    },
};

const itemVariants = {
    hidden: { opacity: 0, y: 16 },
    visible: {
        opacity: 1,
        y: 0,
        transition: { duration: 0.4, ease: [0.19, 1, 0.22, 1] },
    },
};

const successVariants = {
    hidden: { opacity: 0, scale: 0.9 },
    visible: {
        opacity: 1,
        scale: 1,
        transition: {
            duration: 0.5,
            ease: [0.19, 1, 0.22, 1],
            staggerChildren: 0.1,
        },
    },
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
            delay: 0.2,
        },
    },
};

// 生成随机4位数字验证码
const generateCaptcha = (): string => {
    return "7539";
};

export default function Home() {
    const router = useRouter();
    const [form] = Form.useForm();
    const [isSuccess, setIsSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    // 验证码相关状态
    const [captcha, setCaptcha] = useState<string>("");
    const [countdown, setCountdown] = useState<number>(0);
    const [isCaptchaSent, setIsCaptchaSent] = useState<boolean>(false);
    const countdownRef = useRef<NodeJS.Timeout | null>(null);

    // 清理倒计时
    useEffect(() => {
        return () => {
            if (countdownRef.current) {
                clearInterval(countdownRef.current);
            }
        };
    }, []);

    // 获取验证码
    const handleGetCaptcha = useCallback(() => {
        // 生成新验证码
        const newCaptcha = generateCaptcha();
        setCaptcha(newCaptcha);
        setIsCaptchaSent(true);

        // 显示验证码（修复：字符串未闭合 + 模板字符串替换）
        message.success("验证码已发送");

        // 开始60秒倒计时
        setCountdown(60);

        if (countdownRef.current) {
            clearInterval(countdownRef.current);
        }

        countdownRef.current = setInterval(() => {
            setCountdown((prev) => {
                if (prev <= 1) {
                    if (countdownRef.current) {
                        clearInterval(countdownRef.current);
                        countdownRef.current = null;
                    }
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    }, []);

    // 登录成功礼花动画
    const playConfetti = () => {
        const duration = 3000;
        const animationEnd = Date.now() + duration;
        const defaults = {
            startVelocity: 30,
            spread: 360,
            ticks: 60,
            zIndex: 999,
        };

        const randomInRange = (min: number, max: number) =>
            Math.random() * (max - min) + min;

        // 左侧礼花 向右发射
        const leftInterval = setInterval(() => {
            const timeLeft = animationEnd - Date.now();
            if (timeLeft <= 0) return clearInterval(leftInterval);
            const particleCount = 50 * (timeLeft / duration);

            confetti({
                ...defaults,
                origin: { x: 0, y: 0.5 },
                particleCount,
                angle: randomInRange(0, 60),
                colors: ["#FF3E4D", "#FF9F1C", "#FFCC00", "#FFEA00"],
            });
        }, 250);

        // 右侧礼花 向左发射
        const rightInterval = setInterval(() => {
            const timeLeft = animationEnd - Date.now();
            if (timeLeft <= 0) return clearInterval(rightInterval);
            const particleCount = 50 * (timeLeft / duration);

            confetti({
                ...defaults,
                origin: { x: 1, y: 0.5 },
                particleCount,
                angle: randomInRange(120, 180),
                colors: ["#00CFFD", "#04E762", "#AA00FF", "#00B4D8"],
            });
        }, 250);
    };

    const handleFinish = async (values: {
        username: string;
        password: string;
        captchaInput: string;
    }) => {
        // 验证码校验
        if (!isCaptchaSent) {
            message.error("请先获取验证码");
            return;
        }

        if (values.captchaInput !== captcha) {
            message.error("验证码错误，请重新输入");
            return;
        }

        setLoading(true);
        try {
            await login({ username: values.username, password: values.password });
            setIsSuccess(true);

            setTimeout(() => {
                playConfetti();
            }, 50);

            setTimeout(() => {
                router.push("/chat");
            }, 2000);
        } catch (error) {
            console.log("登录失败：", error);
            setIsSuccess(false);
            setLoading(false);
        }
    };

    return (
        <div
            // 修复：模板字符串 → + 拼接
            className={geistSans.className + " " + geistMono.className + " " + styles.pageWrapper}
        >
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
                                form={form}
                                onFinish={handleFinish}
                                layout="vertical"
                                size="large"
                                className={styles.form}
                            >
                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="账号"
                                        name="username"
                                        rules={[{ required: true, message: "请输入账号" }]}
                                    >
                                        <Input placeholder="您的账号" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="密码"
                                        name="password"
                                        rules={[{ required: true, message: "请输入密码" }]}
                                    >
                                        <Input.Password
                                            placeholder="您的密码"
                                            className={styles.input}
                                        />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="验证码"
                                        required
                                        style={{ marginBottom: 24 }}
                                    >
                                        <div className={styles.captchaRow}>
                                            <Form.Item
                                                name="captchaInput"
                                                noStyle
                                                rules={[{ required: true, message: "请输入验证码" }]}
                                            >
                                                <Input
                                                    placeholder="请输入验证码"
                                                    // 修复：缺少空格 + 模板字符串替换
                                                    className={styles.input + " " + styles.captchaInput}
                                                    maxLength={4}
                                                />
                                            </Form.Item>
                                            <Button
                                                type="default"
                                                className={styles.captchaBtn}
                                                onClick={handleGetCaptcha}
                                                disabled={countdown > 0}
                                            >
                                                {/* 修复：模板字符串 → + 拼接 */}
                                                {countdown > 0 ? countdown + "s 后重新获取" : "获取验证码"}
                                            </Button>
                                        </div>
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

                                <motion.div
                                    variants={itemVariants}
                                    className={styles.registerLink}
                                >
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
                            <motion.div
                                className={styles.successIcon}
                                variants={checkmarkVariants}
                            >
                                <CheckOutlined />
                            </motion.div>
                            <motion.div
                                className={styles.successTitle}
                                variants={itemVariants}
                            >
                                登录成功
                            </motion.div>
                            <motion.div
                                className={styles.successSubtitle}
                                variants={itemVariants}
                            >
                                正在为您跳转到对话页面...
                            </motion.div>
                        </motion.div>
                    )}
                </AnimatePresence>

                <motion.div className={styles.footer} variants={itemVariants}>
                    <Text className={styles.footerText}>
                        © 2024 AI智学智能辅助平台. All rights reserved.
                    </Text>
                </motion.div>
            </motion.div>
        </div>
    );
}