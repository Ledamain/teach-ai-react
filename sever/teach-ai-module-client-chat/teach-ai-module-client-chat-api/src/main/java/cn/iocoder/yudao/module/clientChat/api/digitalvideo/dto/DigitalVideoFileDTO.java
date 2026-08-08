package cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DigitalVideoFileDTO {

    private MultipartFile file;

}
