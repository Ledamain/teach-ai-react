package cn.iocoder.teach-ai.framework.common.util.file;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

    public static MultipartFile urlToMultipartFile(String fileUrl) throws Exception {
        // ========== 核心修复：先判断 URL 不能为 null ==========
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("文件 URL 不能为空");
        }

        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream inputStream = conn.getInputStream()) {
            String contentType = conn.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String fileName = getFileNameFromUrl(fileUrl);
            if (fileName == null || fileName.isBlank()) {
                fileName = "unknown-file";
            }

            return new MockMultipartFile(
                    "file",
                    fileName,
                    contentType,
                    inputStream
            );
        } finally {
            conn.disconnect();
        }
    }

    private static String getFileNameFromUrl(String url) {
        if (url == null) return null;

        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }

        int lastSlash = url.lastIndexOf("/");
        if (lastSlash == -1) return url;

        return url.substring(lastSlash + 1);
    }
}
