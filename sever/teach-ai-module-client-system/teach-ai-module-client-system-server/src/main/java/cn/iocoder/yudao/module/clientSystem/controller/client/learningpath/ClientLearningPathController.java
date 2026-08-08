package cn.iocoder.teach-ai.module.clientSystem.controller.client.learningpath;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.LearningPathApi;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ClientUserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Tag(name = "客户端接口 - 学习路径")
@RestController
@RequestMapping("/client-api/client-system")
public class ClientLearningPathController {

    @Resource
    private LearningPathApi learningPathApi;

    private final WebClient webClient;

    public ClientLearningPathController(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://" + ApiConstants.NAME + "/rpc-api").build();
    }

    @GetMapping("/learning-path/list")
    @Operation(summary = "获取学习路径列表")
    @PermitAll
    public CommonResult<List<LearningPathRespDTO>> list(@RequestParam Long userId) {
        return learningPathApi.listByUserId(userId);
    }

    @GetMapping("/learning-path/active")
    @Operation(summary = "获取某学科的活跃路径")
    @PermitAll
    public CommonResult<LearningPathRespDTO> getActive(@RequestParam Long userId,
                                                        @RequestParam Long repoCategoryId) {
        return learningPathApi.getActive(userId, repoCategoryId);
    }

    @GetMapping("/learning-path/nodes")
    @Operation(summary = "获取路径节点")
    @PermitAll
    public CommonResult<List<LearningPathNodeRespDTO>> getNodes(@RequestParam Long pathId) {
        return learningPathApi.getNodes(pathId);
    }

    @PostMapping("/learning-path/generate")
    @Operation(summary = "生成学习路径（调用AI规划Agent）")
    @PermitAll
    public Mono<CommonResult<LearningPathRespDTO>> generate(
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {
        return doGenerate("/client-chat/learning-path/generate", repoCategoryId, repoCategoryName);
    }

    @PostMapping("/learning-path/generate-orchestrated")
    @Operation(summary = "生成学习路径（多智能体协作模式）")
    @PermitAll
    public Mono<CommonResult<LearningPathRespDTO>> generateOrchestrated(
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {
        return doGenerate("/client-chat/learning-path/generate-orchestrated", repoCategoryId, repoCategoryName);
    }

    private Mono<CommonResult<LearningPathRespDTO>> doGenerate(String path, Long repoCategoryId, String repoCategoryName) {
        String userIdStr = ClientUserContext.getCurrentUserId();
        Long userId = userIdStr != null ? Long.parseLong(userIdStr) : null;
        if (userId == null) {
            return Mono.just(success(null));
        }
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("userId", userId)
                        .queryParam("repoCategoryId", repoCategoryId)
                        .queryParam("repoCategoryName", repoCategoryName)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<CommonResult<LearningPathRespDTO>>() {})
                .onErrorResume(e -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).error("生成学习路径失败: {}", e.getMessage());
                    return Mono.just(success(null));
                });
    }

    @PutMapping("/learning-path/node/status")
    @Operation(summary = "更新节点状态")
    @PermitAll
    public CommonResult<Boolean> updateNodeStatus(@RequestParam Long nodeId, @RequestParam String status) {
        return learningPathApi.updateNodeStatus(nodeId, status);
    }

    @GetMapping("/learning-path/recommend")
    @Operation(summary = "今日推荐")
    @PermitAll
    public CommonResult<List<ResourceRecommendRespDTO>> getRecommend(@RequestParam Long userId) {
        return learningPathApi.getTodayRecommend(userId);
    }

    /**
     * 学习效果评估 + 路径动态调整
     */
    @PostMapping("/learning-path/assess")
    @Operation(summary = "学习效果评估与路径动态调整")
    @PermitAll
    public Mono<CommonResult<PathAssessmentRespDTO>> assess(
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {
        String userIdStr = ClientUserContext.getCurrentUserId();
        Long userId = userIdStr != null ? Long.parseLong(userIdStr) : null;
        if (userId == null) {
            return Mono.just(success(null));
        }
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/client-chat/learning-path/assess")
                        .queryParam("userId", userId)
                        .queryParam("repoCategoryId", repoCategoryId)
                        .queryParam("repoCategoryName", repoCategoryName)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<CommonResult<PathAssessmentRespDTO>>() {})
                .onErrorResume(e -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).error("评估失败: {}", e.getMessage());
                    return Mono.just(success(null));
                });
    }
}
