package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDTO;
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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDO;
import cn.iocoder.teach-ai.module.clientSystem.service.exerciseresult.ExerciseResultService;

@Tag(name = "管理后台 - 评判结果")
@RestController
@RequestMapping("/client-system/exercise-result")
@Validated
public class ExerciseResultController {

    @Resource
    private ExerciseResultService exerciseResultService;

    @PostMapping("/create")
    @Operation(summary = "创建评判结果")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:create')")
    public CommonResult<Long> createExerciseResult(@Valid @RequestBody ExerciseResultSaveReqVO createReqVO) {
        return success(exerciseResultService.createExerciseResult(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新评判结果")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:update')")
    public CommonResult<Boolean> updateExerciseResult(@Valid @RequestBody ExerciseResultSaveReqVO updateReqVO) {
        ExerciseResultDO result = exerciseResultService.getExerciseResult(updateReqVO.getId());
        if (result == null) {
            exerciseResultService.createExerciseResult(updateReqVO);
        }
        exerciseResultService.updateExerciseResult(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除评判结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:delete')")
    public CommonResult<Boolean> deleteExerciseResult(@RequestParam("id") Long id) {
        exerciseResultService.deleteExerciseResult(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除评判结果")
                @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:delete')")
    public CommonResult<Boolean> deleteExerciseResultList(@RequestParam("ids") List<Long> ids) {
        exerciseResultService.deleteExerciseResultListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得评判结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:query')")
    public CommonResult<ExerciseResultRespVO> getExerciseResult(@RequestParam("id") Long id) {
        ExerciseResultDO exerciseResult = exerciseResultService.getExerciseResult(id);
        return success(BeanUtils.toBean(exerciseResult, ExerciseResultRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得评判结果分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:query')")
    public CommonResult<PageResult<ExerciseResultRespVO>> getExerciseResultPage(@Valid ExerciseResultPageReqVO pageReqVO) {
        PageResult<ExerciseResultDTO> pageResult = exerciseResultService.getExerciseResultPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ExerciseResultRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得评判结果分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:query')")
    public CommonResult<List<ExerciseResultRespVO>> getExerciseResultList(@Valid ExerciseResultPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExerciseResultDTO> result = exerciseResultService.getExerciseResultPage(pageReqVO).getList();
        return success(BeanUtils.toBean(result, ExerciseResultRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出评判结果 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-result:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExerciseResultExcel(@Valid ExerciseResultPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExerciseResultDTO> list = exerciseResultService.getExerciseResultPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "评判结果.xls", "数据", ExerciseResultRespVO.class,
                        BeanUtils.toBean(list, ExerciseResultRespVO.class));
    }

}
