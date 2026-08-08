package cn.iocoder.teach-ai.module.clientSystem.framework.securiy.config;

import cn.iocoder.teach-ai.framework.security.config.AuthorizeRequestsCustomizer;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * 客户端系统管理 模块的 Security 配置
 */

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {

            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // Swagger 接口文档
                registry.requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll();
                // Druid 监控
                registry.requestMatchers("/druid/**").permitAll();
                // Spring Boot Actuator 的安全配置
                registry.requestMatchers("/actuator").permitAll()
                        .requestMatchers("/actuator/**").permitAll();
                // RPC 服务的安全配置
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
                // 客户端系统登录
                registry.requestMatchers("/client-api/**").permitAll();
            }

        };
    }


}
