package cn.iocoder.teach-ai.module.clientSystem.controller.client.service.repo;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.dataobject.dto.RepoCategoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;

import java.util.List;

public interface ClientRepoService {

    /**
     * 获得全部已启用知识库
     *
     * @param pageReqVO 查询
     * @return 知识库列表
     */
    List<RepoCategoryDTO> getRepoList (RepoPageReqVO pageReqVO);

    /**
     * 获得全部知识库
     *
     * @param pageReqVO 查询
     * @return 知识库列表
     */
    List<RepoDO> getRepoListAll(RepoPageReqVO pageReqVO);

    /**
     * 获得全部知识库
     *
     * @param pageReqVO 查询
     * @return 知识库列表
     */
    List<RepoDO> getRepoListAllByGroupIdAndRepoCategoryId(RepoPageReqVO pageReqVO);

    /**
     * 根据学科id获取知识库id数组
     *
     * @param pageReqVO
     * @return 知识库id数组
     */
    List<String> getRepoArray (RepoPageReqVO pageReqVO);

}
