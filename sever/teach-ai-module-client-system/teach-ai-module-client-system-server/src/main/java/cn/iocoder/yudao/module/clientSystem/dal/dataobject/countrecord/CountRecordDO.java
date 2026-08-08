package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 使用次数记录 DO
 *
 * @author waynelam
 */
@TableName("client_count_record")
@KeySequence("client_count_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountRecordDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 记录使用数量
     */
    private Long recordCount;
    /**
     * 客户端用户id
     */
    private Long userId;


}
