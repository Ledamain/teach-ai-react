package cn.iocoder.teach-ai.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 *
 * @author 芋道源码
 */
@ConfigurationProperties(prefix = "teach-ai.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "teach-ai.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
