package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import lombok.Data;

@Data
public class ChatParamDTO {

    private String taskId;
    private String token;
    private String outline;
    private String query;
    private Integer artifactId;
    private String exportTaskId;
    private Long clientUserId;

}
