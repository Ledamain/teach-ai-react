package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents;

import lombok.Data;

@Data
public class ClassesStudentsDTO extends ClassesStudentsDO {
    /**
     * 客户端用户名称
     */
    private String nickname;
    /**
     * 客户端账号
     */
    private String clientUsername;
}
