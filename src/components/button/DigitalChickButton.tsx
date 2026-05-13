"use client"

import * as React from "react"
import { cn } from "@/lib/utils"

interface AvatarConnectButtonProps
    extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    isConnecting?: boolean
    isConnected?: boolean
}

export function AvatarConnectButton({
                                        className,
                                        isConnecting = false,
                                        isConnected = false,
                                        onClick,
                                        ...props
                                    }: AvatarConnectButtonProps) {
    return (
        <button
            onClick={onClick}
            disabled={isConnecting}
            className={cn(
                "group relative flex items-center justify-center",
                "size-14 rounded-full",
                "bg-[#10a37f] hover:bg-[#1a7f64]",
                "transition-all duration-300 ease-out",
                "shadow-[0_0_20px_rgba(16,163,127,0.4)]",
                "hover:shadow-[0_0_30px_rgba(16,163,127,0.6)]",
                "hover:scale-105 active:scale-95",
                "disabled:opacity-70 disabled:cursor-not-allowed",
                "outline-none focus-visible:ring-2 focus-visible:ring-[#10a37f] focus-visible:ring-offset-2 focus-visible:ring-offset-background",
                isConnected && "bg-[#1a7f64] shadow-[0_0_25px_rgba(16,163,127,0.5)]",
                className
            )}
            {...props}
        >
            {/* 外层脉冲光环动画 */}
            <span
                className={cn(
                    "absolute inset-0 rounded-full",
                    "bg-[#10a37f]/30",
                    "animate-ping",
                    isConnecting && "animate-ping",
                    !isConnecting && !isConnected && "animate-pulse opacity-50"
                )}
            />

            {/* 内层渐变光环 */}
            <span
                className={cn(
                    "absolute inset-[-3px] rounded-full",
                    "bg-gradient-to-r from-[#10a37f] via-[#1ed9a4] to-[#10a37f]",
                    "opacity-0 group-hover:opacity-100",
                    "transition-opacity duration-500",
                    "blur-sm",
                    isConnecting && "opacity-100 animate-spin-slow"
                )}
            />

            {/* 按钮主体背景 */}
            <span
                className={cn(
                    "absolute inset-0 rounded-full",
                    "bg-gradient-to-br from-[#10a37f] to-[#0d8c6d]",
                    "group-hover:from-[#1ed9a4] group-hover:to-[#10a37f]",
                    "transition-all duration-300"
                )}
            />

            {/* 图标容器 */}
            <span className="relative z-10 flex items-center justify-center">
        {isConnecting ? (
            // 连接中状态 - 旋转加载图标
            <svg
                className="size-6 animate-spin text-white"
                viewBox="0 0 24 24"
                fill="none"
            >
                <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="3"
                />
                <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                />
            </svg>
        ) : isConnected ? (
            // 已连接状态 - 波形图标
            <svg
                className="size-6 text-white"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            >
                <path className="animate-waveform-1" d="M12 4v16" />
                <path className="animate-waveform-2" d="M8 8v8" />
                <path className="animate-waveform-3" d="M16 8v8" />
                <path className="animate-waveform-4" d="M4 10v4" />
                <path className="animate-waveform-5" d="M20 10v4" />
            </svg>
        ) : (
            // 默认状态 - 数字人图标
            <svg
                className="size-6 text-white transition-transform duration-300 group-hover:scale-110"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            >
                {/* 头部 */}
                <circle cx="12" cy="8" r="4" />
                {/* 身体 */}
                <path d="M6 21v-2a4 4 0 014-4h4a4 4 0 014 4v2" />
                {/* AI 连接波纹 */}
                <path
                    className="opacity-70"
                    d="M18 8a6 6 0 00-12 0"
                    strokeDasharray="3 3"
                />
            </svg>
        )}
      </span>

            {/* 悬停时的闪光效果 */}
            <span
                className={cn(
                    "absolute inset-0 rounded-full",
                    "bg-gradient-to-tr from-white/20 to-transparent",
                    "opacity-0 group-hover:opacity-100",
                    "transition-opacity duration-300"
                )}
            />
        </button>
    )
}
