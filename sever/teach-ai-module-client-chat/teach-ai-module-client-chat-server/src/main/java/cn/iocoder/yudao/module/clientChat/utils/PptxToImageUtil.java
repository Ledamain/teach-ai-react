package cn.iocoder.teach-ai.module.clientChat.utils;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * PPTX 转图片工具类（支持 MultipartFile）
 * 仅支持 .pptx 格式（PPT 不支持）
 */
public class PptxToImageUtil {

    /**
     * 将 MultipartFile 中的 PPTX 转换为图片列表（返回 BufferedImage）
     * @param multipartFile 上传的 PPTX 文件
     * @return 每页幻灯片对应的图片对象
     * @throws IOException IO异常
     */
    public static List<BufferedImage> pptxToImages(MultipartFile multipartFile) throws IOException {
        List<BufferedImage> imageList = new ArrayList<>();

        // 1. 将 MultipartFile 转为输入流，读取 PPTX
        try (InputStream inputStream = multipartFile.getInputStream();
             XMLSlideShow ppt = new XMLSlideShow(inputStream)) {

            // 获取所有幻灯片
            List<XSLFSlide> slides = ppt.getSlides();
            Dimension pageSize = ppt.getPageSize();
            int width = (int) pageSize.getWidth();
            int height = (int) pageSize.getHeight();

            // 2. 遍历每一页，渲染成图片
            for (XSLFSlide slide : slides) {
                // 创建图片缓冲区
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();

                // 高清渲染
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // 绘制幻灯片到图片
                slide.draw(graphics);
                graphics.dispose();

                imageList.add(image);
            }
        }
        return imageList;
    }

    /**
     * 保存图片到本地（工具方法）
     * @param image 图片对象
     * @param filePath 保存路径（如：D:/ppt/1.png）
     * @throws IOException IO异常
     */
    public static void saveImage(BufferedImage image, String filePath) throws IOException {
        ImageIO.write(image, "png", new File(filePath));
    }

    // 将 BufferedImage 转为 Base64
    public static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
