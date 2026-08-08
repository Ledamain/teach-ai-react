package cn.iocoder.teach-ai.module.clientChat.api.video.dto;

import lombok.Data;

@Data
public class VideoChatParmDTO {

    private Long userId;

    private String prompt;

    private String aspectRatio;

    private String duration;

    private String audioUrl;

}
