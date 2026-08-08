package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

import lombok.Data;

import java.util.List;

@Data
public class DigitalVideoAiGenerateSaveVO {

    private List<SlideItem> slides;

    // 内部类：对应每个 { originalImageUrl: string }
    @Data
    public static class SlideItem {
        private String originalImageUrl;
    }

}
