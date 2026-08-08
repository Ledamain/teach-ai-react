package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo;

import lombok.Data;

@Data
public class RepoDeleteDTO {

    public static final String TOPIC = "REPO_DELETE_SEND_TOPIC";

    private String id;

    private String fileName;

}
