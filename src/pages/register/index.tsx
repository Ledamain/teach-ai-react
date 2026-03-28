import { useState } from "react";
import { Geist, Geist_Mono } from "next/font/google";
import { Button, Form, Input, Typography } from "antd";
import { useRouter } from "next/router";
import { register } from "@/api/login/index"; // 假设你有一个注册的 API
import styles from "../../styles/register/index.module.css";

const { Title, Text } = Typography;

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

export default function Register() {
    const router = useRouter();
    // 控制注册成功的状态，用于切换界面显示
    const [isSuccess, setIsSuccess] = useState(false);

    const handleFinish = async (values: any) => {
        try {
            // 1. 调用注册 API（剔除确认密码字段，避免传给后端）
            const { confirmPassword, ...registerData } = values;
            await register(registerData);

            // 2. 注册成功，改变状态，显示成功提示
            setIsSuccess(true);

            // 3. 延迟 2 秒跳转回登录页，给用户缓冲时间
            setTimeout(() => {
                router.push("/login");
            }, 1000);

        } catch (error) {
            // 失败：封装函数/拦截器已弹错误提示
            console.log('注册失败：', error);
            setIsSuccess(false);
        }
    };

    return (
        <div className={`${geistSans.className} ${geistMono.className} ${styles.pageWrapper}`}>

            <div className={styles.loginCard}>
                <div className={styles.header}>
                    <Title level={2} className={styles.title}>
                        {isSuccess ? "注册成功" : "加入 AI智学教学辅助平台"}
                    </Title>
                    <Text type="secondary">
                        {isSuccess ? "正在为您跳转到登录页面..." : "填写以下信息，开启您的 AI 学习之旅"}
                    </Text>
                </div>

                {/* 注册成功时，隐藏表单 */}
                {!isSuccess && (
                    <Form
                        onFinish={handleFinish}
                        layout="vertical"
                        size="large"
                        className={styles.form}
                    >
                        <Form.Item
                            label="昵称"
                            name='nickname'
                            rules={[{ required: true, message: "请输入您的昵称" }]}
                        >
                            <Input placeholder="怎么称呼您？" className={styles.input} />
                        </Form.Item>

                        <Form.Item
                            label="账号"
                            name='username'
                            rules={[{ required: true, message: "请输入账号" }]}
                        >
                            <Input placeholder="设置一个登录账号" className={styles.input} />
                        </Form.Item>

                        <Form.Item
                            label="密码"
                            name='password'
                            rules={[{ required: true, message: "请输入密码" }]}
                        >
                            <Input.Password placeholder="设置密码" className={styles.input} />
                        </Form.Item>

                        {/* 确认密码：实时比对 password 字段的值 */}
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

                        {/* 返回登录的引导链接 */}
                        <div style={{ textAlign: 'center', marginTop: '16px' }}>
                            <Text type="secondary" style={{ fontSize: '14px' }}>
                                已有账号？ <a onClick={() => router.push('/login')}>去登录</a>
                            </Text>
                        </div>
                    </Form>
                )}

                {/* 注册成功后的占位，保持卡片高度，避免视觉跳跃 */}
                {isSuccess && <div style={{ height: '380px' }}></div>}

                <div className={styles.footer}>
                    <Text className={styles.footerText}>© 2024 AI智学智能辅助平台. All rights reserved.</Text>
                </div>
            </div>
        </div>
    );
}
