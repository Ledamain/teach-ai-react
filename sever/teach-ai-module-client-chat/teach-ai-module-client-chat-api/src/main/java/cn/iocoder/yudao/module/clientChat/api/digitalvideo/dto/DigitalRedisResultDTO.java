package cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto;

import lombok.Data;

import java.util.List;

@Data
public class DigitalRedisResultDTO {

    private String taskStatus;

    private List<DigitalVideoReqDTO> result;

}
