package cn.iocoder.teach-ai.module.clientChat.utils;

import cn.iocoder.teach-ai.module.clientChat.framework.alibaba.config.AlibabaPptProperties;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.gateway.pop.Configuration;
import com.aliyun.sdk.gateway.pop.auth.SignatureVersion;
import com.aliyun.sdk.service.aimiaobi20230801.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ALiyunClientHelper {

    // 注入配置类
    @Resource
    private AlibabaPptProperties alibabaPptProperties;

    // 静态持有，方便静态方法获取
    private static ALiyunClientHelper helper;

    private static volatile AsyncClient client;

    // Spring 初始化后赋值
    @PostConstruct
    public void init() {
        helper = this;
    }

    private ALiyunClientHelper() {
    }

    public static AsyncClient getClient() {
        if (client == null) {
            synchronized (ALiyunClientHelper.class) {
                if (client == null) {
                    // 从配置类读取
                    String accessKeyId = helper.alibabaPptProperties.getAccessKeyId();
                    String accessKeySecret = helper.alibabaPptProperties.getAccessKeySecret();
                    String region = helper.alibabaPptProperties.getRegion();
                    String endpoint = helper.alibabaPptProperties.getEndpoint();

                    log.info("获取阿里云PPT客户端：accessKeyId={}, region={}, endpoint={}",
                            accessKeyId, region, endpoint);

                    StaticCredentialProvider provider = StaticCredentialProvider.create(
                            Credential.builder()
                                    .accessKeyId(accessKeyId)
                                    .accessKeySecret(accessKeySecret)
                                    .build()
                    );

                    client = AsyncClient.builder()
                            .region(region)
                            .credentialsProvider(provider)
                            .serviceConfiguration(Configuration.create().setSignatureVersion(SignatureVersion.V3))
                            .overrideConfiguration(
                                    ClientOverrideConfiguration.create()
                                            .setProtocol("HTTPS")
                                            .setEndpointOverride(endpoint)
                            )
                            .build();
                }
            }
        }
        return client;
    }
}
