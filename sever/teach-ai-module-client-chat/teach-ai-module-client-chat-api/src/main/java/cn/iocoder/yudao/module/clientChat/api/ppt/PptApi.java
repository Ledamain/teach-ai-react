package cn.iocoder.teach-ai.module.clientChat.api.ppt;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.*;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - ppt生成")
public interface PptApi {

    String PREFIX = ApiConstants.PREFIX + "/ppt";

    @PostMapping(PREFIX + "/initiatePptCreation")
    @ResponseBody
    CommonResult<InitiatePptCreationResp> initiatePptCreation(@RequestBody ChatParamDTO chatParam);

    @PostMapping(PREFIX + "/bindPptArtifact")
    @ResponseBody
    CommonResult<BindPptArtifactResp> bindPptArtifact(@RequestBody ChatParamDTO chatParam);

    @PostMapping(PREFIX + "/exportPptArtifact")
    @ResponseBody
    CommonResult<ExportPptArtifactResp> exportPptArtifact(@RequestBody ChatParamDTO chatParam);

    @PostMapping(PREFIX + "/getPptArtifactExportResult")
    @ResponseBody
    CommonResult<Boolean> getPptArtifactExportResult(@RequestBody ChatParamDTO chatParam);

    @GetMapping(PREFIX + "/template")
    @ResponseBody
    CommonResult<PageResponseDTO<TemplateDTO>> template();

}
