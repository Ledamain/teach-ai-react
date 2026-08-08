package cn.iocoder.teach-ai.module.clientSystem.controller.client.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

public class HttpIpUtil {

    public static String getClientIp() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "127.0.0.1";
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return getClientIp(request);
    }

    // 重载：传入request参数版本
    public static String getClientIp(HttpServletRequest request) {
        // 优先从 X-Forwarded-For 获取
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多级代理情况，取第一个 IP
            int index = ip.indexOf(",");
            if (index != -1) {
                ip = ip.substring(0, index);
            }
            return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
        }

        // 其次尝试 Proxy-Client-IP
        ip = request.getHeader("Proxy-Client-IP");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 再次尝试 WL-Proxy-Client-IP
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 兼容其他可能的 Header
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 最后手段：直接获取远程地址
        ip = request.getRemoteAddr();
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
