package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt;

import cn.iocoder.teach-ai.module.infra.api.config.ConfigApi;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JWTConfiguration {

    public static Integer EXPIRE_MINUTE = 300; // 默认5小时兜底

    @Resource
    private ConfigApi configApi;

    @PostConstruct
    public void init(){
        try {
            String value = configApi.getConfigValueByKey("client-token-expire").getCheckedData();
            if (cn.hutool.core.util.StrUtil.isNotBlank(value)) {
                EXPIRE_MINUTE = Integer.parseInt(value.trim());
            }
        } catch (Exception e) {
            // 配置读取失败，使用默认值，打印日志
            e.printStackTrace();
        }
    }
}
