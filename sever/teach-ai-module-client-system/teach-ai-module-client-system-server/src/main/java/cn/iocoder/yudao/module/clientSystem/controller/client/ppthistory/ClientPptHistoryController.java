package cn.iocoder.teach-ai.module.clientSystem.controller.client.ppthistory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.PptHistoryPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.PptHistoryRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.service.ppthistory.PptHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - PPT历史记录")
@RestController
@RequestMapping("/client-api/client-system/ppt-history")
@Validated
public class ClientPptHistoryController {
    @Resource
    private PptHistoryService pptHistoryService;

    @GetMapping("/list")
    @Operation(summary = "获得PPT历史记录列表")
    public CommonResult<PageResult<PptHistoryRespVO>> getPptHistoryPage(@Valid PptHistoryPageReqVO pageReqVO) {
//        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<PptHistoryDTO> pageResult = pptHistoryService.getPptHistoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PptHistoryRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得PPT历史记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<PptHistoryRespVO> getPptHistory(@RequestParam("id") Long id) {
        PptHistoryDO pptHistory = pptHistoryService.getPptHistory(id);
        return success(BeanUtils.toBean(pptHistory, PptHistoryRespVO.class));
    }
}
