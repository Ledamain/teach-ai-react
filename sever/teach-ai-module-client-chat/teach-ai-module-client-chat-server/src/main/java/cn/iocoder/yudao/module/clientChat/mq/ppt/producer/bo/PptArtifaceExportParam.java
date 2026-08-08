package cn.iocoder.teach-ai.module.clientChat.mq.ppt.producer.bo;

import lombok.Data;

@Data
public class PptArtifaceExportParam {
    public static final String TOPIC = "PPT_HISTORY_CREATE_SEND_TOPIC";

    private Long studentUserId;

    private String exportTaskId;

}
