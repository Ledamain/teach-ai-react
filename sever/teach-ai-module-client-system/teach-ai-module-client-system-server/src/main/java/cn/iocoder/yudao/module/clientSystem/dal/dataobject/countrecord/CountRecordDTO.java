package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord;

import lombok.Data;

@Data
public class CountRecordDTO extends CountRecordDO {
    /**
     * 客户端账号名称
     */
    private String clientUserName;
}
