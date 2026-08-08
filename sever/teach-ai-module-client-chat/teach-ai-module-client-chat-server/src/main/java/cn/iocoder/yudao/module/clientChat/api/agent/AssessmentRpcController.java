package cn.iocoder.teach-ai.module.clientChat.api.agent;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.service.agent.AgentContext;
import cn.iocoder.teach-ai.module.clientChat.service.agent.AgentOrchestrator;
import cn.iocoder.teach-ai.module.clientChat.service.agent.AgentRole;
import cn.iocoder.teach-ai.module.clientChat.service.agent.impl.AssessmentAgent;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/rpc-api/client-chat/assessment")
public class AssessmentRpcController {

    @Resource
    private AssessmentAgent assessmentAgent;

    @Resource
    private AgentOrchestrator orchestrator;

    /**
     * 生成学习效果评估报告（多智能体协作模式）。
     * 先通过 GET /agent/status/{taskId} 建立 SSE 连接接收进度。
     */
    @PostMapping("/generate")
    public Mono<CommonResult<Map<String, Object>>> generate(
            @RequestParam Long userId,
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {

        AgentContext ctx = orchestrator.executePathGeneration(userId, repoCategoryId, repoCategoryName);

        // 在路径生成之后执行评估 Agent
        AssessmentAgent.AssessmentResult result = assessmentAgent.generateAssessment(ctx);

        Map<String, Object> data = Map.of(
                "overallScore", result.overallScore,
                "dimensions", result.dimensions,
                "strengths", result.strengths,
                "weaknesses", result.weaknesses,
                "suggestions", result.suggestions,
                "summary", result.summary
        );
        return Mono.just(success(data));
    }

    @PostMapping("/evaluate")
    public Mono<CommonResult<Map<String, Object>>> evaluate(
            @RequestParam Long userId,
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {

        AgentContext ctx = new AgentContext();
        ctx.setUserId(userId);
        ctx.setRepoCategoryId(repoCategoryId);
        ctx.setRepoCategoryName(repoCategoryName);

        AssessmentAgent.AssessmentResult result = assessmentAgent.generateAssessment(ctx);

        Map<String, Object> data = Map.of(
                "overallScore", result.overallScore,
                "dimensions", result.dimensions,
                "strengths", result.strengths,
                "weaknesses", result.weaknesses,
                "suggestions", result.suggestions,
                "summary", result.summary
        );
        return Mono.just(success(data));
    }
}
