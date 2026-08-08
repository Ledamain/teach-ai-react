package cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathGenerateReqDTO {
    private Long userId;
    private Long repoCategoryId;
    private String repoCategoryName;
    private String title;
    private String description;
    private List<PathNodeDTO> nodes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathNodeDTO {
        private Integer orderIndex;
        private String title;
        private String description;
        private String resourceType;
        private Integer dependsOnOrder; // 前置节点序号（1-based），null表示无前置
        private Integer estimatedMinutes;
    }
}
