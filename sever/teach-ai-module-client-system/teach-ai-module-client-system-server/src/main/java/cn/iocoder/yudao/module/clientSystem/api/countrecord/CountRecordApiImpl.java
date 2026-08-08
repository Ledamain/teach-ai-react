package cn.iocoder.teach-ai.module.clientSystem.api.countrecord;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.dto.ConversionDTO;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.dto.CountRecordDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo.CountRecordSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.service.countrecord.CountRecordService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class CountRecordApiImpl implements CountRecordApi {
    @Resource
    private CountRecordService countRecordService;

    @Override
    public CommonResult<Long> createCountRecord(CountRecordDTO countRecordDTO) {
        CountRecordSaveReqVO createReqVO = BeanUtils.toBean(countRecordDTO, CountRecordSaveReqVO.class);
        return success(countRecordService.createCountRecord(createReqVO));
    }

    @Override
    public CommonResult<CountRecordDTO> getCountRecordById(Long id) {
        return success(BeanUtils.toBean(countRecordService.getCountRecord(id), CountRecordDTO.class));
    }

    @Override
    public CommonResult<Boolean> updateCountRecord(CountRecordDTO countRecordDTO) {
        CountRecordSaveReqVO bean = BeanUtils.toBean(countRecordDTO, CountRecordSaveReqVO.class);
        countRecordService.updateCountRecord(bean);
        return success(true);
    }
}
