package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class VideoSplicingReqVO {
    @NotEmpty(message = "视频地址列表不能为空")
    private List<String> videoUrlList;
}
