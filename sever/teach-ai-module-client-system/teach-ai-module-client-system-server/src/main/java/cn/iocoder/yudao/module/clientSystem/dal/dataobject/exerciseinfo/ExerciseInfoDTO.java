package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseInfoDTO extends ExerciseInfoDO {
    /**
     * 知识库类别名称
     */
    private String repoCategoryName;
    /**
     * 客户端用户名称
     */
    private String nickname;
    /**
     * 班级id列表
     */
    private List<String> classesIdsList;
}
