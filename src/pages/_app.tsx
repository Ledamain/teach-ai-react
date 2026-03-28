import React from 'react';
import type { AppProps } from 'next/app';

import '@/styles/globals.css'
import theme from '@/theme/ themeConfig';
import {XProvider} from "@ant-design/x";
import AuthGuard from "@/pages/components/AuthGuard";
import { AppThemeProvider } from '@/theme/ThemeContext';

const App = ({ Component, pageProps }: AppProps) => (
    <AppThemeProvider>
        <AuthGuard>
            <Component {...pageProps} />
        </AuthGuard>
    </AppThemeProvider>
);

export default App;