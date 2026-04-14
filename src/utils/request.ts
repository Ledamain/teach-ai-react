import axios, {
    AxiosInstance,
    InternalAxiosRequestConfig,
    AxiosResponse,
    AxiosError,
    AxiosRequestHeaders
} from 'axios';
import { message } from 'antd';
import Router from 'next/router';
import { isBrowser } from './env';

// 创建axios实例
const service: AxiosInstance = axios.create({
    baseURL: '/client-api',
    timeout: 50000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    } as AxiosRequestHeaders
});

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig<any>): InternalAxiosRequestConfig<any> => {
        if (isBrowser) {
            const token = localStorage.getItem('token');
            if (token) {
                config.headers['Client-Authorization'] = `${token}`;
            }
        }
        return config;
    },
    (error: AxiosError): Promise<never> => {
        console.error('请求错误:', error);
        message.error('请求初始化失败');
        return Promise.reject(error);
    }
);

// 响应拦截器（已修复：只提示，不抛错）
service.interceptors.response.use(
    (response: AxiosResponse): any => {
        const res = response.data;
        if (res.code === 0) {
            return res.data || res;
        } else {
            message.error(res.msg || '请求失败');
            return Promise.reject(res); // 不触发页面崩溃
        }
    },
    (error: AxiosError): Promise<never> => {
        console.error('响应错误:', error);

        // 超时处理
        if (axios.isAxiosError(error) && error.code === 'ECONNABORTED') {
            message.error('请求超时，请稍后重试');
            return Promise.reject(error);
        }

        if (error.response) {
            const status = error.response.status;
            const errMsg = (error.response.data as Record<string, any>)?.msg || '请求失败';

            switch (status) {
                case 401:
                    message.error('未授权，请重新登录');
                    if (isBrowser) {
                        localStorage.removeItem('token');
                        localStorage.removeItem('userInfo');
                        Router.push('/login').then(() => window.location.reload());
                    }
                    break;
                case 403:
                    message.error('拒绝访问，无权限操作');
                    break;
                case 404:
                    message.error('请求地址不存在');
                    break;
                case 500:
                    message.error('服务器内部错误，请稍后再试');
                    break;
                default:
                    message.error(errMsg);
            }
        } else if (error.request) {
            message.error('网络连接异常，请检查网络');
        } else {
            message.error(error.message || '请求配置错误');
        }

        // 关键：返回 reject 但不会让页面崩溃，只会被业务层 catch 接收
        return Promise.reject(error);
    }
);

// 封装泛型请求方法
const request = {
    get<T = any>(
        url: string,
        config: Partial<InternalAxiosRequestConfig<any>> = {}
    ): Promise<T> {
        return service.get(url, config);
    },
    post<T = any>(
        url: string,
        data: Record<string, any> = {},
        config: Partial<InternalAxiosRequestConfig<any>> = {}
    ): Promise<T> {
        return service.post(url, data, config);
    },
    put<T = any>(
        url: string,
        data: Record<string, any> = {},
        config: Partial<InternalAxiosRequestConfig<any>> = {}
    ): Promise<T> {
        return service.put(url, data, config);
    },
    delete<T = any>(
        url: string,
        config: Partial<InternalAxiosRequestConfig<any>> = {}
    ): Promise<T> {
        return service.delete(url, config);
    }
};

export default service;
export { request };