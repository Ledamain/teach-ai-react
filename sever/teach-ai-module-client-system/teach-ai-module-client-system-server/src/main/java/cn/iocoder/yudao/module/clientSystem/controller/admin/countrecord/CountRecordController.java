package cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDTO;
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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.countrecord.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.countrecord.CountRecordDO;
import cn.iocoder.teach-ai.module.clientSystem.service.countrecord.CountRecordService;

@Tag(name = "管理后台 - 使用次数记录")
@RestController
@RequestMapping("/client-system/count-record")
@Validated
public class CountRecordController {

    @Resource
    private CountRecordService countRecordService;

    @GetMapping("/get")
    @Operation(summary = "获得使用次数记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<CountRecordRespVO> getCountRecord(@RequestParam("id") Long id) {
        CountRecordDO countRecord = countRecordService.getCountRecord(id);
        return success(BeanUtils.toBean(countRecord, CountRecordRespVO.class));
    }

    @GetMapping("/get-daily")
    @Operation(summary = "获得使用次数记录")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<Long> getCountRecordDaily() {
        Long countRecord = countRecordService.getCountRecordDaily();
        return success(countRecord);
    }

    @GetMapping("/get-trend")
    @Operation(summary = "获得使用次数记录")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<List<CountRecordRespVO>> getCountRecordTrend() {
        List<CountRecordDTO> countRecord = countRecordService.getCountRecordTrend();
        return success(BeanUtils.toBean(countRecord, CountRecordRespVO.class));
    }

}
