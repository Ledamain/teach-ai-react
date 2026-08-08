package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.ClassesStudentsPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.ExerciseResultSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.StudentsDTO;
import cn.iocoder.teach-ai.module.clientSystem.service.classesstudents.ClassesStudentsService;
import cn.iocoder.teach-ai.module.clientSystem.service.exerciseresult.ExerciseResultService;
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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDO;
import cn.iocoder.teach-ai.module.clientSystem.service.exerciseinfo.ExerciseInfoService;

@Tag(name = "管理后台 - 练习题")
@RestController
@RequestMapping("/client-system/exercise-info")
@Validated
public class ExerciseInfoController {

    @Resource
    private ExerciseInfoService exerciseInfoService;

    @Resource
    private ExerciseResultService exerciseResultService;

    @Resource
    private ClassesStudentsService classesStudentsService;

    @PostMapping("/create")
    @Operation(summary = "创建练习题")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:create')")
    public CommonResult<Long> createExerciseInfo(@Valid @RequestBody ExerciseInfoSaveReqVO createReqVO) {
        Long exerciseId = exerciseInfoService.createExerciseInfo(createReqVO);
        List<ClassesStudentsDO> list = classesStudentsService.getClassesStudentsListOrigin(new ClassesStudentsPageReqVO().setClassesId(Long.valueOf(createReqVO.getClassesId())));
        for (ClassesStudentsDO student : list) {
            exerciseResultService.createExerciseResult(new ExerciseResultSaveReqVO().setExerciseId(exerciseId).setStudentUserId(student.getStudentUserId()));
        }
        return success(exerciseId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新练习题")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:update')")
    public CommonResult<Boolean> updateExerciseInfo(@Valid @RequestBody ExerciseInfoSaveReqVO updateReqVO) {
        exerciseInfoService.updateExerciseInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除练习题")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:delete')")
    public CommonResult<Boolean> deleteExerciseInfo(@RequestParam("id") Long id) {
        exerciseInfoService.deleteExerciseInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除练习题")
                @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:delete')")
    public CommonResult<Boolean> deleteExerciseInfoList(@RequestParam("ids") List<Long> ids) {
        exerciseInfoService.deleteExerciseInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得练习题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:query')")
    public CommonResult<ExerciseInfoRespVO> getExerciseInfo(@RequestParam("id") Long id) {
        ExerciseInfoDO exerciseInfo = exerciseInfoService.getExerciseInfo(id);
        ExerciseInfoRespVO respVO = BeanUtils.toBean(exerciseInfo, ExerciseInfoRespVO.class);
        if (exerciseInfo.getClassesId() != null && !exerciseInfo.getClassesId().isEmpty()) {
            String[] ids = exerciseInfo.getClassesId().replace("_", "").replace("C", "").split(",");
            List<String> classesIds = Arrays.asList(ids);
            respVO.setClassesIdsList(classesIds);
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得练习题分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:query')")
    public CommonResult<PageResult<ExerciseInfoRespVO>> getExerciseInfoPage(@Valid ExerciseInfoPageReqVO pageReqVO) {
        PageResult<ExerciseInfoDTO> pageResult = exerciseInfoService.getExerciseInfoJoinPage(pageReqVO);
        List<ExerciseInfoDTO> list = pageResult.getList();
        for (ExerciseInfoDTO exerciseInfo : list) {
            if (exerciseInfo.getClassesId() != null && !exerciseInfo.getClassesId().isEmpty()) {
                String[] ids = exerciseInfo.getClassesId().replace("_", "").replace("C", "").split(",");
                List<String> classesIds = Arrays.asList(ids);
                exerciseInfo.setClassesIdsList(classesIds);
            }
        }
        return success(BeanUtils.toBean(pageResult, ExerciseInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出练习题 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportExerciseInfoExcel(@Valid ExerciseInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExerciseInfoDO> list = exerciseInfoService.getExerciseInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "练习题.xls", "数据", ExerciseInfoRespVO.class,
                        BeanUtils.toBean(list, ExerciseInfoRespVO.class));
    }

    @PostMapping("/post")
    @Operation(summary = "发布练习题")
    @PreAuthorize("@ss.hasPermission('clientSystem:exercise-info:update')")
    public CommonResult<Boolean> postExerciseInfo(@Valid @RequestBody ExerciseInfoSaveReqVO updateReqVO) {
        updateReqVO.setStatus(1L);
        String[] classesIds = updateReqVO.getClassesId().replace("_", "").replace("C", "").split(",");
        for (String classesId : classesIds) {
            List<ClassesStudentsDO> list = classesStudentsService.getClassesStudentsListOrigin(new ClassesStudentsPageReqVO().setClassesId(Long.parseLong(classesId)));
            for (ClassesStudentsDO student : list) {
                exerciseResultService.createExerciseResult(new ExerciseResultSaveReqVO().setExerciseId(updateReqVO.getId()).setStudentUserId(student.getStudentUserId()));
            }
        }
        exerciseInfoService.updateExerciseInfo(updateReqVO);
        return success(true);
    }

}
