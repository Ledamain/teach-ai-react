package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.loginlog;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 客户端登录日志 DO
 *
 * @author waynelam
 */
@TableName("client_login_log")
@KeySequence("client_login_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogDO extends BaseDO {

    /**
     * 登录日志id
     */
    @TableId
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户名称
     */
    private String nickname;
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    /**
     * 登录IP
     */
    private String loginIp;
    /**
     * 登录结果
     *
     * 枚举 {@link TODO client_login_result 对应的类}
     */
    private Long loginResult;


}
