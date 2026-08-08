package cn.iocoder.teach-ai.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="ppt生成参数")
@Data
public class PptQuery {

    private String query;

    private String fileName;

    private String pptMemoryId;

}
