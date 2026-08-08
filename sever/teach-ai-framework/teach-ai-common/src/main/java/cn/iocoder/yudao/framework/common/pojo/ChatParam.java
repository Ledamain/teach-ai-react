package cn.iocoder.teach-ai.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description="聊天参数")
@Data
public class ChatParam {

    private String memoryId;

    private String prompt;

    private List<String> knowledgeIds;

    /** 学生用户ID（用于画像提取） */
    private Long userId;

}
