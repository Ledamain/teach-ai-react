package cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog;

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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.loginlog.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.loginlog.LoginLogDO;
import cn.iocoder.teach-ai.module.clientSystem.service.loginlog.LoginLogService;

@Tag(name = "管理后台 - 客户端登录日志")
@RestController
@RequestMapping("/client-system/login-log")
@Validated
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    @PostMapping("/create")
    @Operation(summary = "创建客户端登录日志")
    @PreAuthorize("@ss.hasPermission('clientSystem:login-log:create')")
    public CommonResult<Long> createLoginLog(@Valid @RequestBody LoginLogSaveReqVO createReqVO) {
        return success(loginLogService.createLoginLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户端登录日志")
    @PreAuthorize("@ss.hasPermission('clientSystem:login-log:update')")
    public CommonResult<Boolean> updateLoginLog(@Valid @RequestBody LoginLogSaveReqVO updateReqVO) {
        loginLogService.updateLoginLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户端登录日志")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:login-log:delete')")
    public CommonResult<Boolean> deleteLoginLog(@RequestParam("id") Long id) {
        loginLogService.deleteLoginLog(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除客户端登录日志")
                @PreAuthorize("@ss.hasPermission('clientSystem:login-log:delete')")
    public CommonResult<Boolean> deleteLoginLogList(@RequestParam("ids") List<Long> ids) {
        loginLogService.deleteLoginLogListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得客户端登录日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:login-log:query')")
    public CommonResult<LoginLogRespVO> getLoginLog(@RequestParam("id") Long id) {
        LoginLogDO loginLog = loginLogService.getLoginLog(id);
        return success(BeanUtils.toBean(loginLog, LoginLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户端登录日志分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:login-log:query')")
    public CommonResult<PageResult<LoginLogRespVO>> getLoginLogPage(@Valid LoginLogPageReqVO pageReqVO) {
        PageResult<LoginLogDO> pageResult = loginLogService.getLoginLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LoginLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户端登录日志 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:login-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLoginLogExcel(@Valid LoginLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<LoginLogDO> list = loginLogService.getLoginLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "客户端登录日志.xls", "数据", LoginLogRespVO.class,
                        BeanUtils.toBean(list, LoginLogRespVO.class));
    }

}
