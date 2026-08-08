package cn.iocoder.teach-ai.module.clientSystem.api.conversion;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo.ConversionSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDO;
import cn.iocoder.teach-ai.module.clientSystem.service.conversion.ConversionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class ConversionApiImpl implements ConversionApi{

    @Resource
    private ConversionService conversionService;

    @Override
    public CommonResult<Long> createConversion(ConversionDTO conversionDTO) {

        ConversionSaveReqVO bean = BeanUtils.toBean(conversionDTO, ConversionSaveReqVO.class);
        return CommonResult.success(conversionService.createConversion(bean));
    }

    @Override
    public CommonResult<ConversionDTO> getConversionByConversionId(Long conversionId) {
        ConversionDTO conversion = BeanUtils.toBean(conversionService.getConversionByConversionId(conversionId), ConversionDTO.class);
        return CommonResult.success(conversion);
    }

    @Override
    public CommonResult<Boolean> updateConversion(ConversionDTO conversionDTO) {
        ConversionSaveReqVO bean = BeanUtils.toBean(conversionDTO, ConversionSaveReqVO.class);
        conversionService.updateConversion(bean);
        return CommonResult.success(true);
    }
}
