package cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRecommendRespDTO {
    private Long id;
    private String title;
    private String description;
    private String resourceType;
    private Long resourceId;
    private String reason;
}
