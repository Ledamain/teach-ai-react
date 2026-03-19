import React from 'react';
import type { AppProps } from 'next/app';

import '@/styles/globals.css'
import theme from '@/theme/ themeConfig';
import {XProvider} from "@ant-design/x";

const App = ({ Component, pageProps }: AppProps) => (
    <XProvider theme={theme}>
      <Component {...pageProps} />
    </XProvider>
);

export default App;