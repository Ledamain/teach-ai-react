package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.loginlog;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.loginlog.LoginLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo.*;

/**
 * 客户端登录日志 Mapper
 *
 * @author waynelam
 */
@Mapper
public interface LoginLogMapper extends BaseMapperX<LoginLogDO> {

    default PageResult<LoginLogDO> selectPage(LoginLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LoginLogDO>()
                .likeIfPresent(LoginLogDO::getUsername, reqVO.getUsername())
                .likeIfPresent(LoginLogDO::getNickname, reqVO.getNickname())
                .betweenIfPresent(LoginLogDO::getLoginTime, reqVO.getLoginTime())
                .eqIfPresent(LoginLogDO::getLoginResult, reqVO.getLoginResult())
                .betweenIfPresent(LoginLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LoginLogDO::getId));
    }

}
