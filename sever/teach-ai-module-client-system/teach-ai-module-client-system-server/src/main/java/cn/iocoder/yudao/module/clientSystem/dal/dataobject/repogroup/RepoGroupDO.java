package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程文件夹 DO
 *
 * @author waynelam
 */
@TableName("client_repo_group")
@KeySequence("client_repo_group_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoGroupDO extends BaseDO {

    /**
     * 课程文件夹id
     */
    @TableId
    private Long id;
    /**
     * 课程文件夹名称
     */
    private String repoGroupName;
    /**
     * 学科id
     */
    private Long repoCategoryId;
    /**
     * 课程文件夹描述
     */
    private String repoGroupDescription;


}
