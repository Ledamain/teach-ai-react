package cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents;

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

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.error;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

import cn.iocoder.teach-ai.framework.excel.core.util.ExcelUtils;

import cn.iocoder.teach-ai.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.teach-ai.framework.apilog.core.enums.OperateTypeEnum.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.module.clientSystem.service.classesstudents.ClassesStudentsService;

@Tag(name = "管理后台 - 班级学生")
@RestController
@RequestMapping("/client-system/classes-students")
@Validated
public class ClassesStudentsController {

    @Resource
    private ClassesStudentsService classesStudentsService;

    @PostMapping("/create")
    @Operation(summary = "创建班级学生")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:create')")
    public CommonResult<Long> createClassesStudents(@Valid @RequestBody ClassesStudentsSaveReqVO createReqVO) {
        if (classesStudentsService.getClassesStudentsByUserId(createReqVO.getStudentUserId()) != null) {
            return error(400, "学生已有班级");
        }
        return success(classesStudentsService.createClassesStudents(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新班级学生")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:update')")
    public CommonResult<Boolean> updateClassesStudents(@Valid @RequestBody ClassesStudentsSaveReqVO updateReqVO) {
        classesStudentsService.updateClassesStudents(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班级学生")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:delete')")
    public CommonResult<Boolean> deleteClassesStudents(@RequestParam("id") Long id) {
        classesStudentsService.deleteClassesStudents(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除班级学生")
                @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:delete')")
    public CommonResult<Boolean> deleteClassesStudentsList(@RequestParam("ids") List<Long> ids) {
        classesStudentsService.deleteClassesStudentsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班级学生")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:query')")
    public CommonResult<ClassesStudentsRespVO> getClassesStudents(@RequestParam("id") Long id) {
        ClassesStudentsDO classesStudents = classesStudentsService.getClassesStudents(id);
        return success(BeanUtils.toBean(classesStudents, ClassesStudentsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得班级学生分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:query')")
    public CommonResult<PageResult<ClassesStudentsRespVO>> getClassesStudentsPage(@Valid ClassesStudentsPageReqVO pageReqVO) {
        PageResult<ClassesStudentsDO> pageResult = classesStudentsService.getClassesStudentsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClassesStudentsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出班级学生 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClassesStudentsExcel(@Valid ClassesStudentsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClassesStudentsDO> list = classesStudentsService.getClassesStudentsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "班级学生.xls", "数据", ClassesStudentsRespVO.class,
                        BeanUtils.toBean(list, ClassesStudentsRespVO.class));
    }

    @GetMapping("/total")
    @Operation(summary = "查询班级总人数")
    @Parameter(name = "classesId", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:query')")
    public CommonResult<Long> getClassesStudentsTotal(@Valid @RequestParam("id") Long classesId) {
        Long total = classesStudentsService.getClassesStudentsListTotal(classesId);
        return success(total);
    }

    @GetMapping("/get-by-user")
    @Operation(summary = "按用户ID查询学生")
    @Parameter(name = "userId", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:classes-students:query')")
    public CommonResult<ClassesStudentsRespVO> getClassesStudentsByUserId(@RequestParam("id") Long userId) {
        ClassesStudentsDO student = classesStudentsService.getClassesStudentsByUserId(userId);
        return success(BeanUtils.toBean(student, ClassesStudentsRespVO.class));
    }

}
