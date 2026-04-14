import { useEffect, useState } from 'react';
import { useRouter } from 'next/router'; // 注意：Pages Router 使用 next/router
import { message } from 'antd';

export default function AuthGuard({ children }: { children: React.ReactNode }) {
    const router = useRouter();
    const [isAuthorized, setIsAuthorized] = useState(false);

    useEffect(() => {
        // 1. 定义白名单，不需要登录就能访问的页面
        const whiteList = ['/login', '/register'];

        // 如果当前路径在白名单内，直接放行
        if (whiteList.includes(router.pathname)) {
            setIsAuthorized(true);
            return;
        }

        // 2. 检查本地存储的登录凭证
        const userInfo = typeof window !== 'undefined' ? window.localStorage.getItem('userInfo') : null;

        if (!userInfo) {
            // 没找到 userInfo，拦截并跳转
            message.warning('您还未登录，请先登录！');
            router.replace('/login');
        } else {
            // 找到了，允许访问
            setIsAuthorized(true);
        }

        // 当路由发生变化时，会重新触发这个 useEffect 进行检查
    }, [router.pathname]);

    // 3. 在鉴权完成前不渲染页面内容，防止页面闪烁
    if (!isAuthorized) {
        return null; // 这里也可以放一个全局的 <Spin /> 加载动画
    }

    return <>{children}</>;
}
