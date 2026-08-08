package cn.iocoder.teach-ai.module.system.dal.mysql.logger;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import cn.iocoder.teach-ai.module.system.dal.dataobject.logger.LoginLogDO;
import cn.iocoder.teach-ai.module.system.enums.logger.LoginResultEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapperX<LoginLogDO> {

    default PageResult<LoginLogDO> selectPage(LoginLogPageReqVO reqVO) {
        LambdaQueryWrapperX<LoginLogDO> query = new LambdaQueryWrapperX<LoginLogDO>()
                .likeIfPresent(LoginLogDO::getUserIp, reqVO.getUserIp())
                .likeIfPresent(LoginLogDO::getUsername, reqVO.getUsername())
                .betweenIfPresent(LoginLogDO::getCreateTime, reqVO.getCreateTime());
        if (Boolean.TRUE.equals(reqVO.getStatus())) {
            query.eq(LoginLogDO::getResult, LoginResultEnum.SUCCESS.getResult());
        } else if (Boolean.FALSE.equals(reqVO.getStatus())) {
            query.gt(LoginLogDO::getResult, LoginResultEnum.SUCCESS.getResult());
        }
        query.orderByDesc(LoginLogDO::getId); // 降序
        return selectPage(reqVO, query);
    }

}
