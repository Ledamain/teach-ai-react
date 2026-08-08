package cn.iocoder.teach-ai.module.clientSystem.service.repo;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 知识库 Service 接口
 *
 * @author waynelam
 */
public interface RepoService {

    /**
     * 创建知识库
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRepo(@Valid RepoSaveReqVO createReqVO);

    /**
     * 更新知识库
     *
     * @param updateReqVO 更新信息
     */
    void updateRepo(@Valid RepoSaveReqVO updateReqVO);

    /**
     * 删除知识库
     *
     * @param id 编号
     */
    void deleteRepo(Long id);

    /**
    * 批量删除知识库
    *
    * @param ids 编号
    */
    void deleteRepoListByIds(List<Long> ids);

    /**
     * 获得知识库
     *
     * @param id 编号
     * @return 知识库
     */
    RepoDO getRepo(Long id);

    /**
     * 获得知识库分页
     *
     * @param pageReqVO 分页查询
     * @return 知识库分页
     */
    PageResult<RepoDTO> getRepoPage(RepoPageReqVO pageReqVO);



}
