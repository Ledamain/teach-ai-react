package cn.iocoder.teach-ai.module.clientChat.framework.security.config;

import cn.iocoder.teach-ai.framework.security.config.AuthorizeRequestsCustomizer;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * 客户端聊天 模块的 Security 配置
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
                // LangGraph Studio 的安全配置
                registry.requestMatchers("/").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/favicon*.svg").permitAll()
                        .requestMatchers("/init").permitAll()
                        .requestMatchers("/stream/**").permitAll()
                        .requestMatchers("/webui*").permitAll()
                        .requestMatchers("/*Diagram*").permitAll()
                        .requestMatchers("/katex*").permitAll()
                        .requestMatchers("/flowchart*").permitAll()
                        .requestMatchers("/mindmap*").permitAll()
                        .requestMatchers("/timeline*").permitAll()
                        .requestMatchers("/mermaid*").permitAll()
                        .requestMatchers("/*.js").permitAll()
                        .requestMatchers("/*.css").permitAll()
                        .requestMatchers("/*.svg").permitAll()
                        .requestMatchers("/*.js.map").permitAll()
                        .requestMatchers("/*.css.map").permitAll()
                        .requestMatchers("/assets/**").permitAll();
                // RPC 服务的安全配置
                registry.requestMatchers(ApiConstants.PREFIX + "/**").permitAll();
            }

        };
    }

}
