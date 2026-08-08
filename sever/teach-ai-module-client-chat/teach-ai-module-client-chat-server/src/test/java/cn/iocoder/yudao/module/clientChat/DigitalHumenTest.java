package cn.iocoder.teach-ai.module.clientChat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoReqDTO;
import cn.iocoder.teach-ai.module.clientChat.utils.ImageCompositeUtil;
import cn.iocoder.teach-ai.module.clientChat.utils.PptxToImageUtil;
import cn.iocoder.teach-ai.framework.common.util.video.VideoMergeUtil;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

@SpringBootTest
@Slf4j
public class DigitalHumenTest {

    @Resource
    private FileApi fileApi;

    @Test
    void digitalJwt(){
        String sig = DigitalHumenTest.createSig("1498738228423954432", "aca9d854-637c-4b73-a323-fd6b215b326e", 3600);
        System.out.println("sign：" + sig);
    }

    public static String createSig(String appId, String appKey, int sigExp) {
        // 过期时间：当前时间 + sigExp 秒
        Date expiresDate = DateUtil.offsetSecond(new Date(), sigExp);

        // 创建 HMAC256 签名器（和原代码完全一致）
        JWTSigner signer = JWTSignerUtil.hs256(appKey.getBytes());

        return JWT.create()
                .setIssuedAt(new Date())        // 签发时间
                .setExpiresAt(expiresDate)      // 过期时间
                .setPayload("appId", appId)     // 自定义参数
                .sign(signer);                  // 传入签名器
    }

    // 获取token
    @Test
    void digitalVideoTokenTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n" +
                "    \"app_id\": \"b7647660\",\n" +
                "    \"secret_key\": \"689f52697e42461ab1a280b243248dad\"\n" +
                "}");
        Request request = new Request.Builder()
                .url("https://www.chanjing.cc/api/open/v1/access_token")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println("响应结果："+response.body().string());
    }

    @Test
    void digitalVideoTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n" +
                "  \"person\": {\n" +
                "    \"id\": \"C-3f76e3168a05455bb7ea507b82414e5a\",\n" +
                "    \"x\": 1290,\n" +
                "    \"y\": 50,\n" +
                "    \"width\": 607,\n" +
                "    \"height\": 1080,\n" +
                "    \"figure_type\": \"whole_body\",\n" +
                "    \"drive_mode\":\"\"\n" +
                "  },\n" +
                "  \"audio\": {\n" +
                "    \"tts\": {\n" +
                "      \"text\": [\"你好，这是测试蝉镜数字人,黄河之水天上来\"],\n" +
                "      \"speed\": 1,\n" +
                "      \"audio_man\": \"C-5b1abcf58b124cbb854a483078c983a9\"\n" +
                "    },\n" +
                "    \"wav_url\": \"\",\n" +
                "    \"type\": \"tts\",\n" +
                "    \"volume\": 100,\n" +
                "    \"language\": \"cn\"\n" +
                "  },\n" +
                "  \"bg\": {\n" +
                "    \"src_url\": \"https://teach-ai.tos-cn-beijing.volces.com/20260526/temp_89a613c1-e638-4885-9087-5090324cdc9d_1779803651723.png?X-Tos-Algorithm=TOS4-HMAC-SHA256&X-Tos-Content-Sha256=UNSIGNED-PAYLOAD&X-Tos-Credential=AKTP28B9S1l0lqp6iIaVWbG69Z397yueiPLw1P1EbhD1rBy%2F20260526%2Fcn-beijing%2Ftos%2Frequest&X-Tos-Date=20260526T140316Z&X-Tos-Expires=3600&X-Tos-SignedHeaders=host&X-Tos-Security-Token=nChBUMWx3T0xzRTdjcXpSQ2tM.CiQKEGN4Ulp4Qnl4ZDhqZXkxSlMSECeJtFrQX0uWjO5lpdjn1mIQg8PW0AYYk9_W0AYg_ZPp7AcoATD9k-nsBzoEcm9vdEIDdG9zUghjaHVhbmd5dVgBYAE.hBTQKsTHI47-Rmlo58V81oFnPKzhl9B0T1jf53BzsrVKQtrVcmFVL4LPKE_r5Ui1Ndqcs36zrQTjRkGMJlintg&X-Tos-Signature=cdbca63052be7d448e732e3a53599b0f9b18ef7657a3b118fd840313f2555dcc\",\n" +
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
                .addHeader("access_token", "tGQu+f2DmcKNu70IU0X8Y09hlDcrQhoD41/+0oyR+HNuHJUbr+gSJJVRs/DjyaiR")
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    // 获取结果
    @Test
    void getResult() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url("https://www.chanjing.cc/api/open/v1/video?id=2059660302392233984")
                .get()
                .addHeader("access_token", "tGQu+f2DmcKNu70IU0X8Y09hlDcrQhoD41/+0oyR+HNuHJUbr+gSJJVRs/DjyaiR")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }


    @Test
    void getVideoTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"page\":1,\"page_size\":5}");
        Request request = new Request.Builder()
                .url("https://www.chanjing.cc/api/open/v1/video_list")
                .method("POST", body)
                .addHeader("access_token", "DHxApf9a6wXG5TpgnHuMC9C6qsJwUcDhSiJBa8wDPkQbcz322hjWezNsDyQKdGtx")
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                System.out.println("响应结果：" + response.body().string());
            }
        }
    }

    @Test
    void getVideoListTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // 1. 构建URL并拼接Query Params
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.chanjing.cc/api/open/v1/video_list").newBuilder();
        // 分页参数
        urlBuilder.addQueryParameter("id", "2059274300191825920");

        // 2. 构建GET请求
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .method("GET", null) // GET请求无需RequestBody
                .addHeader("Content-Type", "application/json")
                .addHeader("access_token", "DHxApf9a6wXG5TpgnHuMC9C6qsJwUcDhSiJBa8wDPkQbcz322hjWezNsDyQKdGtx")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                System.out.println("响应结果：" + response.body().string());
            }
        }
    }

    @Test
    void digitalListTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // 1. 构建URL并拼接Query Params
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.chanjing.cc/api/open/v1/list_common_dp").newBuilder();
        // 分页参数
        urlBuilder.addQueryParameter("page", "1");
        urlBuilder.addQueryParameter("size", "10");
        // 排序参数（可选，不传则默认按id升序；示例：按最新排序，也可改为hottest）
        urlBuilder.addQueryParameter("sort", "latest");
        // 标签ID参数（示例：同时包含标签1和2的交集）
        urlBuilder.addQueryParameter("tag_ids", "23,73,69,25");

        // 2. 构建GET请求
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .method("GET", null) // GET请求无需RequestBody
                .addHeader("Content-Type", "application/json")
                .addHeader("access_token", "/1MwsH+8DeDHayMzi+wyqoFDppmQcZ6Uz4EDy76Xm2ASm9OUSA0npmL+5109vXam")
                .build();

        // 3. 执行请求并打印响应
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                System.out.println("响应结果：" + response.body().string());
            }
        }
    }

    @Test
    void digitalTagTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // 1. 构建URL并拼接Query Params
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.chanjing.cc/api/open/v1/tag_list").newBuilder();
        // 分页参数
//        urlBuilder.addQueryParameter("page", "1");
//        urlBuilder.addQueryParameter("size", "10");
//        // 排序参数（可选，不传则默认按id升序；示例：按最新排序，也可改为hottest）
//        urlBuilder.addQueryParameter("sort", "latest");
        // 标签ID参数（示例：同时包含标签1和2的交集）
//        urlBuilder.addQueryParameter("tag_ids", "1,2");

        // 2. 构建GET请求
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .method("GET", null) // GET请求无需RequestBody
                .addHeader("Content-Type", "application/json")
                .addHeader("access_token", "MndhHPfDuFsGj2y0MeueCbCHoSfkiC+HyJMbNCkCh4vMPLaOUcN9GRGiFv26YNzL")
                .build();

        // 3. 执行请求并打印响应
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                System.out.println("响应结果：" + response.body().string());
            }
        }
    }


    @Test
    void imageTest() throws IOException {
//        MultipartFile background;
//        MultipartFile material;
        URL background = new URL("https://teach-ai.tos-cn-beijing.volces.com/VideoBG2.png");
        URL material = new URL("https://teach-ai.tos-cn-beijing.volces.com/compositeTest.jpg");
        int x = 50;
        int y = 135;
        int width = 1215;
        int height = 671;
        String format = "png";

        BufferedImage bgImage = ImageIO.read(background);
        BufferedImage matImage = ImageIO.read(material);

        // 缩放素材
        if (width > 0 && height > 0) {
            Image scaled = matImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(scaled, 0, 0, null);
            g.dispose();
            matImage = resized;
        }

        // 合成
        Graphics2D g2d = bgImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(matImage, x, y, null);
        g2d.dispose();

        // 输出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bgImage, format, out);
        fileApi.createFile(out.toByteArray(), "temp-" + LocalDateTimeUtil.now() + ".png");

    }

    @Test
    void pptUploadTest() throws Exception {

        MultipartFile multipartFile = FileUtil.urlToMultipartFile("https://teach-ai.tos-cn-beijing.volces.com/20260517/9008193781j7m58661_1779008340855.pptx");

        URL background = new URL("https://teach-ai.tos-cn-beijing.volces.com/VideoBG2.png");
        BufferedImage bgImage = ImageIO.read(background);

        URL digital = new URL("https://res.chanjing.cc/chanjing/video_matting/2026-02-13/b965447c4e5fdb3a37561c4f65cbcdc4.png");
        BufferedImage digitalImage = ImageIO.read(digital);

        ArrayList<String> list = new ArrayList<>();
        List<BufferedImage> bufferedImages = PptxToImageUtil.pptxToImages(multipartFile);
        bufferedImages.forEach(bufferedImage -> {
            try {
                byte[] image = ImageCompositeUtil.composite(bgImage, bufferedImage, 50, 135, 1215, 671, "png");
                BufferedImage toBufferedImage = ImageCompositeUtil.byteArrayToBufferedImage(image);
                byte[] composite = ImageCompositeUtil.composite(toBufferedImage, digitalImage, 1290, 50, 607, 1080, "png");
                String url = fileApi.createFile(composite, "temp_" + UUID.randomUUID() + ".png");
                list.add(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        list.forEach(url -> {
            System.out.println(url);
        });
    }

    @Test
    void videoMergeTest() throws IOException {
        List<String> list = new ArrayList<>();
        list.add("https://res.chanjing.cc/chanjing/prod/dhaio/output/2026-05-26/2059274300191825920-1779804249-output.mp4");
        list.add("https://res.chanjing.cc/chanjing/prod/dhaio/output/2026-05-26/2059248129795039232-1779798006-output.mp4");
        byte[] output = VideoMergeUtil.mergeOnlineVideos(list);
        System.out.println(fileApi.createFile(output, "testvideo.mp4"));
    }

    @Test
    void videoTest(){
        DigitalVideoReqDTO digitalVideoReqDTO = new DigitalVideoReqDTO().setOriginalImageUrl("https://teach-ai.tos-cn-beijing.volces.com/VideoBG2.png").setText("你好，我是数字人老师刘嘉欣");
        System.out.println(videoCreateTaskId(digitalVideoReqDTO, "dSKdcnrarm459AXhfv8Rie3BLQpryTOx8LOibCumLCUO4/dsDKY855UQ6rvnMy38"));
    }

    private String videoCreateTaskId(DigitalVideoReqDTO digitalVideoReqDTO, String accessToken){

        log.info("开始创建视频任务:背景图片：{},字幕：{},签名：{}",digitalVideoReqDTO.getOriginalImageUrl(),digitalVideoReqDTO.getText(),accessToken);
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\n" +
                    "  \"person\": {\n" +
                    "    \"id\": \"C-3f76e3168a05455bb7ea507b82414e5a\",\n" +
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
                    "      \"audio_man\": \"C-5b1abcf58b124cbb854a483078c983a9\"\n" +
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

    // 获得定制数字人列表
    @Test
    void getPrivateVideoTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"page\":1,\"page_size\":50}");
        Request request = new Request.Builder()
                .url("https://www.chanjing.cc/api/open/v1/list_customised_person")
                .method("POST", body)
                .addHeader("access_token", "dSKdcnrarm459AXhfv8Rie3BLQpryTOx8LOibCumLCUO4/dsDKY855UQ6rvnMy38")
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                System.out.println("响应结果：" + response.body().string());
            }
        }
    }


}
