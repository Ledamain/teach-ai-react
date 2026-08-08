package cn.iocoder.teach-ai.module.clientSystem.controller.client.assessment;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.utils.ClientUserContext;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningbehavior.LearningBehaviorDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningbehavior.LearningBehaviorMapper;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningpath.LearningPathMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Tag(name = "客户端接口 - 学习行为跟踪")
@RestController
@RequestMapping("/client-api/client-system")
public class ClientBehaviorController {

    @Resource
    private LearningBehaviorMapper behaviorMapper;

    @PostMapping("/learning-behavior/track")
    @Operation(summary = "上报学习行为事件")
    @PermitAll
    public CommonResult<Boolean> track(@RequestBody List<BehaviorEventVO> events) {
        String userIdStr = ClientUserContext.getCurrentUserId();
        if (userIdStr == null) return success(false);
        Long userId = Long.parseLong(userIdStr);

        for (BehaviorEventVO e : events) {
            behaviorMapper.insert(LearningBehaviorDO.builder()
                    .userId(userId)
                    .eventType(e.getEventType())
                    .repoCategoryId(e.getRepoCategoryId())
                    .resourceId(e.getResourceId())
                    .durationSeconds(e.getDurationSeconds())
                    .metadata(e.getMetadata())
                    .build());
        }
        return success(true);
    }

    /** 前端埋点上报的VO */
    @lombok.Data
    public static class BehaviorEventVO {
        private String eventType;
        private Long repoCategoryId;
        private Long resourceId;
        private Integer durationSeconds;
        private String metadata;
    }
}
