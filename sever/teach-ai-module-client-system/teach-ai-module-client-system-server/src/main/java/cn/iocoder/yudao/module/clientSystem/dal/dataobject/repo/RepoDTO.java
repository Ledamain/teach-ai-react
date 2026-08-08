package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo;

import lombok.Data;

@Data
public class RepoDTO extends RepoDO {
    /**
     * 知识库类别名称
     */
    private String repoCategoryName;
    /**
     * 学科文件夹名称
     */
    private String repoGroupName;
}
