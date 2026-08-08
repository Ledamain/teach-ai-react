package cn.iocoder.teach-ai.module.clientSystem.api.profile;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileHistoryRespDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.dto.StudentProfileSaveReqDTO;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = ApiConstants.NAME, primary = false)
@Tag(name = "RPC 服务 - 学生画像")
public interface StudentProfileApi {

    String PREFIX = ApiConstants.PREFIX + "/student-profile";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获取学生画像")
    CommonResult<StudentProfileRespDTO> getProfileByUserId(@RequestParam("userId") Long userId);

    @GetMapping(PREFIX + "/history")
    @Operation(summary = "获取画像历史快照")
    CommonResult<List<StudentProfileHistoryRespDTO>> getProfileHistory(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limit", defaultValue = "20") int limit);

    @PostMapping(PREFIX + "/upsert")
    @Operation(summary = "更新或创建画像")
    CommonResult<Boolean> upsertProfile(@Valid @RequestBody StudentProfileSaveReqDTO reqDTO);
}
