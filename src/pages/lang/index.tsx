import { Sender } from '@ant-design/x';
import {App, Button, Upload, UploadProps} from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import {UploadOutlined} from "@ant-design/icons";

const Demo: React.FC = () => {
    const { message } = App.useApp();
    const [recording, setRecording] = useState(false);
    const [value, setValue] = useState('');
    const [mounted, setMounted] = useState(false); // 解决 Hydration 问题

    const SpeechRecognitionRef = useRef<any>(null);
    const recognitionInstance = useRef<any>(null);

    // 确保只在客户端渲染
    useEffect(() => {
        setMounted(true);
        const GlobalSpeechRecognition =
            (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

        if (GlobalSpeechRecognition) {
            SpeechRecognitionRef.current = GlobalSpeechRecognition;
        }
    }, []);

    const handleRecordingChange = (nextRecording: boolean) => {
        if (!SpeechRecognitionRef.current) {
            message.error('当前浏览器不支持语音识别 (建议使用 Chrome/Edge)');
            return;
        }

        if (nextRecording) {
            const rec = new SpeechRecognitionRef.current();
            rec.lang = 'zh-CN';
            rec.continuous = true;
            rec.interimResults = true;

            rec.onresult = (event: any) => {
                let currentText = '';
                for (let i = event.resultIndex; i < event.results.length; ++i) {
                    // 【关键修复】使用 is 而不是 is
                    currentText += event.results[i][0].transcript;
                }
                console.log('收到识别结果:', currentText);
                setValue(currentText);
            };

            rec.onerror = (e: any) => {
                console.error('语音识别错误:', e.error);
                setRecording(false);
            };

            rec.onend = () => {
                setRecording(false);
            };

            try {
                rec.start();
                recognitionInstance.current = rec;
                setRecording(true);
            } catch (err) {
                console.error('启动失败:', err);
            }
        } else {
            if (recognitionInstance.current) {
                recognitionInstance.current.stop();
            }
            setRecording(false);
        }
    };

    // 如果未挂载，返回空或者占位符，避免 SSR 冲突
    if (!mounted) return null;

    const props: UploadProps = {
        name: 'file',
        action: `${process.env.NEXT_PUBLIC_API_URL_LOC}/admin-api/infra/file/upload`,
        headers: {
            authorization: 'authorization-text',
        },
        onChange(info) {
            if (info.file.status !== 'uploading') {
                console.log(info.file, info.fileList);
            }
            if (info.file.status === 'done') {
                message.success(`${info.file.name} file uploaded successfully`);
            } else if (info.file.status === 'error') {
                message.error(`${info.file.name} file upload failed.`);
            }
        },
    };

    return (
        <>
            <Sender
                value={value}
                onChange={setValue}
                allowSpeech={{
                    recording,
                    onRecordingChange: handleRecordingChange,
                }}
            />
            <Upload {...props}>
                <Button icon={<UploadOutlined />}>Click to Upload</Button>
            </Upload>
        </>
    );
};

export default () => (
    <App>
        <Demo />
    </App>
);
