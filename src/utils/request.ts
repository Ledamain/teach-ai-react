import axios, {
    AxiosInstance,
    InternalAxiosRequestConfig, // 核心：改用Axios内部请求配置类型
    AxiosResponse,
    AxiosError,
    AxiosRequestHeaders // 导入headers专属类型
} from 'axios';
import { message } from 'antd';
import Router from 'next/router';
import { isBrowser } from './env';

// 创建axios实例
const service: AxiosInstance = axios.create({
    baseURL: '/client-api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    } as AxiosRequestHeaders // 显式指定headers类型，消除隐式推导问题
});

// 请求拦截器：入参/返回值统一为 InternalAxiosRequestConfig<any>
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig<any>): InternalAxiosRequestConfig<any> => {
        if (isBrowser) {
            const token = localStorage.getItem('token');
            if (token) {
                // 确保config.headers非空，直接赋值Authorization
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

// 响应拦截器：无需修改，入参是AxiosResponse（与内部配置无冲突）
service.interceptors.response.use(
    (response: AxiosResponse): any => {
        const res = response.data;
        // 仅保留后端真实成功码0
        if (res.code === 0) {
            return res.data || res;
        } else {
            // 改为后端的msg字段取提示
            message.error(res.msg || '请求失败');
            return Promise.reject(new Error(res.msg || '请求失败'));
        }
    },
    (error: AxiosError): Promise<never> => {
        console.error('响应错误:', error);
        const showError = (txt: string) => message.error(txt, 3);

        if (error.response) {
            const status = error.response.status;
            // 同步改为msg字段，匹配后端格式
            const errMsg = (error.response.data as Record<string, any>)?.msg || '请求失败';
            switch (status) {
                case 401:
                    showError('未授权，请重新登录');
                    if (isBrowser) {
                        localStorage.removeItem('token');
                        localStorage.removeItem('userInfo');
                        Router.push('/login').then(() => window.location.reload());
                    }
                    break;
                case 403:
                    // showError('拒绝访问，无权限操作');
                    message.error('拒绝访问，无权限操作')
                    break;
                case 404:
                    // showError('请求地址不存在');
                    message.error('请求地址不存在')
                    break;
                case 500:
                    // showError('服务器内部错误，请稍后再试');
                    message.error('服务器内部错误，请稍后再试')
                    break;
                default:
                    showError(errMsg);
            }
        } else if (error.request) {
            // showError('网络连接异常，请检查网络');
            message.error('网络连接异常，请检查网络')
        } else {
            // showError(error.message || '请求配置错误');
            message.error(error.message || '请求配置错误')
        }

        return Promise.reject(error);
    }
);

// 封装泛型请求方法：保持TS类型推导，无报错
// 封装泛型请求方法：修复config默认值，保证headers必选属性存在
const request = {
    get<T = any>(
        url: string,
        config: InternalAxiosRequestConfig<any> = { headers: {}, params: {} }
    ): Promise<T> {
        return service.get(url, config);
    },
    post<T = any>(
        url: string,
        data: Record<string, any> = {},
        config: InternalAxiosRequestConfig<any> = { headers: {} }
    ): Promise<T> {
        return service.post(url, data, config);
    },
    put<T = any>(
        url: string,
        data: Record<string, any> = {},
        config: InternalAxiosRequestConfig<any> = { headers: {} }
    ): Promise<T> {
        return service.put(url, data, config);
    },
    delete<T = any>(
        url: string,
        params: Record<string, any> = {},
        config: InternalAxiosRequestConfig<any> = { headers: {} }
    ): Promise<T> {
        return service.delete(url, { params, ...config });
    }
};

export default service;
export { request };
