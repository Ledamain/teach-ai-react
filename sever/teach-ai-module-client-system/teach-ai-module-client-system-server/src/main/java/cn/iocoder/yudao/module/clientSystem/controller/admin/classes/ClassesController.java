package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDTO;
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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.module.clientSystem.service.classes.ClassesService;

@Tag(name = "管理后台 - 班级")
@RestController
@RequestMapping("/client-system/classes")
@Validated
public class ClassesController {

    @Resource
    private ClassesService classesService;

    @PostMapping("/create")
    @Operation(summary = "创建班级")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:create')")
    public CommonResult<Long> createClasses(@Valid @RequestBody ClassesSaveReqVO createReqVO) {
        return success(classesService.createClasses(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新班级")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:update')")
    public CommonResult<Boolean> updateClasses(@Valid @RequestBody ClassesSaveReqVO updateReqVO) {
        classesService.updateClasses(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:delete')")
    public CommonResult<Boolean> deleteClasses(@RequestParam("id") Long id) {
        classesService.deleteClasses(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除班级")
                @PreAuthorize("@ss.hasPermission('clientSystem:classes:delete')")
    public CommonResult<Boolean> deleteClassesList(@RequestParam("ids") List<Long> ids) {
        classesService.deleteClassesListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:query')")
    public CommonResult<ClassesRespVO> getClasses(@RequestParam("id") Long id) {
        ClassesDO classes = classesService.getClasses(id);
        ClassesRespVO respVO = BeanUtils.toBean(classes, ClassesRespVO.class);
        if (classes.getRepoCategoryIds() != null && !classes.getRepoCategoryIds().isEmpty()) {
            String[] ids = classes.getRepoCategoryIds().split(",");
            List<String> repoCategoryIdsList = new ArrayList<>(Arrays.asList(ids));
            respVO.setRepoCategoryIdsList(repoCategoryIdsList);
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得班级分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:query')")
    public CommonResult<PageResult<ClassesRespVO>> getClassesPage(@Valid ClassesPageReqVO pageReqVO) {
        PageResult<ClassesDTO> pageResult = classesService.getClassesPage(pageReqVO);
        List<ClassesDTO> list = pageResult.getList();
        for (ClassesDTO dto : list) {
            if (dto.getRepoCategoryIds() != null && !dto.getRepoCategoryIds().isEmpty()) {
                String[] ids = dto.getRepoCategoryIds().split(",");
                List<String> repoCategoryIdsList = new ArrayList<>(Arrays.asList(ids));
                dto.setRepoCategoryIdsList(repoCategoryIdsList);
            }
        }
        pageResult.setList(list);
        return success(BeanUtils.toBean(pageResult, ClassesRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得班级列表")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:query')")
    public CommonResult<List<ClassesRespVO>> getClassesList(@Valid ClassesPageReqVO pageReqVO){
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClassesDTO> list = classesService.getClassesPage(pageReqVO).getList();
        return success(BeanUtils.toBean(list, ClassesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出班级 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClassesExcel(@Valid ClassesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClassesDTO> list = classesService.getClassesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "班级.xls", "数据", ClassesRespVO.class,
                        BeanUtils.toBean(list, ClassesRespVO.class));
    }

    // ==================== 子表（班级学生） ====================

    @GetMapping("/classes-students/list-by-classes-id")
    @Operation(summary = "获得班级学生列表")
    @Parameter(name = "classesId", description = "班级id")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes:query')")
    public CommonResult<List<ClassesStudentsDTO>> getClassesStudentsListByClassesId(@RequestParam("classesId") Long classesId) {
        return success(classesService.getClassesStudentsListByClassesIdExt(classesId));
    }

}
