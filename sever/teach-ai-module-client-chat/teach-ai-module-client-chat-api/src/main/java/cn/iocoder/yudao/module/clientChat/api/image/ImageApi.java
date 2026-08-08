package cn.iocoder.teach-ai.module.clientChat.api.image;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.image.dto.ImageChatParmDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 图像生成")
public interface ImageApi {

    String PREFIX = ApiConstants.PREFIX + "/image";

    @PostMapping(PREFIX + "/create-image")
    @ResponseBody
    CommonResult<String> imageCreation(@RequestBody ImageChatParmDTO chatParam);

    @GetMapping(PREFIX + "/get-result")
    @ResponseBody
    CommonResult<String> getImageResult(@RequestParam String taskId, @RequestParam Long userId);
}
