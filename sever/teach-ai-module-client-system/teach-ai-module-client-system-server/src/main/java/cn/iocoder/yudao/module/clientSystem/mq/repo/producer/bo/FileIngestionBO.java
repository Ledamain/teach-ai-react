package cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.bo;

import lombok.Data;

@Data
public class FileIngestionBO {

    public static final String TOPIC = "REPO_SEND_TOPIC";

    private String kId;

    private String fileUrl;

}
