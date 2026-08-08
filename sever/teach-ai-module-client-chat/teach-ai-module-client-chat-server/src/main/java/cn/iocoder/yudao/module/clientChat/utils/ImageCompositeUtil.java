package cn.iocoder.teach-ai.module.clientChat.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompositeUtil {
    public static byte[] composite(BufferedImage bgImage, BufferedImage matImage,
                                   int x, int y, int width, int height,
                                   String format) throws IOException {
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
        return out.toByteArray();
    }

    /**
     * byte[] 转换为 BufferedImage
     * @param imageBytes 图片字节数组
     * @return 转换后的 BufferedImage
     * @throws IOException 流读取/图片格式异常
     */
    public static BufferedImage byteArrayToBufferedImage(byte[] imageBytes) throws IOException {
        // 自动关闭流的写法（最佳实践）
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            // 核心转换代码
            return ImageIO.read(bais);
        }
    }
}
