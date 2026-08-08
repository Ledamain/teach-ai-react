package cn.iocoder.teach-ai.module.clientSystem.service.repogroup;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 课程文件夹 Service 接口
 *
 * @author waynelam
 */
public interface RepoGroupService {

    /**
     * 创建课程文件夹
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRepoGroup(@Valid RepoGroupSaveReqVO createReqVO);

    /**
     * 更新课程文件夹
     *
     * @param updateReqVO 更新信息
     */
    void updateRepoGroup(@Valid RepoGroupSaveReqVO updateReqVO);

    /**
     * 删除课程文件夹
     *
     * @param id 编号
     */
    void deleteRepoGroup(Long id);

    /**
    * 批量删除课程文件夹
    *
    * @param ids 编号
    */
    void deleteRepoGroupListByIds(List<Long> ids);

    /**
     * 获得课程文件夹
     *
     * @param id 编号
     * @return 课程文件夹
     */
    RepoGroupDO getRepoGroup(Long id);

    /**
     * 获得课程文件夹分页
     *
     * @param pageReqVO 分页查询
     * @return 课程文件夹分页
     */
    PageResult<RepoGroupDTO> getRepoGroupPage(RepoGroupPageReqVO pageReqVO);

    /**
     * 获得课程文件夹分页
     *
     * @param repoCategoryId 学科id
     * @return 课程文件夹列表
     */
    List<RepoGroupDO> getRepoGroupListByRepoCategoryId(Long repoCategoryId);


}
