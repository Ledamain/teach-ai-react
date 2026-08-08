package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

import lombok.Data;

import java.util.List;

@Data
public class DigitalVideoResultVO {

    // Synthesizing-》合成中、Stitching-》拼接中
    private String videoProcess;

    private Integer progress;

    private List<String> videoUrlList;

    private String errorMsg;

}
