import React from 'react';
import type { AppProps } from 'next/app';

import '@/styles/globals.css'
import theme from '@/theme/ themeConfig';
import {XProvider} from "@ant-design/x";
import AuthGuard from "@/components/AuthGuard";
import { AppThemeProvider } from '@/theme/ThemeContext';
import Head from 'next/head';
import Script from "next/script";

const App = ({ Component, pageProps }: AppProps) => (
    <AppThemeProvider>
        <Head>
            <title>AI智学</title>
            <meta name="application-name" content="AI智学" />
        </Head>
        <Script
            src="https://quanmiao-public.oss-cn-beijing.aliyuncs.com/quanmiao-sdk/v1.0.0/index.js"
            strategy="lazyOnload"
        />
        <AuthGuard>
            <Component {...pageProps} />
        </AuthGuard>
    </AppThemeProvider>
);

export default App;