package cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessageDTO {

    /**
     * 提示词主键id
     */
    private Long id;
    /**
     * 系统提示词标题
     */
    private String systemMessageTitle;
    /**
     * 提示词内容
     */
    private String systemMessageText;
    /**
     * 系统提示词文件url地址
     */
    private String systemMessageTextUrl;
    /**
     * 启用状态0-启用1-禁止
     *
     * 枚举 {@link TODO chat_system_message_status 对应的类}
     */
    private String status;
    /**
     * 提示词是否存在0-存在1-不存在
     *
     * 枚举 {@link TODO chat_system_message_text 对应的类}
     */
    private String textStatus;


}
