package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDTO;
import lombok.extern.slf4j.Slf4j;
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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDO;
import cn.iocoder.teach-ai.module.clientSystem.service.repogroup.RepoGroupService;

@Slf4j
@Tag(name = "管理后台 - 课程文件夹")
@RestController
@RequestMapping("/client-system/repo-group")
@Validated
public class RepoGroupController {

    @Resource
    private RepoGroupService repoGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建课程文件夹")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:create')")
    public CommonResult<Long> createRepoGroup(@Valid @RequestBody RepoGroupSaveReqVO createReqVO) {
        return success(repoGroupService.createRepoGroup(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程文件夹")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:update')")
    public CommonResult<Boolean> updateRepoGroup(@Valid @RequestBody RepoGroupSaveReqVO updateReqVO) {
        log.info("更新课程文件夹: {}", updateReqVO);
        repoGroupService.updateRepoGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程文件夹")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:delete')")
    public CommonResult<Boolean> deleteRepoGroup(@RequestParam("id") Long id) {
        repoGroupService.deleteRepoGroup(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程文件夹")
                @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:delete')")
    public CommonResult<Boolean> deleteRepoGroupList(@RequestParam("ids") List<Long> ids) {
        repoGroupService.deleteRepoGroupListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程文件夹")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:query')")
    public CommonResult<RepoGroupRespVO> getRepoGroup(@RequestParam("id") Long id) {
        RepoGroupDO repoGroup = repoGroupService.getRepoGroup(id);
        return success(BeanUtils.toBean(repoGroup, RepoGroupRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程文件夹分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:query')")
    public CommonResult<PageResult<RepoGroupRespVO>> getRepoGroupPage(@Valid RepoGroupPageReqVO pageReqVO) {
        PageResult<RepoGroupDTO> pageResult = repoGroupService.getRepoGroupPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RepoGroupRespVO.class));
    }

    @GetMapping("/list-by-category")
    @Operation(summary = "获得课程文件夹分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:query')")
    public CommonResult<List<RepoGroupRespVO>> getRepoGroupListByRepoCategoryId(@Valid RepoGroupPageReqVO pageReqVO) {
        List<RepoGroupDO> list = repoGroupService.getRepoGroupListByRepoCategoryId(pageReqVO.getRepoCategoryId());
        return success(BeanUtils.toBean(list, RepoGroupRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程文件夹 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-group:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRepoGroupExcel(@Valid RepoGroupPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RepoGroupDTO> list = repoGroupService.getRepoGroupPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程文件夹.xls", "数据", RepoGroupRespVO.class,
                        BeanUtils.toBean(list, RepoGroupRespVO.class));
    }

}
