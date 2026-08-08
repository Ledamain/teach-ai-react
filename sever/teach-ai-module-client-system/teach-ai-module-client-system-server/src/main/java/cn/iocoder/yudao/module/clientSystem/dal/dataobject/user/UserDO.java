package cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.teach-ai.framework.mybatis.core.dataobject.BaseDO;

/**
 * 客户端用户 DO
 *
 * @author 芋道源码
 */
@TableName("client_user")
@KeySequence("client_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDO extends BaseDO {

    /**
     * 客户端用户id
     */
    @TableId
    private Long id;
    /**
     * 客户端用户名称
     */
    private String nickname;
    /**
     * 客户端用户头像
     */
    private String clientAvator;
    /**
     * 客户端账号
     */
    private String clientUsername;
    /**
     * 客户端账号密码
     */
    private String clientPassword;
    /**
     * 客户端用户角色
     */
    private String clientRole;
    /**
     * 客户端用户性别
     */
    private String clientGender;
    /**
     * 学号/工号
     */
    private String clientNum;
    /**
     * 手机号
     */
    private String clientTel;
    /**
     * 最后登陆时间
     */
    private LocalDateTime lastLoginTime;


}
