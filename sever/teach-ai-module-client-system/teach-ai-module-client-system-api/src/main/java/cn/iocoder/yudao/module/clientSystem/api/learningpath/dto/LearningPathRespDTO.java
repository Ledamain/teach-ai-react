package cn.iocoder.teach-ai.module.clientSystem.api.learningpath.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathRespDTO {
    private Long id;
    private Long userId;
    private Long repoCategoryId;
    private String repoCategoryName;
    private String title;
    private String description;
    private String status;
    private Integer totalNodes;
    private Integer completedNodes;
    private LocalDateTime generatedAt;
    private LocalDateTime createTime;
}
