package cn.iocoder.teach-ai.module.clientChat.api.learningpath;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.service.agent.AgentContext;
import cn.iocoder.teach-ai.module.clientChat.service.agent.AgentOrchestrator;
import cn.iocoder.teach-ai.module.clientChat.service.agent.impl.AssessmentAgent;
import cn.iocoder.teach-ai.module.clientChat.service.learningpath.PlannerAgent;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.LearningPathRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.PathAssessmentRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

/**
 * RPC 接口 - 学习路径生成与评估
 */
@Slf4j
@RestController
@RequestMapping("/rpc-api/client-chat/learning-path")
public class LearningPathRpcController {

    @Resource
    private PlannerAgent plannerAgent;

    @Resource
    private AgentOrchestrator orchestrator;

    @Resource
    private AssessmentAgent assessmentAgent;

    /**
     * 生成学习路径
     */
    @PostMapping("/generate")
    public CommonResult<LearningPathRespDTO> generate(
            @RequestParam Long userId,
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {
        LearningPathRespDTO result = plannerAgent.generateAndSave(userId, repoCategoryId, repoCategoryName);
        return success(result);
    }

    /**
     * 生成学习路径 - 多智能体协作模式
     */
    @PostMapping("/generate-orchestrated")
    public CommonResult<LearningPathRespDTO> generateOrchestrated(
            @RequestParam Long userId,
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {
        log.info("启动多智能体协作: userId={}, category={}", userId, repoCategoryName);
        AgentContext ctx = orchestrator.executePathGeneration(userId, repoCategoryId, repoCategoryName);
        return success(ctx.getLearningPath());
    }

    /**
     * 学习效果评估 + 路径动态调整
     */
    @PostMapping("/assess")
    public CommonResult<PathAssessmentRespDTO> assess(
            @RequestParam Long userId,
            @RequestParam Long repoCategoryId,
            @RequestParam String repoCategoryName) {
        log.info("启动学习评估: userId={}, category={}", userId, repoCategoryName);

        // 构建评估上下文
        AgentContext ctx = new AgentContext();
        ctx.setUserId(userId);
        ctx.setRepoCategoryId(repoCategoryId);
        ctx.setRepoCategoryName(repoCategoryName);
        ctx.setTaskType("assessment");

        // 生成评估
        AssessmentAgent.AssessmentResult result = assessmentAgent.generateAssessment(ctx);

        // 路径反馈
        AssessmentAgent.PathAdjustment adjustment = assessmentAgent.applyPathFeedback(
                ctx, result, orchestrator.getEmitter(), "assess-" + userId);

        // 构建响应
        PathAssessmentRespDTO resp = PathAssessmentRespDTO.builder()
                .overallScore(result.overallScore)
                .dimensions(result.dimensions)
                .strengths(result.strengths)
                .weaknesses(result.weaknesses)
                .suggestions(result.suggestions)
                .summary(result.summary)
                .matchCount(adjustment.matchCount)
                .remediationNodeCount(adjustment.remediationBefore.size() + adjustment.remediationAfter.size())
                .remediationNodes(new ArrayList<>())
                .build();

        for (var n : adjustment.remediationBefore) {
            resp.getRemediationNodes().add(PathAssessmentRespDTO.RemediationNodeDTO.builder()
                    .targetNodeId(n.targetNodeId)
                    .position("before")
                    .title(n.title)
                    .description(n.description)
                    .resourceType(n.resourceType)
                    .build());
        }
        for (var n : adjustment.remediationAfter) {
            resp.getRemediationNodes().add(PathAssessmentRespDTO.RemediationNodeDTO.builder()
                    .targetNodeId(n.targetNodeId)
                    .position("after")
                    .title(n.title)
                    .description(n.description)
                    .resourceType(n.resourceType)
                    .build());
        }

        log.info("评估完成: overallScore={}, matchCount={}", result.overallScore, adjustment.matchCount);
        return success(resp);
    }
}
