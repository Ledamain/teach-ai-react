package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.systemmessage;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 系统提示词 DO
 *
 * @author 芋道源码
 */
@TableName("client_system_message")
@KeySequence("client_system_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessageDO extends BaseDO {

    /**
     * 提示词主键id
     */
    @TableId
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
