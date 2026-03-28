import React, { useState, useEffect } from "react";
import { Geist, Geist_Mono } from "next/font/google";
import {
    Button,
    Form,
    Input,
    Typography,
    Switch,
    Select,
    Divider,
    Avatar,
    message,
    Slider,
    Tag,
    ColorPicker
} from "antd";
import { motion, AnimatePresence } from "framer-motion";
import styles from "../../styles/settings/index.module.css";
import { useRouter } from "next/router";
import {UserOutlined} from "@ant-design/icons";
import {UserInfo} from "@/types/login/LoginType";
import { BgColorsOutlined } from '@ant-design/icons'; // 引入一个好看的图标（可选）
import { useAppTheme } from '@/theme/ThemeContext';

const { Title, Text } = Typography;

const geistSans = Geist({ variable: "--font-geist-sans", subsets: ["latin"] });
const geistMono = Geist_Mono({ variable: "--font-geist-mono", subsets: ["latin"] });

type TabKey = 'general' | 'profile' | 'security' | 'data';

export default function Settings() {
    const router = useRouter();
    const [form] = Form.useForm();
    const [activeTab, setActiveTab] = useState<TabKey>('general');
    const [isLoaded, setIsLoaded] = useState(false);
    const { themeState, updateTheme } = useAppTheme();


    useEffect(() => {
        setIsLoaded(true);
    }, []);

    // 容器进入动画
    const containerVariants = {
        hidden: { opacity: 0, scale: 0.98 },
        visible: {
            opacity: 1,
            scale: 1,
            transition: { duration: 0.5, ease: [0.19, 1, 0.22, 1] }
        }
    };

    const [mounted, setMounted] = useState(false);

    const [userInfo, setUserInfo] = useState<UserInfo | null>(null);

    const getUserInfo = () => {
        if (typeof window === 'undefined') {
            return null; // 返回 null 而不是 undefined，方便后续判断
        }

        const userInfoStr = window.localStorage.getItem('userInfo');
        // 增加空值判断
        if (!userInfoStr) return null;

        try {
            // 解析 JSON 并返回对象
            return JSON.parse(userInfoStr);
        } catch (e) {
            console.error('用户信息解析失败', e);
            return null;
        }
    }

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (mounted) {
            const info = getUserInfo();
            setUserInfo(info);

            // 当获取到用户信息后，手动同步到表单
            if (info) {
                form.setFieldsValue({
                    nickname: info.nickname || '未登录用户',
                    // 如果后续有 bio 等字段也可以在这里设置
                    // bio: info.bio
                });
            }
        }
    }, [mounted, form]);

    const renderContent = () => {
        return (
            <AnimatePresence mode="wait">
                <motion.div
                    key={activeTab}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    transition={{ duration: 0.3 }}
                >
                    {activeTab === 'general' && (
                        <div className={styles.section}>
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>主题模式</Text>
                                    <Text type="secondary" className={styles.desc}>为系统选择深色、浅色或跟随系统的外观。</Text>
                                </div>
                                <Select defaultValue="system" style={{ width: 160 }}>
                                    <Select.Option value="light">浅色模式</Select.Option>
                                    <Select.Option value="dark">深色模式</Select.Option>
                                    <Select.Option value="system">跟随系统</Select.Option>
                                </Select>
                            </div>
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>组件主题色</Text>
                                    <Text type="secondary" className={styles.desc}>为系统组件选择自定义的主题配色。</Text>
                                </div>

                                {/* 使用 ColorPicker 包裹 Button，实现点击按钮弹出调色盘 */}
                                <ColorPicker
                                    value={themeState.token?.colorPrimary}
                                    onChange={(color, hex) => updateTheme({ colorPrimary: hex })}
                                >
                                    <Button icon={<BgColorsOutlined />}>
                                        自定义配色
                                    </Button>
                                </ColorPicker>
                            </div>
                            <Divider />
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>测试版功能</Text>
                                    <Text type="secondary" className={styles.desc}>抢先体验最新的 AI 模型和实验性界面特性，这些功能可能还在开发中。</Text>
                                </div>
                                <Switch defaultChecked />
                            </div>
                            <Divider />
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>回复速度限制</Text>
                                    <Text type="secondary" className={styles.desc}>控制对话生成的流式响应速度，较低的值可以减少视觉闪烁。</Text>
                                </div>
                                <Slider defaultValue={80} style={{ width: 200 }} />
                            </div>
                        </div>
                    )}

                    {activeTab === 'profile' && (
                        <Form form={form} layout="vertical" onFinish={() => message.success("同步成功")} className={styles.form}>
                            <div className={styles.avatarSection}>
                                <Avatar
                                    className={styles.avatar}
                                    src={userInfo?.clientAvator || ''}
                                    alt="用户头像"
                                    size="large"
                                    fallback={<UserOutlined />}
                                />
                                <div className={styles.avatarActions}>
                                    <Button type="primary">上传头像</Button>
                                    <Button type="text">重置</Button>
                                </div>
                            </div>
                            <Form.Item label="用户昵称" name="nickname">
                                <Input className={styles.input} />
                            </Form.Item>
                            <Form.Item label="个人签名" name="bio">
                                <Input.TextArea placeholder="写点什么来介绍自己..." rows={4} className={styles.input} />
                            </Form.Item>
                            <Button type="primary" htmlType="submit" className={styles.saveBtn} size="large">保存资料</Button>
                        </Form>
                    )}

                    {activeTab === 'security' && (
                        <div className={styles.section}>
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>两步验证</Text>
                                    <Text type="secondary" className={styles.desc}>通过手机 App 获取验证码，为账号多加一层防护。</Text>
                                </div>
                                <Button>立即配置</Button>
                            </div>
                            <Divider />
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>活跃设备</Text>
                                    <div style={{ marginTop: 8 }}>
                                        <Tag color="blue">当前设备: Chrome / macOS</Tag>
                                    </div>
                                </div>
                                <Button danger type="text">全部登出</Button>
                            </div>
                        </div>
                    )}

                    {activeTab === 'data' && (
                        <div className={styles.section}>
                            <div className={styles.item}>
                                <div className={styles.info}>
                                    <Text className={styles.label}>训练反馈</Text>
                                    <Text type="secondary" className={styles.desc}>允许我们将您的匿名对话用于提升模型准确度。</Text>
                                </div>
                                <Switch defaultChecked />
                            </div>
                            <Divider />
                            <div className={styles.dangerZone}>
                                <Title level={5} type="danger">数据操作</Title>
                                <div className={styles.item}>
                                    <div className={styles.info}>
                                        <Text className={styles.label}>清空所有会话</Text>
                                        <Text type="secondary">这将永久删除您过去所有的聊天记录。</Text>
                                    </div>
                                    <Button danger ghost>清空</Button>
                                </div>
                            </div>
                        </div>
                    )}
                </motion.div>
            </AnimatePresence>
        );
    };

    return (
        <div className={`${geistSans.className} ${geistMono.className} ${styles.pageWrapper}`}>
            <motion.div
                className={styles.container}
                initial="hidden"
                animate={isLoaded ? "visible" : "hidden"}
                variants={containerVariants}
            >
                <aside className={styles.sidebar}>
                    <Title level={4} className={styles.navTitle}>设置中心</Title>
                    <nav className={styles.nav}>
                        {[
                            { key: 'general', label: '通用' },
                            { key: 'profile', label: '个人资料' },
                            { key: 'security', label: '账号安全' },
                            { key: 'data', label: '数据管理' }
                        ].map((item) => (
                            <div
                                key={item.key}
                                className={`${styles.navItem} ${activeTab === item.key ? styles.active : ''}`}
                                onClick={() => setActiveTab(item.key as TabKey)}
                            >
                                {item.label}
                            </div>
                        ))}
                    </nav>
                    <div className={styles.sidebarFooter}>
                        <Button type="text" onClick={() => router.push("/chat")} className={styles.backBtn} block>
                            ← 返回对话
                        </Button>
                    </div>
                </aside>

                <main className={styles.mainContent}>
                    <header className={styles.header}>
                        <Title level={3}>
                            {activeTab === 'general' && '通用设置'}
                            {activeTab === 'profile' && '个人资料'}
                            {activeTab === 'security' && '账户安全'}
                            {activeTab === 'data' && '数据管理'}
                        </Title>
                    </header>
                    <div className={styles.contentBody}>
                        {renderContent()}
                    </div>
                </main>
            </motion.div>
        </div>
    );
}
