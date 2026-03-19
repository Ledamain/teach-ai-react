import Image from "next/image";
import { Geist, Geist_Mono } from "next/font/google";
import {Button, Form, Input, message} from "antd";
import styles from "../../styles/login/index.module.css"
import {useRouter} from "next/router";
import {login} from "@/api/login";

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
  const handleFinish = async (values: {username: string,password: string}) => {
    try {
      // 登录成功：封装函数已弹成功提示，直接跳转
      await login(values);
      await router.push("/chat");
    } catch (error) {
      // 失败：封装函数/拦截器已弹错误提示，这里无需额外处理（避免重复弹）
      console.log('登录失败：', error);
    }
  };

  return (
    <div
      className={`${geistSans.className} ${geistMono.className} ${styles.container}`}
    >
      <h1 className={styles.title}>登录到BullGPT</h1>
        <Form onFinish={handleFinish}>
          <Form.Item label="账号" name='username' rules={[{ required: true, message: "请输入账号" }]}>
            <Input placeholder="请输入账号" />
          </Form.Item>
          <Form.Item label="密码" name='password' rules={[{ required: true, message: "请输入密码" }]}>
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" className={styles.btn}>登录</Button>
          </Form.Item>
        </Form>
    </div>
  );
}
