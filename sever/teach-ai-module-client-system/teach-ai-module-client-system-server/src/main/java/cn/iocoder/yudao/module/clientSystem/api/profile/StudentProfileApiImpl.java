package cn.iocoder.teach-ai.module.clientSystem.api.profile;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileHistoryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileSaveReqDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.profile.StudentProfileHistoryDO;
import cn.iocoder.teach-ai.module.clientSystem.service.profile.StudentProfileService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

/**
 * RPC 实现 - 学生画像
 *
 * 实现 {@link StudentProfileApi} Feign 接口，供 clientChat 等模块通过 RPC 调用。
 * 路径继承自接口注解：/rpc-api/client-system/student-profile/**
 */
@RestController
@Validated
public class StudentProfileApiImpl implements StudentProfileApi {

    @Resource
    private StudentProfileService profileService;

    @Override
    public CommonResult<StudentProfileRespDTO> getProfileByUserId(Long userId) {
        StudentProfileDO profile = profileService.getProfileByUserId(userId);
        if (profile == null) {
            return success(null);
        }
        return success(BeanUtils.toBean(profile, StudentProfileRespDTO.class));
    }

    @Override
    public CommonResult<List<StudentProfileHistoryRespDTO>> getProfileHistory(Long userId, int limit) {
        List<StudentProfileHistoryDO> history = profileService.getProfileHistory(userId, limit);
        return success(BeanUtils.toBean(history, StudentProfileHistoryRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> upsertProfile(StudentProfileSaveReqDTO reqDTO) {
        StudentProfileDO profileDO = BeanUtils.toBean(reqDTO, StudentProfileDO.class);
        profileService.upsertProfile(reqDTO.getUserId(), profileDO, reqDTO.getMemoryId(), reqDTO.getChangeSummary());
        return success(true);
    }
}
