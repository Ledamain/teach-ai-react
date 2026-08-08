package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class GetPptArtifactExportResultResp implements Serializable {
    private List<String> exportFileLink;
}
