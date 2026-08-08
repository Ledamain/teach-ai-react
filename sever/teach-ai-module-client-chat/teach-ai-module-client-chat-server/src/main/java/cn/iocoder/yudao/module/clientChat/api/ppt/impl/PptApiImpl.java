package cn.iocoder.teach-ai.module.clientChat.api.ppt.impl;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.PptApi;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.*;
import cn.iocoder.teach-ai.module.clientChat.service.ppt.PptGenerationService;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Valid
public class PptApiImpl implements PptApi {

    @Resource
    private PptGenerationService pptGenerationService;

    @Override
    public CommonResult<InitiatePptCreationResp> initiatePptCreation(ChatParamDTO chatParam) {
        InitiatePptCreationResp resp = pptGenerationService.initiatePptCreation(chatParam.getTaskId(), chatParam.getOutline());
        return CommonResult.success(resp);
    }

    @Override
    public CommonResult<BindPptArtifactResp> bindPptArtifact(ChatParamDTO chatParam) {
        BindPptArtifactResp resp = pptGenerationService.bindPptArtifact(chatParam.getTaskId(), chatParam.getArtifactId());
        return CommonResult.success(resp);
    }

    @Override
    public CommonResult<ExportPptArtifactResp> exportPptArtifact(ChatParamDTO chatParam) {
        ExportPptArtifactResp resp = pptGenerationService.exportPptArtifact(chatParam.getArtifactId());
        return CommonResult.success(resp);
    }

    @Override
    public CommonResult<Boolean> getPptArtifactExportResult(ChatParamDTO chatParam) {
        return CommonResult.success(pptGenerationService.getPptArtifactExportResult(chatParam.getClientUserId(),chatParam.getExportTaskId()));
    }

    @Override
    public CommonResult<PageResponseDTO<TemplateDTO>> template() {
        List<TemplateDTO> templateList = Lists.newArrayList();
        templateList.add(TemplateDTO.builder().id("12540").previewUrl("https://aippt-domestic.aippt.com/ppt-sop/0/121/styles/20250516173948567451.jpeg").build());
        templateList.add(TemplateDTO.builder().id("12538").previewUrl("https://aippt-domestic.aippt.com/ppt-sop/0/121/styles/20250516172714717455.jpeg").build());
        templateList.add(TemplateDTO.builder().id("12534").previewUrl("https://aippt-domestic.aippt.com/ppt-sop/0/121/styles/20250516171130489635.jpeg").build());
        templateList.add(TemplateDTO.builder().id("12525").previewUrl("https://aippt-domestic.aippt.com/ppt-sop/0/121/styles/20250516164523557510.jpeg").build());

        PageResponseDTO<TemplateDTO> response = new PageResponseDTO<>();
        response.setContent(templateList);
        response.setTotalPages(10); // 示例总页数
        response.setTotalElements(100); // 示例总条目数
        response.setSize(10); // 每页数量
        response.setNumber(1);
        response.setFirst(true);
        response.setLast(true);
        response.setNumberOfElements(templateList.size());
        return CommonResult.success(response);
    }
}
