package cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryFileSummaryRespDTO {
    private String memoryId;
    private List<FileIngestSummaryDTO> files;
    private int totalFiles;
    private int totalChunks;
}
