package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 知识库 DO
 *
 * @author waynelam
 */
@TableName("client_repo")
@KeySequence("client_repo_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoDO extends BaseDO {

    /**
     * 知识库id
     */
    @TableId
    private Long id;
    /**
     * 知识库标题
     */
    private String repoTitle;
    /**
     * 知识库文件链接
     */
    private String repoFile;
    /**
     * 知识库描述
     */
    private String repoDesp;
    /**
     * 知识库类别id
     */
    private Long repoCategoryId;
    /**
     * 学科文件夹id
     */
    private Long repoGroupId;
    /**
     * 启用状态
     */
    private String repoStatus;


}
