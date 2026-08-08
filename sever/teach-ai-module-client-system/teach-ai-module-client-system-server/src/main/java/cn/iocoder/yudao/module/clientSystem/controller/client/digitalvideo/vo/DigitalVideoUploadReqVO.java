package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalVideoUploadReqVO {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "文件目录", example = "XXX/YYY")
    private String directory;

    @AssertTrue(message = "文件目录不正确")
    @JsonIgnore
    public boolean isDirectoryValid() {
        return isDirectoryValid(directory);
    }

    public static boolean isDirectoryValid(String directory) {
        // 1. 不能包含 .. 防止目录穿越
        // 2. 不能以 / 或 \ 开头，防止上传到根目录
        return !StrUtil.contains(directory, "..")
                && !StrUtil.startWithAny(directory, "/", "\\");
    }

}
