package cn.iocoder.teach-ai.module.clientSystem.api.webClientApi.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        // 构建HttpClient，配置底层netty的超时参数
        HttpClient httpClient = HttpClient.create()
                // 1. 连接超时：5秒（建立连接的超时，无需过长）
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 2. 响应超时：全局兜底（5分钟）
                .responseTimeout(Duration.ofMinutes(5))
                // 3. 更精准的读写超时控制（netty层面）
                .doOnConnected(conn -> conn
                        // 读取超时：5分钟（流式接口核心，避免提前断开）
                        .addHandlerLast(new ReadTimeoutHandler(300, TimeUnit.SECONDS))
                        // 写入超时：30秒（发送请求的超时，按需调整）
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

        // 构建WebClient.Builder，保留负载均衡和编解码配置
        return WebClient.builder()
                // 调整内存缓冲区大小（适配大流量流式数据）
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                // 注入配置好的HttpClient
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

}
