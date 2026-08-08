package cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileIngestionDTO {

    private MultipartFile file;

    private String memoryId;

    private String kId;

    private String fileUrl;

}
