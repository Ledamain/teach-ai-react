package cn.iocoder.teach-ai.module.clientChat.api.digitalvideo;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoFileDTO;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoReqDTO;
import cn.iocoder.teach-ai.module.clientChat.api.image.bo.TaskResult;
import cn.iocoder.teach-ai.module.clientChat.utils.ImageCompositeUtil;
import cn.iocoder.teach-ai.module.clientChat.utils.PptxToImageUtil;
import cn.iocoder.teach-ai.module.infra.api.config.ConfigApi;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@Valid
@Slf4j
public class DigitalVideoApiImpl implements DigitalVideoApi{

    private static final String TASK_KEY_PREFIX = "digital-video:task:";
    private static final long TASK_EXPIRE_MINUTES = 10L;

    @Value("${dashscope.api-key}") // 请在配置文件中添加此配置
    private String dashscopeApiKey;

    @Value("${dashscope.multimodal-name}")
    private String multimodalName;

    @Resource
    private FileApi fileApi;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ConfigApi configApi;

    @Override
    public CommonResult<String> uploadPptx(DigitalVideoFileDTO dto) {
        String taskId = UUID.randomUUID().toString();
        saveTask(taskId, new TaskResult("PENDING", null, null));

        // ======================== 核心修复 ========================
        // 把文件复制到内存，防止Tomcat清理临时文件
        MultipartFile file = dto.getFile();
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes(); // 主线程读取文件到内存
        } catch (Exception e) {
            log.error("读取上传文件失败", e);
            saveTask(taskId, new TaskResult("ERROR", null, "读取文件失败"));
            throw new RuntimeException("读取文件失败");
        }
        // 构建内存文件
        MultipartFile memoryFile = new MockMultipartFile(
                file.getName(), originalFilename, contentType, fileBytes
        );
        // ==========================================================

        CompletableFuture.runAsync(() -> {
            try {
                URL digital = new URL("https://res.chanjing.cc/chanjing/video_matting/2026-02-13/b965447c4e5fdb3a37561c4f65cbcdc4.png");
                BufferedImage digitalImage = ImageIO.read(digital);

                URL background = new URL("https://teach-ai.tos-cn-beijing.volces.com/VideoBG2.png");
                BufferedImage bgImage = ImageIO.read(background);

                ArrayList<DigitalVideoReqDTO> list = new ArrayList<>();

                // 使用内存文件，不会被删除！
                List<BufferedImage> bufferedImages = PptxToImageUtil.pptxToImages(memoryFile);

                for (BufferedImage bufferedImage : bufferedImages) {
                    try {
                        byte[] image = ImageCompositeUtil.composite(bgImage, bufferedImage, 50, 135, 1215, 671, "png");
                        String originalUrl = fileApi.createFile(image, "temp_" + UUID.randomUUID() + ".png");

                        BufferedImage toBufferedImage = ImageCompositeUtil.byteArrayToBufferedImage(image);
                        byte[] composite = ImageCompositeUtil.composite(toBufferedImage, digitalImage, 1290, 50, 607, 1080, "png");
                        String url = fileApi.createFile(composite, "temp_" + UUID.randomUUID() + ".png");

                        list.add(new DigitalVideoReqDTO().setOriginalImageUrl(originalUrl).setImageUrl(url));
                    } catch (Exception e) {
                        log.error("单页图片合成失败", e);
                    }
                }
                saveTask(taskId, new TaskResult("DONE", JsonUtils.toJson(list), null));

            } catch (Exception e) {
                log.error("PPT 异步处理任务失败", e);
                saveTask(taskId, new TaskResult("ERROR", null, "PPT文件解析失败：" + e.getMessage()));
            }
        });

        return CommonResult.success(taskId);
    }

    @Override
    public CommonResult<String> getDigitalVideoResult(String taskId) {
        Object value = redisTemplate.opsForValue().get(TASK_KEY_PREFIX + taskId);
        if (value == null) {
            return CommonResult.error(404, "任务不存在或已过期");
        }
        String result = value.toString();
        log.info("任务结果：{}",result);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<String> GenerateSubtitles(DigitalVideoReqDTO digitalVideoReqDTO) {

        String DIGITAL_VIDEO_SYSTEM_MESSAGE = configApi.getConfigValueByKey("digital-video-system-message").getCheckedData() + digitalVideoReqDTO.getLanguage();
        log.info("数字人口播系统提示词:{}",DIGITAL_VIDEO_SYSTEM_MESSAGE);
        if (DIGITAL_VIDEO_SYSTEM_MESSAGE == null && DIGITAL_VIDEO_SYSTEM_MESSAGE.isEmpty()){
            return CommonResult.error(500, "请先配置DIGITAL_VIDEO_SYSTEM_MESSAGE");
        }

        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("image",digitalVideoReqDTO.getImageUrl()),
                            Collections.singletonMap("text", DIGITAL_VIDEO_SYSTEM_MESSAGE))).build();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(System.getenv(dashscopeApiKey))
                    .model(multimodalName)
                    .messages(Arrays.asList(userMessage))
                    .build();
            MultiModalConversationResult result = conv.call(param);
            String text = result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
            return CommonResult.success(text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveTask(String taskId, TaskResult taskResult) {
        redisTemplate.opsForValue().set(
                TASK_KEY_PREFIX + taskId,
                JsonUtils.toJson(taskResult),
                TASK_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );
    }

}
