package cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup;

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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.coursegroup.CourseGroupDO;
import cn.iocoder.teach-ai.module.clientSystem.service.coursegroup.CourseGroupService;

@Tag(name = "管理后台 - 课程组")
@RestController
@RequestMapping("/client-system/course-group")
@Validated
public class CourseGroupController {

    @Resource
    private CourseGroupService courseGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建课程组")
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:create')")
    public CommonResult<Long> createCourseGroup(@Valid @RequestBody CourseGroupSaveReqVO createReqVO) {
        return success(courseGroupService.createCourseGroup(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程组")
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:update')")
    public CommonResult<Boolean> updateCourseGroup(@Valid @RequestBody CourseGroupSaveReqVO updateReqVO) {
        courseGroupService.updateCourseGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:delete')")
    public CommonResult<Boolean> deleteCourseGroup(@RequestParam("id") Long id) {
        courseGroupService.deleteCourseGroup(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程组")
                @PreAuthorize("@ss.hasPermission('clientSystem:course-group:delete')")
    public CommonResult<Boolean> deleteCourseGroupList(@RequestParam("ids") List<Long> ids) {
        courseGroupService.deleteCourseGroupListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:query')")
    public CommonResult<CourseGroupRespVO> getCourseGroup(@RequestParam("id") Long id) {
        CourseGroupDO courseGroup = courseGroupService.getCourseGroup(id);
        return success(BeanUtils.toBean(courseGroup, CourseGroupRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程组分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:query')")
    public CommonResult<PageResult<CourseGroupRespVO>> getCourseGroupPage(@Valid CourseGroupPageReqVO pageReqVO) {
        PageResult<CourseGroupDO> pageResult = courseGroupService.getCourseGroupPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CourseGroupRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得课程组列表")
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:query')")
    public CommonResult<List<CourseGroupRespVO>> getCourseGroupList() {
        List<CourseGroupDO> list = courseGroupService.getCourseGroupList();
        return success(BeanUtils.toBean(list, CourseGroupRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程组 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:course-group:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseGroupExcel(@Valid CourseGroupPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CourseGroupDO> list = courseGroupService.getCourseGroupPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程组.xls", "数据", CourseGroupRespVO.class,
                        BeanUtils.toBean(list, CourseGroupRespVO.class));
    }

}
