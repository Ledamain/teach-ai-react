package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class DesignPageInfoDTO implements Serializable {

    private Long total;

    @JSONField(name = "current_page")
    private Long currentPage;

    @JSONField(name = "page_size")
    private String pageSize;

}
