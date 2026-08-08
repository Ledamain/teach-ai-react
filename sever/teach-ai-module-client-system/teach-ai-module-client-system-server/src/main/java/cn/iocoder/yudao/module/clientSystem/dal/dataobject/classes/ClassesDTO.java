package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes;

import lombok.Data;

import java.util.List;

@Data
public class ClassesDTO extends ClassesDO {
    /**
     * 客户端用户名称
     */
    private String nickname;
    /**
     * 客户端账号
     */
    private String clientUsername;
    /**
     * 知识库类别id列表
     */
    private List<String> repoCategoryIdsList;
}
