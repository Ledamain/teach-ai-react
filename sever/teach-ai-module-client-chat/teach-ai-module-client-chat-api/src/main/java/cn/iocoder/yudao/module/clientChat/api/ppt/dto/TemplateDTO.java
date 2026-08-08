package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateDTO {
    private String id;
    private String previewUrl;
}
