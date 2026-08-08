package cn.iocoder.teach-ai.module.clientSystem.api.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 画像历史快照响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileHistoryRespDTO {

    private Long id;
    private Long userId;
    private Integer profileVersion;
    private String memoryId;
    private String snapshotJson;
    private String changeSummary;
    private LocalDateTime createTime;
}
