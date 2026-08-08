package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user;

import cn.iocoder.teach-ai.module.clientSystem.controller.client.user.vo.StudentsRespVO;
import lombok.Data;

@Data
public class StudentsDTO extends StudentsRespVO {

    /**
     * 班级id
     */
    private Long classesId;
    /**
     * 学生用户id
     */
    private Long studentUserId;

}
