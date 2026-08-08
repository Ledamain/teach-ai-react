package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup;

import lombok.Data;

@Data
public class RepoGroupDTO extends RepoGroupDO{
    /**
     * 知识库类别名称
     */
    private String repoCategoryName;
}
