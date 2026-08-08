package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory;

import lombok.Data;

@Data
public class PptHistoryDTO extends PptHistoryDO {
    /**
     * 客户端用户名称
     */
    private String nickname;
}
