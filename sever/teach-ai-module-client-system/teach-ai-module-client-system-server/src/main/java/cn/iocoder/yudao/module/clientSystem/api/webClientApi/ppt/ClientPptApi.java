package cn.iocoder.teach-ai.module.clientSystem.api.webClientApi.ppt;

import cn.iocoder.teach-ai.framework.common.pojo.ChatParam;
import cn.iocoder.teach-ai.framework.common.pojo.PptQuery;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
public class ClientPptApi {

    private final WebClient webClient;

    public ClientPptApi(@LoadBalanced WebClient.Builder builder) {
        // baseUrl 使用 A 服务在注册中心的 **服务名**
        this.webClient = builder.baseUrl("http://"+ ApiConstants.NAME+"/rpc-api").build();
    }

    /**
     * 调用 SSE 流式接口：/runPptOutlineGeneration
     */
    public Flux<ServerSentEvent<Map>> runPptOutlineGeneration(PptQuery query) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/client-chat/ppt/runPptOutlineGeneration")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(query)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<Map>>() {})
                .doOnNext(event -> {
                    System.out.println("Event: " + event.event());
                    System.out.println("Data: " + event.data());
                })
                .doOnError(e -> log.error("SSE stream error", e))
                .doOnComplete(() -> log.info("SSE stream completed"));
    }

}
