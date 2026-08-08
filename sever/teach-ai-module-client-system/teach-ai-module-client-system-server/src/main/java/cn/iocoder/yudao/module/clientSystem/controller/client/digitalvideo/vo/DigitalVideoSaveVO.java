package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoReqDTO;
import lombok.Data;

import java.util.List;

@Data
public class DigitalVideoSaveVO {

    private List<DigitalVideoReqDTO> slides;

}
