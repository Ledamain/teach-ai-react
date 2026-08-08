package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class CodeDataDTO implements Serializable {

    private String code;

    @JSONField(name = "time_expire")
    private int timeExpire;

}
