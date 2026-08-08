package cn.iocoder.teach-ai.module.clientChat.service.ppt;

import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface PptGenerationService {

    SseEmitter runPptOutlineGeneration(String query);

    InitiatePptCreationResp initiatePptCreation(String taskId, String outline);

    BindPptArtifactResp bindPptArtifact(String taskId, Integer artifactId);

    GetPptConfigResp getPptConfig(String taskId, String outline);

    ExportPptArtifactResp exportPptArtifact(Integer artifactId);

    Boolean getPptArtifactExportResult(Long clientUserId, String exportTaskId);
}
