package cn.iocoder.teach-ai.module.clientSystem.controller.client.image;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.image.ImageApi;
import cn.iocoder.teach-ai.module.clientChat.api.image.dto.ImageChatParmDTO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client-api/client-system/image")
public class ImageController {

    @Resource
    private ImageApi imageApi;

    @PostMapping(value = "/create")
    public CommonResult<String> createImage(@RequestBody ImageChatParmDTO chatParmDTO) {
        return imageApi.imageCreation(chatParmDTO);
    }

    @GetMapping("/result")
    public CommonResult<String> getImageResult(@RequestParam String taskId, @RequestParam Long userId) {
        return imageApi.getImageResult(taskId, userId);
    }

}
