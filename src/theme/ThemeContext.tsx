import React, { createContext, useState, useContext, ReactNode } from 'react';
import type { ThemeConfig } from 'antd';
import { XProvider } from "@ant-design/x";
import defaultTheme from '@/theme/ themeConfig';

// 1. 定义 Context 的数据结构
interface ThemeContextType {
    themeState: ThemeConfig;
    updateTheme: (newTokens: Partial<ThemeConfig['token']>) => void;
}

// 2. 创建 Context（✅ 必须放在组件函数的外部！）
const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

// 3. 封装自定义的 Provider
export const AppThemeProvider = ({ children }: { children: ReactNode }) => {
    // 初始化 State
    const [themeState, setThemeState] = useState<ThemeConfig>(defaultTheme);

    // 定义更新主题的方法
    const updateTheme = (newTokens: Partial<ThemeConfig['token']>) => {
        setThemeState((prevTheme) => ({
            ...prevTheme,
            token: {
                ...prevTheme.token,
                ...newTokens,
            },
        }));
    };

    return (
        <ThemeContext.Provider value={{ themeState, updateTheme }}>
            <XProvider theme={themeState}>
                {children}
            </XProvider>
        </ThemeContext.Provider>
    );
};

// 4. 导出自定义 Hook
export const useAppTheme = () => {
    const context = useContext(ThemeContext);
    if (!context) {
        throw new Error('useAppTheme must be used within an AppThemeProvider');
    }
    return context;
};
