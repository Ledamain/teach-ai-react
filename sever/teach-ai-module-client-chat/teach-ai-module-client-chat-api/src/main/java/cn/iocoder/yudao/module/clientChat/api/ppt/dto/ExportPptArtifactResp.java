package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExportPptArtifactResp implements Serializable {
    private String exportTaskId;
}
