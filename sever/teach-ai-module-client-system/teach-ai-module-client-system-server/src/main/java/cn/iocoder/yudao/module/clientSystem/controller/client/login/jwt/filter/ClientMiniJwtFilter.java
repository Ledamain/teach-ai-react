package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.filter;


import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service.IClientMiniJwtService;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ClientUserContext;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.error;

/**
 * 微信小程序后台api接口校验过滤器
 *
 * @author weijiayu
 * @date 2025/4/22 23:48
 */
@Component
@Slf4j
public class ClientMiniJwtFilter extends OncePerRequestFilter {

    @Resource
    private IClientMiniJwtService jwtService;

    // 注入JWT启用状态配置（关键新增）
    @Value("${wechat.jwt.enabled:true}")  // 默认true，避免配置缺失导致问题
    private boolean jwtEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!jwtEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // 标记是否为RPC请求
        boolean isRpcRequest = path.startsWith("/rpc-api");

        // 排除 RPC 调用路径，直接放行
        if (path.startsWith("/rpc-api")) {
            log.debug("[ClientMiniJwtFilter] RPC 请求放行: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // 只有需要鉴权的接口才进行校验
        if (!this.checkIsExcludeUri(path) && path.startsWith("/client-api")) {
            String token = request.getHeader("Client-Authorization");

            // Token 为空或格式不正确
            if (token == null || !token.startsWith("Bearer ")) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"message\":\"认证失败，无法访问系统资源\",\"data\":null}");
                return;
            }

            try {
                String jwtToken = token.substring(7);
                if (!jwtService.verifyToken(jwtToken)) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"code\":401,\"message\":\"认证失败，无法访问系统资源\",\"data\":null}");
                    return;
                }

                String userId = jwtService.parseUserId(jwtToken);
//                String typeId = jwtService.parseTypeId(jwtToken);
//                String uRole = jwtService.parseURole(jwtToken);

                if (StringUtils.isEmpty(userId)) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"code\":401,\"message\":\"认证失败，无法访问系统资源\",\"data\":null}");
                    return;
                }

                ClientUserContext.setCurrentUserId(userId);
//                ClientUserContext.setCurrentTypeId(typeId);
//                ClientUserContext.setCurrentRole(uRole);

            } catch (Exception e) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"message\":\"JWT validation failed: " + e.getMessage() + "\",\"data\":null}");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ClientUserContext.clear();
        }
    }

    // 跳过无需鉴权的
    private boolean checkIsExcludeUri(String path) {
        return path.startsWith("/client-api/client-system/clientLogin") ||
                path.startsWith("/client-api/client-system/clientToken") ||
                path.startsWith("/client-api/client-system/clientRegister") ||
                path.startsWith("/client-api/client-system/digital-human-video/upload-ppt");
//                || path.startsWith("/client-api/client-system/ppt/runPptOutlineGeneration");
    }
}
