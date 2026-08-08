package cn.iocoder.teach-ai.module.clientChat.api.ppt;

import cn.iocoder.teach-ai.framework.common.pojo.PptQuery;
import cn.iocoder.teach-ai.module.clientChat.service.ppt.PptGenerationService;
import cn.iocoder.teach-ai.module.clientChat.service.ppt.aiService.PptAiService;
import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rpc-api/client-chat/ppt")
public class ClientPptApi {

    @Resource
    private PptGenerationService pptGenerationService;

    @Resource
    private PptAiService pptAiService;

    @PostMapping(value = "/runPptOutlineGeneration", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter runPptOutlineGeneration(
            @RequestBody(required = false) PptQuery query) {
        try {
            ClientUserContext.setCurrentMemoryId(query.getPptMemoryId());
            String queryContent = pptAiService.pptChat(query.getQuery());
            return pptGenerationService.runPptOutlineGeneration(queryContent);
        } finally {
            ClientUserContext.clear();
        }
    }

}
