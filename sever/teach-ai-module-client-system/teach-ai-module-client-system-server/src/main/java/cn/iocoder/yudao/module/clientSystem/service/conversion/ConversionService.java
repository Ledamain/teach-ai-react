package cn.iocoder.teach-ai.module.clientSystem.service.conversion;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 会话历史 Service 接口
 *
 * @author waynelam
 */
public interface ConversionService {

    /**
     * 创建会话历史
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createConversion(@Valid ConversionSaveReqVO createReqVO);

    /**
     * 更新会话历史
     *
     * @param updateReqVO 更新信息
     */
    void updateConversion(@Valid ConversionSaveReqVO updateReqVO);

    /**
     * 删除会话历史
     *
     * @param id 编号
     */
    void deleteConversion(Long id);

    /**
    * 批量删除会话历史
    *
    * @param ids 编号
    */
    void deleteConversionListByIds(List<Long> ids);

    /**
     * 获得会话历史
     *
     * @param id 编号
     * @return 会话历史
     */
    ConversionDO getConversion(Long id);

    /**
     * 根据会话id获得会话历史
     *
      * @param conversionId 会话id
     * @return 会话历史
     */
    ConversionDO getConversionByConversionId(Long conversionId);

    /**
     * 获得会话历史分页
     *
     * @param pageReqVO 分页查询
     * @return 会话历史分页
     */
    PageResult<ConversionDTO> getConversionPage(ConversionPageReqVO pageReqVO);

    /**
     * 获得近一周
     *
     * @return 会话历史分页
     */
    List<ConversionDTO> getConversionRecentWeek();
}
