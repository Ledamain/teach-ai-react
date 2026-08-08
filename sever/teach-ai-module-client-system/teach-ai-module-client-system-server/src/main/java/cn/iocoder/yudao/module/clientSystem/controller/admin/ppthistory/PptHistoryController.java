package cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDTO;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

import cn.iocoder.teach-ai.framework.excel.core.util.ExcelUtils;

import cn.iocoder.teach-ai.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.teach-ai.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDO;
import cn.iocoder.teach-ai.module.clientSystem.service.ppthistory.PptHistoryService;

@Tag(name = "管理后台 - PPT历史记录")
@RestController
@RequestMapping("/client-system/ppt-history")
@Validated
public class PptHistoryController {

    @Resource
    private PptHistoryService pptHistoryService;

    @PostMapping("/create")
    @Operation(summary = "创建PPT历史记录")
    @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:create')")
    public CommonResult<Long> createPptHistory(@Valid @RequestBody PptHistorySaveReqVO createReqVO) {
        return success(pptHistoryService.createPptHistory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新PPT历史记录")
    @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:update')")
    public CommonResult<Boolean> updatePptHistory(@Valid @RequestBody PptHistorySaveReqVO updateReqVO) {
        pptHistoryService.updatePptHistory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除PPT历史记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:delete')")
    public CommonResult<Boolean> deletePptHistory(@RequestParam("id") Long id) {
        pptHistoryService.deletePptHistory(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除PPT历史记录")
                @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:delete')")
    public CommonResult<Boolean> deletePptHistoryList(@RequestParam("ids") List<Long> ids) {
        pptHistoryService.deletePptHistoryListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得PPT历史记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:query')")
    public CommonResult<PptHistoryRespVO> getPptHistory(@RequestParam("id") Long id) {
        PptHistoryDO pptHistory = pptHistoryService.getPptHistory(id);
        return success(BeanUtils.toBean(pptHistory, PptHistoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得PPT历史记录分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:query')")
    public CommonResult<PageResult<PptHistoryRespVO>> getPptHistoryPage(@Valid PptHistoryPageReqVO pageReqVO) {
        PageResult<PptHistoryDTO> pageResult = pptHistoryService.getPptHistoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PptHistoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出PPT历史记录 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:ppt-history:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPptHistoryExcel(@Valid PptHistoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PptHistoryDTO> list = pptHistoryService.getPptHistoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "PPT历史记录.xls", "数据", PptHistoryRespVO.class,
                        BeanUtils.toBean(list, PptHistoryRespVO.class));
    }

}
