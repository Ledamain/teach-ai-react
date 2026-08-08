package cn.iocoder.teach-ai.module.clientSystem.controller.client.video;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.image.ImageApi;
import cn.iocoder.teach-ai.module.clientChat.api.image.dto.ImageChatParmDTO;
import cn.iocoder.teach-ai.module.clientChat.api.video.VideoApi;
import cn.iocoder.teach-ai.module.clientChat.api.video.dto.VideoChatParmDTO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client-api/client-system/video")
public class videoController {

    @Resource
    private VideoApi videoApi;

    @PostMapping(value = "/create")
    public CommonResult<String> createImage(@RequestBody VideoChatParmDTO chatParmDTO) {
        return videoApi.videoCreation(chatParmDTO);
    }

    @GetMapping("/result")
    public CommonResult<String> getImageResult(@RequestParam String taskId, @RequestParam Long userId) {
        return videoApi.getVideoResult(taskId, userId);
    }

}
