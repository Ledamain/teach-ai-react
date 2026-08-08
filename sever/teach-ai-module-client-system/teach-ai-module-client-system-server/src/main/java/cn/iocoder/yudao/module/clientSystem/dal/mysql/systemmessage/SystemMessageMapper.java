package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.systemmessage;

import java.util.*;

import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.systemmessage.SystemMessageDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo.*;

/**
 * 系统提示词 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SystemMessageMapper extends BaseMapperX<SystemMessageDO> {

    default PageResult<SystemMessageDO> selectPage(SystemMessagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SystemMessageDO>()
                .likeIfPresent(SystemMessageDO::getSystemMessageTitle, reqVO.getSystemMessageTitle())
                .likeIfPresent(SystemMessageDO::getSystemMessageText, reqVO.getSystemMessageText())
                .eqIfPresent(SystemMessageDO::getSystemMessageTextUrl, reqVO.getSystemMessageTextUrl())
                .eqIfPresent(SystemMessageDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SystemMessageDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SystemMessageDO::getTextStatus, reqVO.getTextStatus())
                .orderByDesc(SystemMessageDO::getId));
    }

    default List<SystemMessageDO> selectList(SystemMessagePageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<SystemMessageDO>()
                .likeIfPresent(SystemMessageDO::getSystemMessageTitle, reqVO.getSystemMessageTitle())
                .likeIfPresent(SystemMessageDO::getSystemMessageText, reqVO.getSystemMessageText())
                .eqIfPresent(SystemMessageDO::getSystemMessageTextUrl, reqVO.getSystemMessageTextUrl())
                .eqIfPresent(SystemMessageDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SystemMessageDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(SystemMessageDO::getTextStatus, reqVO.getTextStatus())
                .orderByDesc(SystemMessageDO::getId));
    }

}
