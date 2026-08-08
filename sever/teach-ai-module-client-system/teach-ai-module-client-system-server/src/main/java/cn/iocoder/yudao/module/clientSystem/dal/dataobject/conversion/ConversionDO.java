package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 会话历史 DO
 *
 * @author waynelam
 */
@TableName("client_conversion")
@KeySequence("client_conversion_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionDO extends BaseDO {

    /**
     * 会话历史id
     */
    @TableId
    private Long id;
    /**
     * 会话id
     */
    private String conversionId;
    /**
     * 客户端账号id
     */
    private Long clientUserId;
    /**
     * 会话标题
     */
    private String title;


}
