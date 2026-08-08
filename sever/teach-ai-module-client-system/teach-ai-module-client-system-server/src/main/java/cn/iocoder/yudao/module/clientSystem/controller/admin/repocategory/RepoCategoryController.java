package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDTO;
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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.service.repocategory.RepoCategoryService;

@Tag(name = "管理后台 - 知识库类别")
@RestController
@RequestMapping("/client-system/repo-category")
@Validated
public class RepoCategoryController {

    @Resource
    private RepoCategoryService repoCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建知识库类别（学科）")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:create')")
    public CommonResult<Long> createRepoCategory(@Valid @RequestBody RepoCategorySaveReqVO createReqVO) {
        return success(repoCategoryService.createRepoCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新知识库类别（学科）")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:update')")
    public CommonResult<Boolean> updateRepoCategory(@Valid @RequestBody RepoCategorySaveReqVO updateReqVO) {
        repoCategoryService.updateRepoCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识库类别")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:delete')")
    public CommonResult<Boolean> deleteRepoCategory(@RequestParam("id") Long id) {
        repoCategoryService.deleteRepoCategory(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除知识库类别")
                @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:delete')")
    public CommonResult<Boolean> deleteRepoCategoryList(@RequestParam("ids") List<Long> ids) {
        repoCategoryService.deleteRepoCategoryListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得知识库类别")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:query')")
    public CommonResult<RepoCategoryRespVO> getRepoCategory(@RequestParam("id") Long id) {
        RepoCategoryDO repoCategory = repoCategoryService.getRepoCategory(id);
        return success(BeanUtils.toBean(repoCategory, RepoCategoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得知识库类别分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:query')")
    public CommonResult<PageResult<RepoCategoryRespVO>> getRepoCategoryPage(@Valid RepoCategoryPageReqVO pageReqVO) {
        PageResult<RepoCategoryDTO> pageResult = repoCategoryService.getRepoCategoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RepoCategoryRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得知识库类别列表")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:query')")
    public CommonResult<List<RepoCategoryRespVO>> getRepoCategoryList() {
        List<RepoCategoryDO> list = repoCategoryService.getRepoCategoryList(new RepoCategoryPageReqVO());
        return success(BeanUtils.toBean(list, RepoCategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出知识库类别 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo-category:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRepoCategoryExcel(@Valid RepoCategoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RepoCategoryDTO> list = repoCategoryService.getRepoCategoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "知识库类别.xls", "数据", RepoCategoryRespVO.class,
                        BeanUtils.toBean(list, RepoCategoryRespVO.class));
    }

}
