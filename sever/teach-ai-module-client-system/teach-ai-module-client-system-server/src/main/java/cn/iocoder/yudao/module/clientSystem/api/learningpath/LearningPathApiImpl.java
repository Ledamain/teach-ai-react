package cn.iocoder.teach-ai.module.clientSystem.api.learningpath;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathNodeDO;
import cn.iocoder.teach-ai.module.clientSystem.service.learningpath.LearningPathService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class LearningPathApiImpl implements LearningPathApi {

    @Resource
    private LearningPathService learningPathService;

    @Override
    public CommonResult<List<LearningPathRespDTO>> listByUserId(Long userId) {
        List<LearningPathDO> list = learningPathService.listByUserId(userId);
        return success(BeanUtils.toBean(list, LearningPathRespDTO.class));
    }

    @Override
    public CommonResult<LearningPathRespDTO> getActive(Long userId, Long repoCategoryId) {
        LearningPathDO path = learningPathService.getActiveByUserAndCategory(userId, repoCategoryId);
        if (path == null) return success(null);
        return success(BeanUtils.toBean(path, LearningPathRespDTO.class));
    }

    @Override
    public CommonResult<List<LearningPathNodeRespDTO>> getNodes(Long pathId) {
        List<LearningPathNodeDO> nodes = learningPathService.getNodesByPathId(pathId);
        return success(BeanUtils.toBean(nodes, LearningPathNodeRespDTO.class));
    }

    @Override
    public CommonResult<LearningPathRespDTO> generate(LearningPathGenerateReqDTO req) {
        List<LearningPathNodeDO> nodes = new ArrayList<>();
        Map<Integer, Integer> orderToDependsOnOrder = new HashMap<>();
        if (req.getNodes() != null) {
            for (var dto : req.getNodes()) {
                nodes.add(LearningPathNodeDO.builder()
                        .orderIndex(dto.getOrderIndex())
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .resourceType(dto.getResourceType())
                        .estimatedMinutes(dto.getEstimatedMinutes())
                        .status("pending")
                        .build());
                if (dto.getDependsOnOrder() != null && dto.getDependsOnOrder() > 0) {
                    orderToDependsOnOrder.put(dto.getOrderIndex(), dto.getDependsOnOrder());
                }
            }
        }

        LearningPathDO path = learningPathService.createPath(
                req.getUserId(), req.getRepoCategoryId(), req.getRepoCategoryName(),
                req.getTitle(), req.getDescription(), nodes);

        if (!orderToDependsOnOrder.isEmpty()) {
            List<LearningPathNodeDO> savedNodes = learningPathService.getNodesByPathId(path.getId());
            Map<Integer, Long> orderToId = new HashMap<>();
            for (LearningPathNodeDO n : savedNodes) {
                orderToId.put(n.getOrderIndex(), n.getId());
            }
            for (LearningPathNodeDO node : savedNodes) {
                Integer dependsOnOrder = orderToDependsOnOrder.get(node.getOrderIndex());
                if (dependsOnOrder != null) {
                    Long dependsOnId = orderToId.get(dependsOnOrder);
                    if (dependsOnId != null) {
                        learningPathService.updateNodeDependsOn(node.getId(), dependsOnId);
                    }
                }
            }
        }

        return success(BeanUtils.toBean(path, LearningPathRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> updateNodeStatus(Long nodeId, String status) {
        learningPathService.updateNodeStatus(nodeId, status);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> setNodeDependsOn(Long nodeId, Long dependsOn) {
        learningPathService.updateNodeDependsOn(nodeId, dependsOn);
        return success(true);
    }

    @Override
    public CommonResult<List<ResourceRecommendRespDTO>> getTodayRecommend(Long userId) {
        List<LearningPathDO> activePaths = learningPathService.listActiveByUserId(userId);
        if (activePaths.isEmpty()) return success(Collections.emptyList());

        List<ResourceRecommendRespDTO> recommends = new ArrayList<>();
        for (LearningPathDO path : activePaths) {
            List<LearningPathNodeDO> nodes = learningPathService.getNodesByPathId(path.getId());
            for (LearningPathNodeDO node : nodes) {
                if ("pending".equals(node.getStatus())) {
                    recommends.add(ResourceRecommendRespDTO.builder()
                            .id(node.getId())
                            .title(node.getTitle())
                            .description(node.getDescription())
                            .resourceType(node.getResourceType())
                            .resourceId(node.getResourceId())
                            .reason("来自「" + path.getRepoCategoryName() + "」学习路径")
                            .build());
                    if (recommends.size() >= 5) break;
                }
            }
            if (recommends.size() >= 5) break;
        }
        return success(recommends);
    }

    @Override
    public CommonResult<PathAssessmentRespDTO> assessAndAdjust(Long userId, Long repoCategoryId, String repoCategoryName) {
        // 评估由 RPC 调用 client-chat 模块完成，此接口仅在 client-chat 侧实现
        // 在 client-system 侧返回空，实际通过 WebClient 转发到 client-chat
        return success(null);
    }
}
