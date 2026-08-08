package cn.iocoder.teach-ai.module.clientChat.api.video;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientChat.api.image.bo.TaskResult;
import cn.iocoder.teach-ai.module.clientChat.api.image.dto.ImageChatParmDTO;
import cn.iocoder.teach-ai.module.clientChat.api.video.dto.VideoChatParmDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.VIDEO_EXPORT_EXCEPTION;

@RestController
@Valid
@Slf4j
public class VideoApiImpl implements VideoApi {

    @Value("${alibaba-video.model-name}")
    private String modelName;

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private FileApi fileApi;

    @Resource
    private PptHistoryApi pptHistoryApi;


    private static final String TASK_KEY_PREFIX = "video:task:";
    private static final long TASK_EXPIRE_MINUTES = 10L;

    static {
        Constants.baseHttpApiUrl = "https://dashscope.aliyuncs.com/api/v1";
    }

    @Override
    public CommonResult<String> videoCreation(VideoChatParmDTO chatParam) {

        String taskId = UUID.randomUUID().toString();

        // 初始化任务状态存入 Redis
        saveTask(taskId, new TaskResult("PENDING", null, null));

        CompletableFuture.runAsync(() -> {
            try {
                VideoSynthesis vs = new VideoSynthesis();
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("prompt_extend", true);
                parameters.put("watermark", false);
                parameters.put("seed", 12345);

                VideoSynthesisParam param =
                        VideoSynthesisParam.builder()
                                .apiKey(apiKey)
                                .model(modelName)
                                .prompt(chatParam.getPrompt())
                                .audioUrl(chatParam.getAudioUrl())
                                .negativePrompt("")
                                .size(chatParam.getAspectRatio())
                                .duration(Integer.valueOf(chatParam.getDuration()))
                                .parameters(parameters)
                                .build();
                System.out.println("please wait...");
                VideoSynthesisResult result = vs.call(param);
                saveTask(taskId, new TaskResult("DONE", JsonUtils.toJson(result), null));
            }catch (Exception e){
                log.error("[videoCreation][prompt({}) 出错]", chatParam.getPrompt(), e);
                saveTask(taskId, new TaskResult("ERROR", null, e.getMessage()));
            }
        });
        return CommonResult.success(taskId);
    }

    @Override
    public CommonResult<String> getVideoResult(String taskId, Long userId) {
        Object value = redisTemplate.opsForValue().get(TASK_KEY_PREFIX + taskId);
        if (value == null) {
            return CommonResult.error(404, "任务不存在或已过期");
        }
        try {
            String result = value.toString();
            log.info("任务结果：{}",result);
            String status = new JSONObject(result).getStr("status");
            if (status.equals("DONE")) {
                ObjectMapper objectMapper = new ObjectMapper();
                // 2. 解析外层 JSON
                JsonNode rootNode = objectMapper.readTree(result);
                String resultStr = rootNode.get("result").asText();

                // 3. 解析内层 result JSON
                JsonNode resultNode = objectMapper.readTree(resultStr);

                // 4. 直接取 video_url（核心路径）
                String videoUrl = resultNode.get("output")
                        .get("video_url")
                        .asText();
                CompletableFuture.runAsync(() -> {
                    try {
                        MultipartFile file = FileUtil.urlToMultipartFile(videoUrl);
                        byte[] content = IoUtil.readBytes(file.getInputStream());
                        String fileName = file.getOriginalFilename();
                        if (pptHistoryApi.getPptHistoryByFileName(fileName) == null) {
                            String fileUrl = fileApi.createFile(content, fileName);
                            PptHistoryDTO pptHistoryDTO = new PptHistoryDTO().setPptTitle(fileName).setPptFile(fileUrl).setClientUserId(userId).setPptFiletype("1");
                            pptHistoryApi.createPptHistory(pptHistoryDTO);
                        }
                    } catch (Exception e) {
                        log.error("视频生成异常:{}",e.getMessage());
                    }
                });
            }
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.error(VIDEO_EXPORT_EXCEPTION);
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
