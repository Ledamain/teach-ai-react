package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * PPT历史记录 DO
 *
 * @author waynelam
 */
@TableName("client_ppt_history")
@KeySequence("client_ppt_history_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PptHistoryDO extends BaseDO {

    /**
     * ppt生成记录id
     */
    @TableId
    private Long id;
    /**
     * ppt生成记录标题
     */
    private String pptTitle;
    /**
     * ppt文件
     */
    private String pptFile;
    /**
     * 文件类型
     */
    private String pptFiletype;
    /**
     * 客户端用户id
     */
    private Long clientUserId;


}
