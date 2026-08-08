package cn.iocoder.teach-ai.module.clientSystem.api.webClientApi.chat;

import cn.iocoder.teach-ai.framework.common.pojo.ChatParam;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientChatApi {

    private final WebClient webClient;

    public ClientChatApi(@LoadBalanced WebClient.Builder builder) {
        // baseUrl 使用 A 服务在注册中心的 **服务名**
        this.webClient = builder.baseUrl("http://"+ ApiConstants.NAME+"/rpc-api").build();
    }

    public Flux<String> streamChat(ChatParam chatParam) {

        return webClient.post()
                .uri("/client-chat/consultant/stream-post")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(chatParam)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> Mono.error(new RuntimeException("AI服务返回错误: " + response.statusCode()))
                )
                .bodyToFlux(String.class);
    }

    public Flux<ServerSentEvent<String>> agentStream(ChatParam chatParam) {
        // 声明带泛型的类型引用
        ParameterizedTypeReference<ServerSentEvent<String>> sseType =
                new ParameterizedTypeReference<>() {};

        return webClient.post()
                .uri("/client-chat/consultant/agent-post")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(chatParam)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> Mono.error(new RuntimeException("AI服务返回错误: " + response.statusCode()))
                )
                // 传入类型引用，替代 .class
                .bodyToFlux(sseType);
    }

}
