package cn.iocoder.teach-ai.module.clientSystem.controller.client.assessment;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ClientUserContext;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.assessment.AssessmentReportDO;
import cn.iocoder.teach-ai.module.clientSystem.service.assessment.AssessmentReportService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/client-api/client-system")
public class ClientAssessmentController {

    private final WebClient webClient;

    @Resource
    private AssessmentReportService assessmentReportService;

    public ClientAssessmentController(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://" + ApiConstants.NAME + "/rpc-api").build();
    }

    @PostMapping("/assessment/evaluate")
    @PermitAll
    public Mono<CommonResult<Map<String, Object>>> evaluate(@RequestParam Long repoCategoryId,
                                                             @RequestParam String repoCategoryName) {
        String userIdStr = ClientUserContext.getCurrentUserId();
        Long userId = userIdStr != null ? Long.parseLong(userIdStr) : null;
        if (userId == null) return Mono.just(success(null));

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/client-chat/assessment/evaluate")
                        .queryParam("userId", userId)
                        .queryParam("repoCategoryId", repoCategoryId)
                        .queryParam("repoCategoryName", repoCategoryName)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<CommonResult<Map<String, Object>>>() {})
                .onErrorResume(e -> Mono.just(success(null)))
                .map(result -> {
                    // 保存评估报告到数据库
                    if (result != null && result.getData() != null) {
                        try {
                            Map<String, Object> data = result.getData();
                            AssessmentReportDO report = AssessmentReportDO.builder()
                                    .userId(userId)
                                    .periodStart(LocalDateTime.now().minusDays(7))
                                    .periodEnd(LocalDateTime.now())
                                    .overallScore(intVal(data.get("overallScore")))
                                    .dimensions(strVal(data.get("dimensions")))
                                    .strengths(strVal(data.get("strengths")))
                                    .weaknesses(strVal(data.get("weaknesses")))
                                    .suggestions(strVal(data.get("suggestions")))
                                    .summary(strVal(data.get("summary")))
                                    .build();
                            assessmentReportService.saveReport(userId, report);
                        } catch (Exception ignored) {}
                    }
                    return result;
                });
    }

    /** 获取用户最新评估报告 */
    @GetMapping("/assessment/latest")
    @PermitAll
    public CommonResult<AssessmentReportDO> getLatest() {
        String userIdStr = ClientUserContext.getCurrentUserId();
        Long userId = userIdStr != null ? Long.parseLong(userIdStr) : null;
        if (userId == null) return success(null);
        return success(assessmentReportService.getLatestByUserId(userId));
    }

    private int intVal(Object v) {
        if (v instanceof Number) return ((Number) v).intValue();
        return 60;
    }

    private String strVal(Object v) {
        return v != null ? v.toString() : "";
    }
}
