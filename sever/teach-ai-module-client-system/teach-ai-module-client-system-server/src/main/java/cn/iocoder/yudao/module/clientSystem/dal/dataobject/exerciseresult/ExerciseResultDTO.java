package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult;

import lombok.Data;

@Data
public class ExerciseResultDTO extends ExerciseResultDO{

    private String studentUserName;

    private String classesName;

    private String ClientNum;

}
