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
public class FileIngestSummaryDTO {
    private String fileName;
    private String fileType;
    private int chunkCount;
    private String summary;
    private List<String> keyPoints;
    private String fileUrl;
}
