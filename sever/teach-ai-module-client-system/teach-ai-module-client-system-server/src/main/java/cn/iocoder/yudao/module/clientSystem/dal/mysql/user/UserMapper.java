package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.user;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.*;

/**
 * 客户端用户 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {

    default PageResult<UserDO> selectPage(UserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getNickname, reqVO.getNickname())
                .eqIfPresent(UserDO::getClientAvator, reqVO.getClientAvator())
                .likeIfPresent(UserDO::getClientUsername, reqVO.getClientUsername())
                .eqIfPresent(UserDO::getClientPassword, reqVO.getClientPassword())
                .eqIfPresent(UserDO::getClientRole, reqVO.getClientRole())
                .eqIfPresent(UserDO::getClientGender, reqVO.getClientGender())
                .likeIfPresent(UserDO::getClientNum, reqVO.getClientNum())
                .likeIfPresent(UserDO::getClientTel, reqVO.getClientTel())
                .betweenIfPresent(UserDO::getLastLoginTime, reqVO.getLastLoginTime())
                .betweenIfPresent(UserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserDO::getId));
    }

}
