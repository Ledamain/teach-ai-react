package cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto;

import lombok.Data;

@Data
public class DigitalVideoReqDTO {

    private String originalImageUrl;

    private String imageUrl;

    private String text;

    private String language;

}
