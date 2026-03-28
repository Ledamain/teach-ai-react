/** 登录接口返回值 */
export interface LoginResult {
    accessToken: string;
    refreshToken: string;
    userInfo?: { // 可选：后端返回的用户信息，按需定义
        id?: number;
        nickname: string;
        clientUsername: string;
        clientAvator?: string;
    };
    msg?: string;
    code: number;
}

/** 登录接口参数 */
export interface LoginParams {
    id: number;
    password?: string;
    username?: string;
}

/** 注册接口参数 */
export interface RegisterParams {
    id: number;
    password?: string;
    username?: string;
    nickname?: string;
}

/** 浏览器缓存用户信息参数*/
export interface UserInfo {
    id?: number;
    nickname: string;
    clientUsername: string;
    clientAvator?: string;
}