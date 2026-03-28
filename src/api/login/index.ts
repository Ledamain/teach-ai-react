import {LoginParams, LoginResult, RegisterParams} from "@/types/login/LoginType";
import {request} from "@/utils/request";
import {message} from "antd";

export async function login(
    params: Pick<LoginParams, "username" | "password">
): Promise<LoginResult> {
    try {
        // 调用登录接口，泛型指定返回值为LoginResponse，TS自动推导
        const res = await request.post<LoginResult>("/clientLogin", params);
        // 核心：提取token并存储到localStorage（和请求拦截器的token读取对应）
        if (res.accessToken) {
            localStorage.setItem('token', res.accessToken); // 存储token，供后续接口携带
            // 可选：如果后端返回userInfo，一并存储（和你Vue版的userInfo存储逻辑一致）
            if (res.userInfo) {
                localStorage.setItem(
                    'userInfo',
                    JSON.stringify({
                        ...res.userInfo,
                        loginTime: new Date().toLocaleString() // 追加登录时间，和Vue版一致
                    })
                );
            }
            message.success('登录成功'); // 统一登录成功提示
        } else {
            // 兜底：接口成功但无token的异常情况
            message.error('登录失败，未获取到身份令牌');
            throw new Error('登录失败，未获取到token');
        }
        return res; // 返回完整响应数据，供组件后续处理（比如跳转）
    } catch (error) {
        throw error; // 抛出错误，让组件能捕获并处理（比如关闭loading）
    }
}

export async function register(
    params: Pick<RegisterParams, "nickname" |"username" | "password">
): Promise<boolean> {
    try {
        const res = await request.post<boolean>("/clientRegister", params);
        message.success('注册成功,请再次登录')
        return true;
    }catch ( error){
        message.error('注册失败');
        return false;
    }
}