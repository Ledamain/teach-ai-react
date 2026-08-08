import { UserInfo } from "@/types/login/LoginType";

export function getUserId(): number | null {
    // 非浏览器环境直接返回 null
    if (typeof window === "undefined") return null;

    try {
        const userInfoStr = localStorage.getItem("userInfo");
        if (!userInfoStr) return null;

        const userInfo: unknown = JSON.parse(userInfoStr);

        if (
            userInfo &&
            typeof userInfo === "object" &&
            "id" in userInfo &&
            !isNaN(Number((userInfo as UserInfo).id))
        ) {
            return Number((userInfo as UserInfo).id);
        }

        return null;
    } catch (err) {
        return null;
    }
}
