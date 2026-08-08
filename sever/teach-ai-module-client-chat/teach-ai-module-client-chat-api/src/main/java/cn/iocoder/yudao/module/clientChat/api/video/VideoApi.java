package cn.iocoder.teach-ai.module.clientChat.api.video;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.image.dto.ImageChatParmDTO;
import cn.iocoder.teach-ai.module.clientChat.api.video.dto.VideoChatParmDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 视频生成")
public interface VideoApi {

    String PREFIX = ApiConstants.PREFIX + "/video";

    @PostMapping(PREFIX + "/create-video")
    @ResponseBody
    CommonResult<String> videoCreation(@RequestBody VideoChatParmDTO chatParam);

    @GetMapping(PREFIX + "/get-result")
    @ResponseBody
    CommonResult<String> getVideoResult(@RequestParam String taskId , @RequestParam Long userId);

}
