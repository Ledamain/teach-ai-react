package cn.iocoder.teach-ai.module.clientSystem.controller.client.repo;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo.RepoGroupSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.dataobject.dto.RepoCategoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.repo.vo.ClientRepoGroupDetailRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.repo.vo.ClientRepoGroupRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.service.repo.ClientRepoService;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDO;
import cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.RepoDeleteProducer;
import cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.RepoIngestionProducer;
import cn.iocoder.teach-ai.module.clientSystem.service.repo.RepoService;
import cn.iocoder.teach-ai.module.clientSystem.service.repogroup.RepoGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.KNOWLEDGE_SEGMENTATION_FAILURE;

@Slf4j
@Tag(name = "客户端接口 - 知识库")
@RestController
@RequestMapping("/client-api/client-system/repo")
@Validated
public class ClientRepoController {

    @Resource
    private ClientRepoService clientRepoService;

    @Resource
    private RepoGroupService repoGroupService;

    @Resource
    private RepoService repoService;


    @Resource
    private RepoIngestionProducer repoIngestionProducer;

    @Resource
    private RepoDeleteProducer repoDeleteProducer;

    @GetMapping("/list")
    @Operation(summary = "获得所有已启用知识库（封装）")
    public CommonResult<List<RepoCategoryDTO>> getRepoList(@Valid RepoPageReqVO pageReqVO) {
        List<RepoCategoryDTO> list = clientRepoService.getRepoList(pageReqVO);
        return success(list);
    }

    @GetMapping("/getRepoArray")
    @Operation(summary = "获得所有已启用知识库数组")
    public CommonResult<String[]> getRepoArray(@Valid RepoPageReqVO pageReqVO) {
        log.info("pageReqBO中的RepoCategoryId:{}",pageReqVO.getRepoCategoryId());
        String[] array = clientRepoService.getRepoArray(pageReqVO).toArray(new String[0]);
        return success(array);
    }

    @GetMapping("/getRepoArrayByCourseId")
    @Operation(summary = "根据学科id获取知识库文件夹列表")
    public CommonResult<List<ClientRepoGroupRespVO>> getRepoArray(@RequestParam Long courseId) {
        List<ClientRepoGroupRespVO> bean = BeanUtils.toBean(repoGroupService.getRepoGroupListByRepoCategoryId(courseId), ClientRepoGroupRespVO.class);
        bean.forEach(item -> {
            RepoPageReqVO repoPageReqVO = new RepoPageReqVO();
            repoPageReqVO.setRepoGroupId(item.getId());
            log.info("repoPageReqVO中的RepoGroupId:{}",repoPageReqVO.getRepoGroupId());
            item.setFileCount(clientRepoService.getRepoListAll(repoPageReqVO).size());
        });
        return success(bean);
    }

    @GetMapping("/get-repo-list-by-course-id-and-repo-group-id")
    @Operation(summary = "获取知识库列表")
    public CommonResult<ClientRepoGroupDetailRespVO> getRepoListByCourseIdAndRepoGroupId(@Valid RepoPageReqVO repoPageReqVO) {

        ClientRepoGroupDetailRespVO clientRepoGroupDetailRespVO = new ClientRepoGroupDetailRespVO();
        clientRepoGroupDetailRespVO.setId(repoPageReqVO.getRepoGroupId());
        RepoGroupDO repoGroup = repoGroupService.getRepoGroup(repoPageReqVO.getRepoGroupId());
        clientRepoGroupDetailRespVO.setRepoGroupName(repoGroup.getRepoGroupName());
        clientRepoGroupDetailRespVO.setRepoGroupDescription(repoGroup.getRepoGroupDescription());
        // 写入文件
        List<RepoDO> fileList = clientRepoService.getRepoListAllByGroupIdAndRepoCategoryId(repoPageReqVO);
        clientRepoGroupDetailRespVO.setRepoList(BeanUtils.toBean(fileList, RepoRespVO.class));
        // 写入文件数量
        clientRepoGroupDetailRespVO.setFileCount(fileList.size());
        clientRepoGroupDetailRespVO.setCreateTime(repoGroup.getCreateTime());
        clientRepoGroupDetailRespVO.setUpdateTime(repoGroup.getUpdateTime());

        return success(clientRepoGroupDetailRespVO);
    }

    @PostMapping("/create")
    @Operation(summary = "创建课程文件夹")
    public CommonResult<Long> createRepoGroup(@Valid @RequestBody RepoGroupSaveReqVO createReqVO) {
        return success(repoGroupService.createRepoGroup(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程文件夹")
    public CommonResult<Boolean> updateRepoGroup(@Valid @RequestBody RepoGroupSaveReqVO updateReqVO) {
        log.info("更新课程文件夹: {}", updateReqVO);
        repoGroupService.updateRepoGroup(updateReqVO);
        return success(true);
    }

    @GetMapping("/change-status")
    @Operation(summary = "切换知识库状态")
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
            }catch (Exception e){
                log.error("知识库停用失败，原因：{}",e.getMessage());
            }
        }
        return success(true);
    }

    @PostMapping("/create-repo")
    @Operation(summary = "创建知识库")
    public CommonResult<Long> createRepo(@Valid @RequestBody RepoSaveReqVO createReqVO) {
        return success(repoService.createRepo(createReqVO));
    }
}
