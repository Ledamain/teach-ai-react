package cn.iocoder.teach-ai.framework.common.util.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class VideoMergeUtil {

    // 兜底绝对路径，防止配置未生效
    private static String ffmpegPath = "/usr/bin/ffmpeg";

    @Value("${ffmpeg.path:/usr/bin/ffmpeg}")
    public void setFfmpegPath(String path) {
        VideoMergeUtil.ffmpegPath = path;
        log.info("FFmpeg工具路径已配置：{}", ffmpegPath);
    }

    /**
     * 在线视频拼接，返回合并后视频字节数组
     */
    public static byte[] mergeOnlineVideos(List<String> videoUrls) throws IOException {
        if (videoUrls == null || videoUrls.isEmpty()) {
            throw new IllegalArgumentException("视频列表不能为空");
        }
        log.info("开始拼接在线视频，共{}个视频", videoUrls.size());

        Path tempDir = Files.createTempDirectory("video_merge_");
        List<Path> localFiles = new ArrayList<>();
        Path outputFile = tempDir.resolve("merged_output.mp4");

        try {
            // 1. 下载所有远程视频到临时目录
            for (int i = 0; i < videoUrls.size(); i++) {
                Path localPath = tempDir.resolve("input_" + i + ".mp4");
                log.info("下载第{}个视频：{} -> {}", i + 1, videoUrls.get(i), localPath);
                downloadFile(videoUrls.get(i), localPath);
                localFiles.add(localPath);
            }

            // 2. 生成ffmpeg concat文件列表
            Path listFile = tempDir.resolve("filelist.txt");
            try (var writer = Files.newBufferedWriter(listFile)) {
                for (Path f : localFiles) {
                    String absPath = f.toAbsolutePath().toString();
                    writer.write("file '" + absPath + "'\n");
                }
            }
            log.info("生成拼接文件清单：{}", listFile.toAbsolutePath());

            // 3. 执行ffmpeg拼接命令
            runFfmpeg(listFile, outputFile);
            log.info("视频拼接完成，读取合并文件字节");

            // 4. 返回视频二进制
            return Files.readAllBytes(outputFile);

        } catch (Exception e) {
            log.error("视频拼接流程异常", e);
            throw e;
        } finally {
            // 清理临时文件
            cleanTempDir(tempDir);
            log.info("临时目录清理完成");
        }
    }

    /**
     * 下载网络文件到本地临时文件
     */
    private static void downloadFile(String url, Path targetPath) throws IOException {
        try (var in = new URL(url).openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 执行ffmpeg拼接命令，修复缓冲区阻塞、打印完整日志
     */
    private static void runFfmpeg(Path listFile, Path outputFile) throws IOException {
        List<String> command = Arrays.asList(
                ffmpegPath,
                "-f", "concat",
                "-safe", "0",
                "-i", listFile.toAbsolutePath().toString(),
                "-c", "copy",
                "-y", // 自动覆盖输出文件，无需交互确认
                outputFile.toAbsolutePath().toString()
        );
        log.info("执行FFmpeg命令：{}", command);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // 标准错误合并到标准输出
        Process process = pb.start();

        // 新开线程持续读取输出流，解决缓冲区满阻塞问题
        new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.info("[FFmpeg日志] {}", line);
                }
            } catch (IOException e) {
                log.error("读取FFmpeg输出流异常", e);
            }
        }, "ffmpeg-log-read-thread").start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg执行失败，退出码: " + exitCode + "，请查看上方FFmpeg日志定位原因");
            }
            log.info("FFmpeg执行成功，退出码：0");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("视频拼接任务被线程中断", e);
        }
    }

    /**
     * 递归删除临时目录所有文件
     */
    public static void cleanTempDir(Path tempDir) {
        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        boolean del = file.delete();
                        if (!del) {
                            log.warn("临时文件删除失败：{}", file.getAbsolutePath());
                        }
                    });
        } catch (IOException ignored) {
            log.warn("清理临时目录异常", ignored);
        }
    }
}
