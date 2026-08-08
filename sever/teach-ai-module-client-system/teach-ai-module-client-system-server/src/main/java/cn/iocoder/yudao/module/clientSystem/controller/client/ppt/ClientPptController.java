package cn.iocoder.teach-ai.module.clientSystem.controller.client.ppt;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PptQuery;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.PptApi;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.*;
import cn.iocoder.teach-ai.module.clientSystem.api.webClientApi.ppt.ClientPptApi;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Context;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client-api/client-system/ppt")
public class ClientPptController {

    @Resource
    private PptApi pptApi;

    @Resource
    private ClientPptApi clientPptApi;

    @PostMapping(value = "/runPptOutlineGeneration", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map>>runPptOutlineGeneration(
            @RequestBody(required = false) PptQuery query) {
        return clientPptApi.runPptOutlineGeneration(query);
    }

    @PostMapping("/initiatePptCreation")
    @ResponseBody
    public CommonResult<InitiatePptCreationResp> initiatePptCreation(@RequestBody ChatParamDTO chatParam) {
        return pptApi.initiatePptCreation(chatParam);
    }

    @PostMapping("/bindPptArtifact")
    @ResponseBody
    public CommonResult<BindPptArtifactResp> bindPptArtifact(@RequestBody ChatParamDTO chatParam) {
        return pptApi.bindPptArtifact(chatParam);
    }

    @PostMapping("/exportPptArtifact")
    @ResponseBody
    public CommonResult<ExportPptArtifactResp> exportPptArtifact(@RequestBody ChatParamDTO  chatParam) {
        return pptApi.exportPptArtifact(chatParam);
    }

    @PostMapping("/getPptArtifactExportResult")
    @ResponseBody
    public CommonResult<Boolean> getPptArtifactExportResult(@RequestBody ChatParamDTO chatParam) {
        return pptApi.getPptArtifactExportResult(chatParam);
    }

    @GetMapping("/template")
    @ResponseBody
    public CommonResult<PageResponseDTO<TemplateDTO>> template() {
        return pptApi.template();
    }

}
