package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.framework.common.util.video.VideoMergeUtil;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.DigitalVideoApi;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoFileDTO;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoReqDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo.*;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;
import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.VIDEO_EXPORT_EXCEPTION;

@Slf4j
@Tag(name = "客户端接口 - 数字人视频生成")
@RestController
@RequestMapping("/client-api/client-system/digital-human-video")
@Validated
public class ClientDigitalVideoController {

    private static final String TASK_KEY_PREFIX = "splicing-video:task:";
    private static final long TASK_EXPIRE_MINUTES = 10L;

    @Value("${digital-human-video.app-id}")
    private String appId;

    @Value("${digital-human-video.app-key}")
    private String appKey;

    @Resource
    private DigitalVideoApi digitalVideoApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private PptHistoryApi pptHistoryApi;

//    @PostMapping(value = "/upload-ppt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public CommonResult<List<DigitalVideoReqDTO>> uploadFile(
//            // 关键：必须用 @RequestParam 接收文件
//            @RequestParam("file") MultipartFile file,
//            // 可选参数 directory
//            @RequestParam(value = "directory", required = false) String directory
//    ) {
//        // 业务逻辑不变
//        return digitalVideoApi.uploadPptx(new DigitalVideoFileDTO().setFile(file));
//    }

    @PostMapping("/upload-ppt")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    public CommonResult<String> uploadFile(
            // 1. 文件必须单独用 @RequestParam 接收
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "fileflag", required = false) String fileflag
    ) throws Exception {

        // 2. 手动 set 进 DTO
        DigitalVideoFileDTO uploadReqVO = new DigitalVideoFileDTO();
        uploadReqVO.setFile(file);

        // 3. 调用远程接口
        return digitalVideoApi.uploadPptx(uploadReqVO);
    }

    @GetMapping("/get-upload-result")
    @Operation(summary = "获得解析结果")
    public CommonResult<String> getUploadResult(@RequestParam String taskId){
        return digitalVideoApi.getDigitalVideoResult(taskId);
    }

    @PostMapping("/generate-subtitles")
    @Operation(summary = "AI生成单页字幕")
    public CommonResult<String> GenerateSubtitles(@RequestBody DigitalVideoReqDTO digitalVideoReqDTO){
        return digitalVideoApi.GenerateSubtitles(digitalVideoReqDTO);
    }

    @PostMapping("/create")
    @Operation(summary = "生成数字人视频")
    public CommonResult<DigitalVideoCreateRespVO> createDigitalVideo(@Valid @RequestBody DigitalVideoSaveVO digitalVideoSaveVO) throws IOException {

        List<DigitalVideoReqDTO> videoReqDTOs = digitalVideoSaveVO.getSlides();

        String accessToken = getAccessToken();

        List<String> taskIds = new ArrayList<>();
        videoReqDTOs.forEach(digitalVideoReqDTO -> {
            String taskId = videoCreateTaskId(digitalVideoReqDTO, accessToken);
            taskIds.add(taskId);
        });
        return success(new DigitalVideoCreateRespVO().setSign(accessToken).setTaskIds(taskIds));
    }

    @PostMapping("/ai-create")
    @Operation(summary = "AI一键生成数字人视频")
    public CommonResult<DigitalVideoCreateRespVO> aiCreateDigitalVideo(@Valid @RequestBody DigitalVideoAiGenerateSaveVO digitalVideoSaveVO) throws IOException {

        List<DigitalVideoAiGenerateSaveVO.SlideItem> slides = digitalVideoSaveVO.getSlides();

        List<DigitalVideoReqDTO> videoReqDTOS = new ArrayList<>();
        slides.forEach(slideItem -> {
            String originalImageUrl = slideItem.getOriginalImageUrl();
            // AI生成字幕
            String checkedData = digitalVideoApi.GenerateSubtitles(new DigitalVideoReqDTO().setImageUrl(originalImageUrl)).getCheckedData();
            DigitalVideoReqDTO digitalVideoReqDTO = new DigitalVideoReqDTO().setOriginalImageUrl(originalImageUrl).setText(checkedData);
            videoReqDTOS.add(digitalVideoReqDTO);
        });

        String accessToken = getAccessToken();

        List<String> taskIds = new ArrayList<>();
        videoReqDTOS.forEach(digitalVideoReqDTO -> {
            String taskId = videoCreateTaskId(digitalVideoReqDTO, accessToken);
            taskIds.add(taskId);
        });
        return success(new DigitalVideoCreateRespVO().setSign(accessToken).setTaskIds(taskIds));
    }


    @PostMapping("/get-result")
    @Operation(summary = "获取生成数字人视频结果")
    public CommonResult<DigitalVideoResultVO> getDigitalVideoResult(
            @Valid @RequestBody DigitalVideoCreateRespVO digitalVideoCreateRespVO) {

        DigitalVideoResultVO resultVO = new DigitalVideoResultVO();
        String sign = digitalVideoCreateRespVO.getSign();
        List<String> taskIds = digitalVideoCreateRespVO.getTaskIds();
        int totalTaskCount = taskIds.size();

        int totalProgress = 0;
        List<String> videoUrlList = new ArrayList<>();

        // 【优化1】全局复用 OkHttpClient，不要循环内 new
        OkHttpClient client = new OkHttpClient();

        for (String taskId : taskIds) {
            Response response = null;
            try {
                Request request = new Request.Builder()
                        .url("https://www.chanjing.cc/api/open/v1/video?id=" + taskId)
                        .get()
                        .addHeader("access_token", sign)
                        .build();

                response = client.newCall(request).execute();
                // 判响应状态
                if (!response.isSuccessful()) {
                    log.warn("任务[{}]请求接口失败，响应码:{}", taskId, response.code());
                    continue;
                }

                String body = response.body().string();
                log.info("任务[{}]结果：{}", taskId, body);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(body);
                JsonNode dataNode = rootNode.path("data");
                if (dataNode.isMissingNode()) {
                    log.warn("任务[{}]data节点不存在", taskId);
                    continue;
                }

                // 【优化2】加默认值兜底，防止节点缺失抛异常
                int taskProgress = dataNode.get("progress").asInt(0);
                totalProgress += taskProgress;

                int status = dataNode.get("status").asInt(0);
                // 30 = 完成（按你实际接口定义）
                if (status == 30) {
                    String videoUrl = dataNode.get("video_url").asText("");
                    if (!videoUrl.isBlank() && videoUrl.startsWith("http")) {
                        videoUrlList.add(videoUrl);
                    }
                } else if (status != 10) {
                    String msg = dataNode.get("msg").asText("");
                    resultVO.setErrorMsg("任务异常：" + msg);
                }

            } catch (Exception e) {
                log.error("查询任务[{}]进度异常", taskId, e);
                resultVO.setErrorMsg("查询视频进度异常");
            } finally {
                // 【关键修复】强制关闭响应流，杜绝连接泄漏
                if (response != null) {
                    response.close();
                }
            }
        }

        // 防除0
        int finalProgress = totalTaskCount == 0 ? 0 : totalProgress / totalTaskCount;
        resultVO.setProgress(finalProgress);
        resultVO.setVideoProcess("Synthesizing");

        // 全部任务完成且地址数量匹配，切换为拼接状态
        if (finalProgress == 100 && videoUrlList.size() == totalTaskCount) {
            resultVO.setVideoProcess("Stitching");
        }

        // 【强制回传集合】无论有无数据，都把 list 塞回去，保证前端拿到数组而非 null
        resultVO.setVideoUrlList(videoUrlList);

        return CommonResult.success(resultVO);
    }

    @PostMapping("/splicing-video")
    @Operation(summary = "拼接数字人视频")
// 1. 改为接收包装VO  2. 加上 @RequestBody JSON解析
    public CommonResult<String> splicingDigitalVideo(
            @Valid @RequestBody VideoSplicingReqVO reqVO){

        List<String> videoUrlList = reqVO.getVideoUrlList();

        if (videoUrlList == null || videoUrlList.isEmpty()) {
            return CommonResult.error(400, "视频地址列表不能为空");
        }

        String taskId = UUID.randomUUID().toString();
        // 初始化任务状态存入 Redis
        saveTask(taskId, new TaskResult("PENDING", null, null));

        CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = VideoMergeUtil.mergeOnlineVideos(videoUrlList);
                String fileName = "digital_video_" + UUID.randomUUID() + ".mp4";
                String url = fileApi.createFile(bytes, fileName);
                saveTask(taskId, new TaskResult("DONE", url, null));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return CommonResult.success(taskId);
    }

    @PostMapping("/get-splicing-result")
    @Operation(summary = "获取拼接数字人视频结果")
    public CommonResult<String> getSplicingVideoResult(
            @Valid @RequestBody SplicingResultReqVO reqVO) {
        String taskId = reqVO.getTaskId();
        Long userId = reqVO.getUserId();

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
                JsonNode rootNode = objectMapper.readTree(result);
                String videoUrl = rootNode.get("result").asText();

                CompletableFuture.runAsync(() -> {
                    try {
                        MultipartFile file = FileUtil.urlToMultipartFile(videoUrl);
                        byte[] content = IoUtil.readBytes(file.getInputStream());
                        String fileName = file.getOriginalFilename();
                        if (pptHistoryApi.getPptHistoryByFileName(fileName) == null) {
                            String fileUrl = fileApi.createFile(content, fileName);
                            PptHistoryDTO pptHistoryDTO = new PptHistoryDTO()
                                    .setPptTitle(fileName)
                                    .setPptFile(fileUrl)
                                    .setClientUserId(userId)
                                    .setPptFiletype("1");
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

    private String getAccessToken() {
        try {
            OkHttpClient clientToken = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaTypeToken = MediaType.parse("application/json");
            okhttp3.RequestBody bodyToken = okhttp3.RequestBody.create(mediaTypeToken, "{\n" +
                    "    \"app_id\": \"" + appId + "\",\n" +
                    "    \"secret_key\": \"" + appKey + "\"\n" +
                    "}");
            Request requestToken = new Request.Builder()
                    .url("https://www.chanjing.cc/api/open/v1/access_token")
                    .method("POST", bodyToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response responseToken = clientToken.newCall(requestToken).execute();

            String string = responseToken.body().string();

            log.info("获取的sign请求：{}",string);


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(string);
            // 下面是解析 access_token 的核心代码（链式取值）
            String accessToken = rootNode
                    .path("data")               // 先拿到 data 节点
                    .path("access_token")       // 再从 data 里拿 access_token
                    .asText();                 // 转成字符串
            return accessToken;
        } catch (IOException e) {
            log.error("获取sign失败"+ e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String videoCreateTaskId(DigitalVideoReqDTO digitalVideoReqDTO, String accessToken){

        log.info("开始创建视频任务:背景图片：{},字幕：{},签名：{}",digitalVideoReqDTO.getOriginalImageUrl(),digitalVideoReqDTO.getText(),accessToken);
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\n" +
                    "  \"person\": {\n" +
                    "    \"id\": \"488eb1aba23841ed95f44f258589cd73\",\n" +
                    "    \"x\": 1290,\n" +
                    "    \"y\": 50,\n" +
                    "    \"width\": 607,\n" +
                    "    \"height\": 1080,\n" +
                    "    \"figure_type\": \"whole_body\",\n" +
                    "    \"drive_mode\":\"\"\n" +
                    "  },\n" +
                    "  \"audio\": {\n" +
                    "    \"tts\": {\n" +
                    "      \"text\": [\"" + digitalVideoReqDTO.getText() + "\"],\n" +
                    "      \"speed\": 1,\n" +
                    "      \"audio_man\": \"C-CASE-01103c0b2ff94d619b7a2f09c0b08b6f\"\n" +
                    "    },\n" +
                    "    \"wav_url\": \"\",\n" +
                    "    \"type\": \"tts\",\n" +
                    "    \"volume\": 100,\n" +
                    "    \"language\": \"cn\"\n" +
                    "  },\n" +
                    "  \"bg\": {\n" +
                    "    \"src_url\": \"" + digitalVideoReqDTO.getOriginalImageUrl() + "\",\n" +
                    "    \"x\": 0,\n" +
                    "    \"y\": 0,\n" +
                    "    \"height\": 1080,\n" +
                    "    \"width\": 1920\n" +
                    "  },\n" +
                    "  \"bg_color\": \"#d92127\",\n" +
                    "  \"screen_width\": 1920,\n" +
                    "  \"screen_height\": 1080,\n" +
                    "  \"subtitle_config\": {\n" +
                    "    \"x\": 460,\n" +
                    "    \"y\": 950,\n" +
                    "    \"show\": true,\n" +
                    "    \"width\": 1000,\n" +
                    "    \"height\": 200,\n" +
                    "    \"font_size\": 64,\n" +
                    "    \"color\": \"#FFFFFF\",\n" +
                    "    \"stroke_color\": \"#000000\",\n" +
                    "    \"stroke_width\": 7,\n" +
                    "    \"asr_type\": 0\n" +
                    "  }\n" +
                    "}");
            Request request = new Request.Builder()
                    .url("https://www.chanjing.cc/api/open/v1/create_video")
                    .method("POST", body)
                    .addHeader("access_token", accessToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            String json = response.body().string();
            log.info("获得的response：{}",json);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            String data = jsonNode.get("data").asText();
            log.info("获取的taskId：{}",data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveTask(String taskId, TaskResult taskResult) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(taskResult);

            redisTemplate.opsForValue().set(
                    TASK_KEY_PREFIX + taskId,
                    jsonStr,
                    TASK_EXPIRE_MINUTES,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            log.error("保存任务失败：{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
