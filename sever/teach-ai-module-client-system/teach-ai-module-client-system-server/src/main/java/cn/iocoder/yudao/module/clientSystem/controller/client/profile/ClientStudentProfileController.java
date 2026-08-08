package cn.iocoder.teach-ai.module.clientSystem.controller.client.profile;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileHistoryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileSaveReqDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileHistoryDO;
import cn.iocoder.teach-ai.module.clientSystem.service.profile.StudentProfileService;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

/**
 * 客户端接口 - 学生画像
 */
@Tag(name = "客户端接口 - 学生画像")
@RestController
@RequestMapping("/client-api/client-system")
public class ClientStudentProfileController {

    @Resource
    private StudentProfileService profileService;

    @GetMapping("/student-profile/get")
    @Operation(summary = "获取当前学生画像")
    @PermitAll
    public CommonResult<StudentProfileRespDTO> getProfile(@RequestParam("userId") Long userId) {
        StudentProfileDO profile = profileService.getProfileByUserId(userId);
        if (profile == null) {
            return success(null);
        }
        return success(BeanUtils.toBean(profile, StudentProfileRespDTO.class));
    }

    @GetMapping("/student-profile/history")
    @Operation(summary = "获取画像历史快照")
    @PermitAll
    public CommonResult<List<StudentProfileHistoryRespDTO>> getProfileHistory(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        List<StudentProfileHistoryDO> history = profileService.getProfileHistory(userId, limit);
        return success(BeanUtils.toBean(history, StudentProfileHistoryRespDTO.class));
    }

    @PostMapping("/student-profile/upsert")
    @Operation(summary = "更新或创建画像")
    @PermitAll
    public CommonResult<Boolean> upsertProfile(@Valid @RequestBody StudentProfileSaveReqDTO reqDTO) {
        StudentProfileDO profileDO = BeanUtils.toBean(reqDTO, StudentProfileDO.class);
        profileService.upsertProfile(reqDTO.getUserId(), profileDO, reqDTO.getMemoryId(), reqDTO.getChangeSummary());
        return success(true);
    }
}
