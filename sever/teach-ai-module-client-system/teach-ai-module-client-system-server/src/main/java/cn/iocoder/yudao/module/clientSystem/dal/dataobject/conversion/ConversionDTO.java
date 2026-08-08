package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion;

import lombok.Data;

@Data
public class ConversionDTO extends ConversionDO {
    /**
     * 客户端账号名称
     */
    private String clientUserName;
}
