package cn.iocoder.teach-ai.module.clientSystem.api.ppthistoy;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.PptHistoryPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.PptHistoryRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.PptHistorySaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.service.ppthistory.PptHistoryService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class PptHistoryApiImpl implements PptHistoryApi {

    @Resource
    private PptHistoryService pptHistoryService;

    @Override
    public CommonResult<Long> createPptHistory(PptHistoryDTO createReqVO) {
        PptHistorySaveReqVO bean = BeanUtils.toBean(createReqVO, PptHistorySaveReqVO.class);
        return CommonResult.success(pptHistoryService.createPptHistory(bean));
    }

    @Override
    public CommonResult<PptHistoryDTO> getPptHistoryByFileName(String fileName) {
        return CommonResult.success(BeanUtils.toBean(pptHistoryService.getPptHistoryByFileName(fileName), PptHistoryDTO.class));
    }

    @Override
    public CommonResult<List<PptHistoryDTO>> getPptHistoryList(PptHistoryDTO pageReqVO) {
        return success( pptHistoryService.getPptHistoryList(pageReqVO));
    }
}
