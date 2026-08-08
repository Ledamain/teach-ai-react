package cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo;

import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.RepoDeleteProducer;
import cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.RepoIngestionProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.error;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

import cn.iocoder.teach-ai.framework.excel.core.util.ExcelUtils;

import cn.iocoder.teach-ai.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.teach-ai.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.KNOWLEDGE_SEGMENTATION_FAILURE;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.module.clientSystem.service.repo.RepoService;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "管理后台 - 知识库")
@RestController
@RequestMapping("/client-system/repo")
@Validated
public class RepoController {

    @Resource
    private RepoService repoService;

    @Resource
    private RepoIngestionProducer repoIngestionProducer;

    @Resource
    private RepoDeleteProducer repoDeleteProducer;

    @PostMapping("/create")
    @Operation(summary = "创建知识库")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:create')")
    public CommonResult<Long> createRepo(@Valid @RequestBody RepoSaveReqVO createReqVO) {
        return success(repoService.createRepo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新知识库")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:update')")
    public CommonResult<Boolean> updateRepo(@Valid @RequestBody RepoSaveReqVO updateReqVO) {
        repoService.updateRepo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识库")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:delete')")
    public CommonResult<Boolean> deleteRepo(@RequestParam("id") Long id) {
        repoService.deleteRepo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除知识库")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:delete')")
    public CommonResult<Boolean> deleteRepoList(@RequestParam("ids") List<Long> ids) {
        repoService.deleteRepoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得知识库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:query')")
    public CommonResult<RepoRespVO> getRepo(@RequestParam("id") Long id) {
        RepoDO repo = repoService.getRepo(id);
        return success(BeanUtils.toBean(repo, RepoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得知识库分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:query')")
    public CommonResult<PageResult<RepoRespVO>> getRepoPage(@Valid RepoPageReqVO pageReqVO) {
        PageResult<RepoDTO> pageResult = repoService.getRepoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RepoRespVO.class));
    }

    @PostMapping("/change-status")
    @Operation(summary = "切换知识库状态")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:update')")
    public CommonResult<Boolean> changeRepoStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        RepoDO repo = repoService.getRepo(id).setRepoStatus(status);
        repoService.updateRepo(BeanUtils.toBean(repo, RepoSaveReqVO.class));
        if (repo.getRepoStatus().equals("1")){
            try {
                String repoFileUrl = repo.getRepoFile();
                if (repoFileUrl == null || repoFileUrl.isBlank()) {
                    log.error("知识库文件 URL 为空，无法切分，知识库ID：{}", id);
                    throw exception(KNOWLEDGE_SEGMENTATION_FAILURE);
                }
                // 加入消息队列
                repoIngestionProducer.fileIngest(String.valueOf(repo.getId()), repoFileUrl);
            } catch (Exception e) {
                log.info("知识库切分失败，原因：{}", e.getMessage());
                throw exception(KNOWLEDGE_SEGMENTATION_FAILURE);
            }
        } else if (repo.getRepoStatus().equals("0")) {
            try {
                repoDeleteProducer.fileDelete(String.valueOf(repo.getId()),repo.getRepoTitle());
                log.info("知识库id：{},知识库名字：{}", repo.getId(),repo.getRepoTitle());
            }catch (Exception e){
                log.error("知识库停用失败，原因：{}",e.getMessage());
            }
        }
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出知识库 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:repo:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRepoExcel(@Valid RepoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RepoDTO> list = repoService.getRepoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "知识库.xls", "数据", RepoRespVO.class,
                        BeanUtils.toBean(list, RepoRespVO.class));
    }

}
