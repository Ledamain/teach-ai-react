import React, { useEffect, useRef, useState, useCallback } from 'react';
import { Modal, message } from 'antd';
import { CloseOutlined, ReloadOutlined, CheckCircleFilled, CloseCircleFilled } from '@ant-design/icons';
import { getCaptcha, checkCaptcha } from '@/api/login'; // 替换为你的实际路径
import styles from '@/styles/captcha/index.module.css';

// AES 加密（需安装 crypto-js: npm install crypto-js）
import CryptoJS from 'crypto-js';

interface SliderCaptchaProps {
    open: boolean;
    onSuccess: (captchaVerification: string) => void;
    onClose: () => void;
}

interface CaptchaData {
    token: string;
    secretKey: string;
    originalImageBase64: string;
    jigsawImageBase64: string;
}

type VerifyStatus = 'default' | 'success' | 'error';

const SLIDER_WIDTH = 50;    // 滑块方块宽度
const CANVAS_WIDTH = 310;   // 验证码图片宽度
const CANVAS_HEIGHT = 155;  // 验证码图片高度

const SliderCaptcha: React.FC<SliderCaptchaProps> = ({ open, onSuccess, onClose }) => {
    const [captchaData, setCaptchaData] = useState<CaptchaData | null>(null);
    const [loading, setLoading] = useState(false);
    const [dragging, setDragging] = useState(false);
    const [sliderLeft, setSliderLeft] = useState(0);
    const [status, setStatus] = useState<VerifyStatus>('default');
    const [verifying, setVerifying] = useState(false);

    const startXRef = useRef(0);
    const sliderLeftRef = useRef(0);
    const trackRef = useRef<HTMLDivElement>(null);
    const maxSlide = CANVAS_WIDTH - SLIDER_WIDTH;

    // 获取验证码
    const fetchCaptcha = useCallback(async () => {
        setLoading(true);
        setSliderLeft(0);
        setStatus('default');
        try {
            const res = await getCaptcha({ captchaType: 'blockPuzzle' });
            const data = res?.data?.repData;
            if (data) {
                setCaptchaData({
                    token: data.token,
                    secretKey: data.secretKey,
                    originalImageBase64: data.originalImageBase64,
                    jigsawImageBase64: data.jigsawImageBase64,
                });
            }
        } catch {
            message.error('验证码加载失败，请重试');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (open) fetchCaptcha();
    }, [open, fetchCaptcha]);

    // 鼠标/触摸 按下
    const handleDragStart = (e: React.MouseEvent | React.TouchEvent) => {
        if (status !== 'default' || verifying) return;
        const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
        startXRef.current = clientX;
        sliderLeftRef.current = sliderLeft;
        setDragging(true);
    };

    // 鼠标/触摸 移动
    const handleDragMove = useCallback(
        (e: MouseEvent | TouchEvent) => {
            if (!dragging) return;
            const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
            const delta = clientX - startXRef.current;
            const newLeft = Math.min(Math.max(sliderLeftRef.current + delta, 0), maxSlide);
            setSliderLeft(newLeft);
        },
        [dragging, maxSlide],
    );

    // 鼠标/触摸 抬起
    const handleDragEnd = useCallback(async () => {
        if (!dragging) return;
        setDragging(false);
        if (!captchaData) return;

        setVerifying(true);
        try {
            // 计算坐标点（x 为滑动距离，按比例换算，y 固定居中）
            const point = { x: Math.round(sliderLeft), y: 5 };
            const pointJson = encryptPoint(point, captchaData.secretKey);

            const res = await checkCaptcha({
                captchaType: 'blockPuzzle',
                token: captchaData.token,
                pointJson,
            });

            const result = res?.data?.repData?.result;
            if (result) {
                setStatus('success');
                // 生成 captchaVerification
                const verificationStr = `${captchaData.secretKey}---${JSON.stringify(point)}`;
                const captchaVerification = CryptoJS.enc.Base64.stringify(
                    CryptoJS.enc.Utf8.parse(verificationStr),
                );
                setTimeout(() => {
                    onSuccess(captchaVerification);
                }, 600);
            } else {
                setStatus('error');
                setTimeout(() => fetchCaptcha(), 1000);
            }
        } catch {
            setStatus('error');
            setTimeout(() => fetchCaptcha(), 1000);
        } finally {
            setVerifying(false);
        }
    }, [dragging, captchaData, sliderLeft, fetchCaptcha, onSuccess]);

    useEffect(() => {
        if (dragging) {
            window.addEventListener('mousemove', handleDragMove);
            window.addEventListener('mouseup', handleDragEnd);
            window.addEventListener('touchmove', handleDragMove);
            window.addEventListener('touchend', handleDragEnd);
        }
        return () => {
            window.removeEventListener('mousemove', handleDragMove);
            window.removeEventListener('mouseup', handleDragEnd);
            window.removeEventListener('touchmove', handleDragMove);
            window.removeEventListener('touchend', handleDragEnd);
        };
    }, [dragging, handleDragMove, handleDragEnd]);

    // AES 加密坐标
    const encryptPoint = (point: { x: number; y: number }, secretKey: string): string => {
        const key = CryptoJS.enc.Utf8.parse(secretKey);
        const encrypted = CryptoJS.AES.encrypt(JSON.stringify(point), key, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7,
        });
        return encrypted.toString();
    };

    const trackWidth = sliderLeft + SLIDER_WIDTH;

    const statusColorMap: Record<VerifyStatus, string> = {
        default: '',
        success: styles.trackSuccess,
        error: styles.trackError,
    };

    return (
        <Modal
            open={open}
            onCancel={onClose}
            footer={null}
            width={380}
            centered
            closable={false}
            className={styles.modal}
            styles={{ body: { padding: 0 } }}
        >
            <div className={styles.container}>
                {/* Header */}
                <div className={styles.header}>
                    <span className={styles.title}>安全验证</span>
                    <div className={styles.headerActions}>
                        <ReloadOutlined className={styles.iconBtn} onClick={fetchCaptcha} title="刷新" />
                        <CloseOutlined className={styles.iconBtn} onClick={onClose} title="关闭" />
                    </div>
                </div>

                {/* 图片区域 */}
                <div className={styles.imageWrap}>
                    {loading ? (
                        <div className={styles.loadingBox}>加载中...</div>
                    ) : captchaData ? (
                        <>
                            {/* 背景图 */}
                            <img
                                src={`data:image/png;base64,${captchaData.originalImageBase64}`}
                                alt="captcha"
                                className={styles.bgImage}
                                draggable={false}
                            />
                            {/* 滑块拼图 */}
                            <img
                                src={`data:image/png;base64,${captchaData.jigsawImageBase64}`}
                                alt="jigsaw"
                                className={styles.jigsawImage}
                                style={{ left: sliderLeft }}
                                draggable={false}
                            />
                        </>
                    ) : null}
                </div>

                {/* 滑动轨道 */}
                <div className={styles.sliderArea}>
                    <div className={styles.track}>
                        {/* 填充条 */}
                        <div
                            className={`${styles.trackFill} ${statusColorMap[status]}`}
                            style={{ width: trackWidth }}
                        />
                        {/* 提示文字 */}
                        {status === 'default' && sliderLeft === 0 && (
                            <span className={styles.trackTip}>向右滑动完成拼图</span>
                        )}
                        {status === 'success' && (
                            <span className={styles.statusTip}>
                <CheckCircleFilled style={{ color: '#52c41a', marginRight: 4 }} />
                验证成功
              </span>
                        )}
                        {status === 'error' && (
                            <span className={styles.statusTip}>
                <CloseCircleFilled style={{ color: '#ff4d4f', marginRight: 4 }} />
                验证失败，请重试
              </span>
                        )}
                        {/* 滑块按钮 */}
                        <div
                            className={`${styles.sliderBtn} ${dragging ? styles.sliderBtnActive : ''} ${
                                status === 'success' ? styles.sliderBtnSuccess : ''
                            } ${status === 'error' ? styles.sliderBtnError : ''}`}
                            style={{ left: sliderLeft }}
                            onMouseDown={handleDragStart}
                            onTouchStart={handleDragStart}
                        >
                            {status === 'success' ? (
                                <CheckCircleFilled style={{ color: '#52c41a', fontSize: 20 }} />
                            ) : status === 'error' ? (
                                <CloseCircleFilled style={{ color: '#ff4d4f', fontSize: 20 }} />
                            ) : (
                                <span className={styles.sliderArrow}>{'›'}</span>
                            )}
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div className={styles.footer}>
                    <span className={styles.footerTip}>拖动左侧滑块，完成上方拼图</span>
                </div>
            </div>
        </Modal>
    );
};

export default SliderCaptcha;