package cn.iocoder.teach-ai.module.clientChat.api.image;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientChat.api.image.bo.TaskResult;
import cn.iocoder.teach-ai.module.clientChat.api.image.dto.ImageChatParmDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.IMAGE_EXPORT_EXCEPTION;

@Slf4j
@RestController
@Valid
public class ImageApiImpl implements ImageApi {

    @Value("${alibaba-image.api-url}")
    private String apiUrl;

    @Value("${alibaba-image.model-name}")
    private String modelName;

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Resource
    private FileApi fileApi;

    @Resource
    private PptHistoryApi pptHistoryApi;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    private static final String TASK_KEY_PREFIX = "image:task:";
    private static final long TASK_EXPIRE_MINUTES = 10L;

    static {
        Constants.baseHttpApiUrl = "https://dashscope.aliyuncs.com/api/v1";
    }

    @Override
    public CommonResult<String> imageCreation(ImageChatParmDTO chatParam) {
        String taskId = UUID.randomUUID().toString();

        // 初始化任务状态存入 Redis
        saveTask(taskId, new TaskResult("PENDING", null, null));

        // 异步执行图像生成
        CompletableFuture.runAsync(() -> {
            try {
                MultiModalConversation conv = new MultiModalConversation();
                MultiModalMessage userMessage = MultiModalMessage.builder()
                        .role(Role.USER.getValue())
                        .content(Arrays.asList(
                                Collections.singletonMap("text", chatParam.getPrompt())
                        )).build();

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("watermark", false);
                parameters.put("prompt_extend", true);
                parameters.put("negative_prompt", "低分辨率，低画质，肢体畸形，手指畸形，画面过饱和，蜡像感，人脸无细节，过度光滑，画面具有AI感。构图混乱。文字模糊，扭曲。");
                parameters.put("size", "2048*2048");

                MultiModalConversationParam param = MultiModalConversationParam.builder()
                        .apiKey(apiKey)
                        .model(modelName)
                        .messages(Collections.singletonList(userMessage))
                        .parameters(parameters)
                        .build();

                MultiModalConversationResult result = conv.call(param);
                saveTask(taskId, new TaskResult("DONE", JsonUtils.toJson(result), null));

            } catch (Exception e) {
                log.error("[imageCreation][prompt({}) 出错]", chatParam.getPrompt(), e);
                saveTask(taskId, new TaskResult("ERROR", null, e.getMessage()));
            }
        });

        // 立即返回 taskId
        return CommonResult.success(taskId);
    }

    @Override
    public CommonResult<String> getImageResult(String taskId, Long userId) {
        Object value = redisTemplate.opsForValue().get(TASK_KEY_PREFIX + taskId);
        if (value == null) {
            return CommonResult.error(404, "任务不存在或已过期");
        }
        String result = value.toString();
        log.info("任务结果：{}",result);
        String status = new JSONObject(result).getStr("status");
        try {
            if (status.equals("DONE")) {
                ObjectMapper objectMapper = new ObjectMapper();
                // 1. 解析最外层 JSON
                JsonNode rootNode = objectMapper.readTree(result);

                // 2. 取出 result 字段（字符串）
                String resultStr = rootNode.get("result").asText();

                // 3. 解析 result 里的第二层 JSON
                JsonNode resultNode = objectMapper.readTree(resultStr);

                // 4. 逐层获取 image URL
                String imageUrl = resultNode
                        .get("output")               // output
                        .get("choices")              // choices 数组
                        .get(0)                     // 第一个元素
                        .get("message")             // message
                        .get("content")             // content 数组
                        .get(0)                     // 第一个元素
                        .get("image")               // image
                        .asText();                  // 转字符串

                CompletableFuture.runAsync(() -> {
                    try {
                        MultipartFile file = FileUtil.urlToMultipartFile(imageUrl);
                        byte[] content = IoUtil.readBytes(file.getInputStream());
                        String fileName = file.getOriginalFilename();
                        if (pptHistoryApi.getPptHistoryByFileName(fileName).getCheckedData() == null) {
                            String fileUrl = fileApi.createFile(content, fileName);
                            PptHistoryDTO pptHistoryDTO = new PptHistoryDTO().setPptTitle(fileName).setPptFile(fileUrl).setClientUserId(userId).setPptFiletype("2");
                            pptHistoryApi.createPptHistory(pptHistoryDTO);
                        }
                    } catch (Exception e) {
                        log.error("图片生成异常:{}",e.getMessage());
                    }
                });
            }
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.error(IMAGE_EXPORT_EXCEPTION);
        }
    }

    // 存储任务到 Redis，10分钟过期
    private void saveTask(String taskId, TaskResult taskResult) {
        redisTemplate.opsForValue().set(
                TASK_KEY_PREFIX + taskId,
                JsonUtils.toJson(taskResult),
                TASK_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );
    }
}
