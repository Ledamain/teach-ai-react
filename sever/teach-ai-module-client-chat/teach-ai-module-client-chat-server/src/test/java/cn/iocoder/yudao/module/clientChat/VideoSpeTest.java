package cn.iocoder.teach-ai.module.clientChat;

import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
public class VideoSpeTest {

    private static String ffmpegPath = "ffmpeg";
    // 转场时长 秒
    private static final double TRANS_DURATION = 0.8;
    // 统一视频规格
    private static final int TARGET_WIDTH = 1080;
    private static final int TARGET_HEIGHT = 1920;
    private static final int TARGET_FPS = 25;

    @Resource
    private FileApi fileApi;

    @Value("${ffmpeg.path:ffmpeg}")
    public void setFfmpegPath(String path) {
        VideoSpeTest.ffmpegPath = path;
    }

    @Test
    void speTest() {
        try {
            List<String> list = new ArrayList<>();
            list.add("https://res.chanjing.cc/chanjing/prod/dhaio/output/2026-05-26/2059274300191825920-1779804249-output.mp4");
            list.add("https://res.chanjing.cc/chanjing/prod/dhaio/output/2026-05-26/2059248129795039232-1779798006-output.mp4");
            byte[] bytes = mergeOnlineVideos(list);
            System.out.println(fileApi.createFile(bytes, "testvideoNew.mp4"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] mergeOnlineVideos(List<String> videoUrls) throws IOException {
        if (videoUrls == null || videoUrls.isEmpty()) {
            throw new IllegalArgumentException("视频列表不能为空");
        }

        Path tempDir = Files.createTempDirectory("video_merge_");
        List<Path> localFiles = new ArrayList<>();
        Path outputFile = tempDir.resolve("merged_output.mp4");

        try {
            for (String url : videoUrls) {
                Path localPath = tempDir.resolve(System.currentTimeMillis() + "_input.mp4");
                downloadFile(url, localPath);
                localFiles.add(localPath);
            }

            // 区分：2个视频用xfade中间转场，多个用通用拼接
            if (localFiles.size() == 2) {
                runTwoVideoXfade(localFiles, outputFile);
            } else {
                runFfmpegMultiWithTransition(localFiles, outputFile);
            }
            return Files.readAllBytes(outputFile);

        } catch (Exception e) {
            cleanTempDir(tempDir);
            throw e;
        } finally {
            cleanTempDir(tempDir);
        }
    }

    private static void downloadFile(String url, Path targetPath) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 双视频专用：中间叠化转场（xfade，效果最好）
     */
    private static void runTwoVideoXfade(List<Path> videoList, Path output) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-i");
        command.add(videoList.get(0).toAbsolutePath().toString());
        command.add("-i");
        command.add(videoList.get(1).toAbsolutePath().toString());

        // 纯字符串拼接，无任何 format 占位符
        String filter = "[0:v][1:v]xfade=transition=dissolve:duration=" + TRANS_DURATION + "[v];"
                + "[0:a][1:a]acrossfade=d=" + TRANS_DURATION + "[a]";

        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add("[v]");
        command.add("-map");
        command.add("[a]");

        // 性能参数
        command.add("-preset");
        command.add("ultrafast");
        command.add("-crf");
        command.add("28");
        command.add("-threads");
        command.add("0");
        command.add("-y");
        command.add(output.toAbsolutePath().toString());

        execProcess(command);
    }

    /**
     * 3个及以上视频通用拼接（首尾淡入淡出）
     */
    private static void runFfmpegMultiWithTransition(List<Path> videoList, Path output) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);

        for (Path path : videoList) {
            command.add("-i");
            command.add(path.toAbsolutePath().toString());
        }

        int videoNum = videoList.size();
        StringBuilder filterComplex = new StringBuilder();

        // 1. 统一分辨率、帧率、音频（纯字符串拼接，彻底移除 String.format）
        for (int i = 0; i < videoNum; i++) {
            // 视频标准化
            filterComplex.append("[").append(i).append(":v]scale=")
                    .append(TARGET_WIDTH).append(":").append(TARGET_HEIGHT)
                    .append(",fps=").append(TARGET_FPS)
                    .append(",setsar=1[v").append(i).append("];");
            // 音频标准化
            filterComplex.append("[").append(i).append(":a]aformat=sample_fmts=fltp:channel_layouts=stereo[a")
                    .append(i).append("];");
        }

        // 2. 组装流标签
        StringBuilder vSrc = new StringBuilder();
        StringBuilder aSrc = new StringBuilder();
        for (int i = 0; i < videoNum; i++) {
            vSrc.append("[v").append(i).append("]");
            aSrc.append("[a").append(i).append("]");
        }

        // 3. 拼接 + 整体淡入淡出
        filterComplex.append(vSrc)
                .append("concat=n=").append(videoNum).append(":v=1:a=1:unsafe=1")
                .append(",fade=t=in:st=0:d=").append(TRANS_DURATION)
                .append(",fade=t=out:d=").append(TRANS_DURATION).append("[outv];");

        filterComplex.append(aSrc)
                .append("concat=n=").append(videoNum).append(":v=0:a=1")
                .append(",afade=t=in:st=0:d=").append(TRANS_DURATION)
                .append(",afade=t=out:d=").append(TRANS_DURATION).append("[outa];");

        command.add("-filter_complex");
        command.add(filterComplex.toString());
        command.add("-map");
        command.add("[outv]");
        command.add("-map");
        command.add("[outa]");

        command.add("-preset");
        command.add("ultrafast");
        command.add("-crf");
        command.add("28");
        command.add("-threads");
        command.add("0");
        command.add("-y");
        command.add(output.toAbsolutePath().toString());

        execProcess(command);
    }

    /**
     * 统一执行 FFmpeg 进程 + 日志输出
     */
    private static void execProcess(List<String> command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 打印日志排错
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("FFmpeg LOG: " + line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg 执行失败，退出码：" + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("视频任务被中断", e);
        }
    }

    /**
     * 清理临时目录
     */
    public static void cleanTempDir(Path tempDir) {
        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException ignored) {
        }
    }

}
