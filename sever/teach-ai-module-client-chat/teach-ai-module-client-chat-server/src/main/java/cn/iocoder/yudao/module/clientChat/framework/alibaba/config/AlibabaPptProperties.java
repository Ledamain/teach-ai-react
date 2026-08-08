package cn.iocoder.teach-ai.module.clientChat.framework.alibaba.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 AI PPT 配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "alibaba-ppt")
public class AlibabaPptProperties {

    /**
     * 阿里云 AccessKey
     */
    private String accessKeyId;

    /**
     * 阿里云 AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * 区域
     */
    private String region;

    /**
     * 访问端点
     */
    private String endpoint;

    /**
     * 工作空间 ID
     */
    private String workspaceId;
}
