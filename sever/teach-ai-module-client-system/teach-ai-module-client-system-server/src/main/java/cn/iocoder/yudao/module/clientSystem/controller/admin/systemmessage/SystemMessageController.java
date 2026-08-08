package cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage;

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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.systemmessage.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.systemmessage.SystemMessageDO;
import cn.iocoder.teach-ai.module.clientSystem.service.systemmessage.SystemMessageService;

@Tag(name = "管理后台 - 系统提示词")
@RestController
@RequestMapping("/client-system/system-message")
@Validated
public class SystemMessageController {

    @Resource
    private SystemMessageService systemMessageService;

    @PostMapping("/create")
    @Operation(summary = "创建系统提示词")
    @PreAuthorize("@ss.hasPermission('clientSystem:system-message:create')")
    public CommonResult<Long> createSystemMessage(@Valid @RequestBody SystemMessageSaveReqVO createReqVO) {
        return success(systemMessageService.createSystemMessage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统提示词")
    @PreAuthorize("@ss.hasPermission('clientSystem:system-message:update')")
    public CommonResult<Boolean> updateSystemMessage(@Valid @RequestBody SystemMessageSaveReqVO updateReqVO) {
        systemMessageService.updateSystemMessage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统提示词")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:system-message:delete')")
    public CommonResult<Boolean> deleteSystemMessage(@RequestParam("id") Long id) {
        systemMessageService.deleteSystemMessage(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除系统提示词")
                @PreAuthorize("@ss.hasPermission('clientSystem:system-message:delete')")
    public CommonResult<Boolean> deleteSystemMessageList(@RequestParam("ids") List<Long> ids) {
        systemMessageService.deleteSystemMessageListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统提示词")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:system-message:query')")
    public CommonResult<SystemMessageRespVO> getSystemMessage(@RequestParam("id") Long id) {
        SystemMessageDO systemMessage = systemMessageService.getSystemMessage(id);
        return success(BeanUtils.toBean(systemMessage, SystemMessageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得系统提示词分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:system-message:query')")
    public CommonResult<PageResult<SystemMessageRespVO>> getSystemMessagePage(@Valid SystemMessagePageReqVO pageReqVO) {
        PageResult<SystemMessageDO> pageResult = systemMessageService.getSystemMessagePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SystemMessageRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出系统提示词 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:system-message:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSystemMessageExcel(@Valid SystemMessagePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SystemMessageDO> list = systemMessageService.getSystemMessagePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "系统提示词.xls", "数据", SystemMessageRespVO.class,
                        BeanUtils.toBean(list, SystemMessageRespVO.class));
    }

}
