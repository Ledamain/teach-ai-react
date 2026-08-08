package cn.iocoder.teach-ai.module.clientSystem.controller.client.service.repo;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.RepoCategoryPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.dataobject.dto.RepoCategoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repo.RepoMapper;
import cn.iocoder.teach-ai.module.clientSystem.service.repocategory.RepoCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ClientRepoServiceImpl implements ClientRepoService {

    @Resource
    private RepoMapper repoMapper;

    @Resource
    private RepoCategoryService repoCategoryService;

    @Override
    public List<RepoCategoryDTO> getRepoList(RepoPageReqVO pageReqVO) {

        pageReqVO.setRepoStatus("1");
        ArrayList<RepoCategoryDTO> repoCategoryDTOS = new ArrayList<>();
        for (RepoCategoryDO repoCategoryDO : repoCategoryService.getRepoCategoryList(new RepoCategoryPageReqVO())) {
            log.info("所有知识库类别：{}", repoCategoryDO);
            RepoCategoryDTO repoCategoryDTO = new RepoCategoryDTO();
            repoCategoryDTO.setId(repoCategoryDO.getId());
            repoCategoryDTO.setRepoCategoryName(repoCategoryDO.getRepoCategoryName());
            pageReqVO.setRepoCategoryId(repoCategoryDO.getId());
            List<RepoDTO> repoDTOS = repoMapper.selectList(pageReqVO);
            repoCategoryDTO.setRepoDTOS(repoDTOS);
            repoCategoryDTOS.add(repoCategoryDTO);
        }

        return repoCategoryDTOS;
    }

    @Override
    public List<RepoDO> getRepoListAll(RepoPageReqVO pageReqVO) {
        return repoMapper.selectList(RepoDO::getRepoGroupId,pageReqVO.getRepoGroupId());
    }

    @Override
    public List<RepoDO> getRepoListAllByGroupIdAndRepoCategoryId(RepoPageReqVO pageReqVO) {
        return repoMapper.selectList(RepoDO::getRepoGroupId,pageReqVO.getRepoGroupId(),RepoDO::getRepoCategoryId,pageReqVO.getRepoCategoryId());
    }

    @Override
    public List<String> getRepoArray(RepoPageReqVO pageReqVO) {
        pageReqVO.setRepoStatus("1");
        List<String> repoArray = new ArrayList<>();
        for (RepoDTO repoDTO : repoMapper.selectList(pageReqVO)) {
            repoArray.add(String.valueOf(repoDTO.getId()));
        }
        return repoArray;
    }
}
