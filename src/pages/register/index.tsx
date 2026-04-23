import { useState } from "react";
import { Geist, Geist_Mono } from "next/font/google";
import { Button, Form, Input, Typography } from "antd";
import { CheckOutlined } from "@ant-design/icons";
import { useRouter } from "next/router";
import { register } from "@/api/login/index";
import styles from "../../styles/register/index.module.css";
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
            staggerChildren: 0.06
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

export default function Register() {
    const router = useRouter();
    const [isSuccess, setIsSuccess] = useState(false);

    const handleFinish = async (values: any) => {
        try {
            const { confirmPassword, ...registerData } = values;
            await register(registerData);

            setIsSuccess(true);

            setTimeout(() => {
                router.push("/login");
            }, 1000);

        } catch (error) {
            console.log('注册失败：', error);
            setIsSuccess(false);
        }
    };

    return (
        <div className={`${geistSans.className} ${geistMono.className} ${styles.pageWrapper}`}>

            <motion.div
                className={styles.loginCard}
                initial="hidden"
                animate="visible"
                variants={containerVariants}
            >
                <AnimatePresence mode="wait">
                    {!isSuccess ? (
                        <motion.div
                            key="register-form"
                            variants={containerVariants}
                            initial="hidden"
                            animate="visible"
                            exit="exit"
                        >
                            <motion.div className={styles.header} variants={itemVariants}>
                                <Title level={2} className={styles.title}>
                                    加入 智汇伴学教学辅助平台
                                </Title>
                                <Text className={styles.subtitle}>
                                    填写以下信息，开启您的 AI 学习之旅
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
                                        label="昵称"
                                        name='nickname'
                                        rules={[{ required: true, message: "请输入您的昵称" }]}
                                    >
                                        <Input placeholder="怎么称呼您？" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="账号"
                                        name='username'
                                        rules={[{ required: true, message: "请输入账号" }]}
                                    >
                                        <Input placeholder="设置一个登录账号" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="密码"
                                        name='password'
                                        rules={[{ required: true, message: "请输入密码" }]}
                                    >
                                        <Input.Password placeholder="设置密码" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item
                                        label="确认密码"
                                        name='confirmPassword'
                                        dependencies={['password']}
                                        rules={[
                                            { required: true, message: "请再次输入密码" },
                                            ({ getFieldValue }) => ({
                                                validator(_, value) {
                                                    if (!value || getFieldValue('password') === value) {
                                                        return Promise.resolve();
                                                    }
                                                    return Promise.reject(new Error('两次输入的密码不一致！'));
                                                },
                                            }),
                                        ]}
                                    >
                                        <Input.Password placeholder="请再次输入密码" className={styles.input} />
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants}>
                                    <Form.Item className={styles.buttonItem}>
                                        <Button
                                            type="primary"
                                            htmlType="submit"
                                            block
                                            className={styles.submitBtn}
                                        >
                                            立即注册
                                        </Button>
                                    </Form.Item>
                                </motion.div>

                                <motion.div variants={itemVariants} className={styles.loginLink}>
                                    <span>已有账号？</span>
                                    <a onClick={() => router.push('/login')}>去登录</a>
                                </motion.div>
                            </Form>
                        </motion.div>
                    ) : (
                        <motion.div
                            key="register-success"
                            className={styles.successWrapper}
                            variants={successVariants}
                            initial="hidden"
                            animate="visible"
                        >
                            <motion.div className={styles.successIcon} variants={checkmarkVariants}>
                                <CheckOutlined />
                            </motion.div>
                            <motion.div className={styles.successTitle} variants={itemVariants}>
                                注册成功
                            </motion.div>
                            <motion.div className={styles.successSubtitle} variants={itemVariants}>
                                正在为您跳转到登录页面...
                            </motion.div>
                        </motion.div>
                    )}
                </AnimatePresence>

                <motion.div className={styles.footer} variants={itemVariants}>
                    <Text className={styles.footerText}> 2026 智汇伴学智能辅助平台.</Text>
                </motion.div>
            </motion.div>
        </div>
    );
}
