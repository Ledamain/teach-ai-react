package cn.iocoder.teach-ai.module.clientSystem.api.learningpath;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto.*;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = ApiConstants.NAME, primary = false)
@Tag(name = "RPC 服务 - 学习路径")
public interface LearningPathApi {

    String PREFIX = ApiConstants.PREFIX + "/learning-path";

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获取用户的学习路径列表")
    CommonResult<List<LearningPathRespDTO>> listByUserId(@RequestParam("userId") Long userId);

    @GetMapping(PREFIX + "/active")
    @Operation(summary = "获取用户某学科的活跃路径")
    CommonResult<LearningPathRespDTO> getActive(@RequestParam("userId") Long userId,
                                                  @RequestParam("repoCategoryId") Long repoCategoryId);

    @GetMapping(PREFIX + "/nodes")
    @Operation(summary = "获取路径节点列表")
    CommonResult<List<LearningPathNodeRespDTO>> getNodes(@RequestParam("pathId") Long pathId);

    @PostMapping(PREFIX + "/generate")
    @Operation(summary = "生成学习路径")
    CommonResult<LearningPathRespDTO> generate(@Valid @RequestBody LearningPathGenerateReqDTO req);

    @PutMapping(PREFIX + "/node/status")
    @Operation(summary = "更新节点状态")
    CommonResult<Boolean> updateNodeStatus(@RequestParam("nodeId") Long nodeId,
                                            @RequestParam("status") String status);

    @PutMapping(PREFIX + "/node/depends-on")
    @Operation(summary = "设置节点前置依赖")
    CommonResult<Boolean> setNodeDependsOn(@RequestParam("nodeId") Long nodeId,
                                           @RequestParam("dependsOn") Long dependsOn);

    @GetMapping(PREFIX + "/recommend")
    @Operation(summary = "获取今日推荐资源")
    CommonResult<List<ResourceRecommendRespDTO>> getTodayRecommend(@RequestParam("userId") Long userId);

    @PostMapping(PREFIX + "/assess")
    @Operation(summary = "学习效果评估 + 路径动态调整")
    CommonResult<PathAssessmentRespDTO> assessAndAdjust(@RequestParam("userId") Long userId,
                                                         @RequestParam("repoCategoryId") Long repoCategoryId,
                                                         @RequestParam("repoCategoryName") String repoCategoryName);
}
