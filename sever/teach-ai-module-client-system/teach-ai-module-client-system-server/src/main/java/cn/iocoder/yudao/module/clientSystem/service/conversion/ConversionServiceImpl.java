package cn.iocoder.teach-ai.module.clientSystem.service.conversion;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.conversion.ConversionMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.CONVERSION_NOT_EXISTS;

/**
 * 会话历史 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class ConversionServiceImpl implements ConversionService {

    @Resource
    private ConversionMapper conversionMapper;

    @Override
    public Long createConversion(ConversionSaveReqVO createReqVO) {
        // 插入
        ConversionDO conversion = BeanUtils.toBean(createReqVO, ConversionDO.class);
        conversionMapper.insert(conversion);

        // 返回
        return conversion.getId();
    }

    @Override
    public void updateConversion(ConversionSaveReqVO updateReqVO) {
        // 校验存在
        validateConversionExists(updateReqVO.getId());
        // 更新
        ConversionDO updateObj = BeanUtils.toBean(updateReqVO, ConversionDO.class);
        conversionMapper.updateById(updateObj);
    }

    @Override
    public void deleteConversion(Long id) {
        // 校验存在
        validateConversionExists(id);
        // 删除
        conversionMapper.deleteById(id);
    }

    @Override
        public void deleteConversionListByIds(List<Long> ids) {
        // 删除
        conversionMapper.deleteByIds(ids);
        }


    private void validateConversionExists(Long id) {
        if (conversionMapper.selectById(id) == null) {
            throw exception(CONVERSION_NOT_EXISTS);
        }
    }

    @Override
    public ConversionDO getConversion(Long id) {
        return conversionMapper.selectById(id);
    }

    @Override
    public ConversionDO getConversionByConversionId(Long conversionId) {
        return conversionMapper.selectByConversionId(conversionId);
    }

    @Override
    public PageResult<ConversionDTO> getConversionPage(ConversionPageReqVO pageReqVO) {
        return conversionMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ConversionDTO> getConversionRecentWeek() {
        return conversionMapper.selectListRecentWeek();
    }

}
