package cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathNodeRespDTO {
    private Long id;
    private Long pathId;
    private Integer orderIndex;
    private String title;
    private String description;
    private String resourceType;
    private Long resourceId;
    private String resourceName;
    private Long dependsOn;
    private String status;
    private Integer estimatedMinutes;
}
