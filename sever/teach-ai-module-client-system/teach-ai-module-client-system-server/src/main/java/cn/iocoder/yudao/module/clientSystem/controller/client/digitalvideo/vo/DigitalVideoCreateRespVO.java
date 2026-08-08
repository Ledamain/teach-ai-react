package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

import lombok.Data;

import java.util.List;

@Data
public class DigitalVideoCreateRespVO {

    private List<String> taskIds;

    private String sign;

}
