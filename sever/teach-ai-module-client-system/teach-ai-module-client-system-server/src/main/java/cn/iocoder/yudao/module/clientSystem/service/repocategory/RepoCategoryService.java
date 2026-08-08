package cn.iocoder.teach-ai.module.clientSystem.service.repocategory;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 知识库类别 Service 接口
 *
 * @author waynelam
 */
public interface RepoCategoryService {

    /**
     * 创建知识库类别
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRepoCategory(@Valid RepoCategorySaveReqVO createReqVO);

    /**
     * 更新知识库类别
     *
     * @param updateReqVO 更新信息
     */
    void updateRepoCategory(@Valid RepoCategorySaveReqVO updateReqVO);

    /**
     * 删除知识库类别
     *
     * @param id 编号
     */
    void deleteRepoCategory(Long id);

    /**
    * 批量删除知识库类别
    *
    * @param ids 编号
    */
    void deleteRepoCategoryListByIds(List<Long> ids);

    /**
     * 获得知识库类别
     *
     * @param id 编号
     * @return 知识库类别
     */
    RepoCategoryDO getRepoCategory(Long id);

    /**
     * 获得知识库类别分页
     *
     * @param pageReqVO 分页查询
     * @return 知识库类别分页
     */
    PageResult<RepoCategoryDTO> getRepoCategoryPage(RepoCategoryPageReqVO pageReqVO);

    /**
     * 获得知识库类别列表
     *
      * @param pageReqVO
      * @return识库类别列表
     */
    List<RepoCategoryDO> getRepoCategoryList(RepoCategoryPageReqVO pageReqVO);

    /**
     * 获得知识库类别列表（封装）
     *
     * @param pageReqVO
     * @return识库类别列表
     */
    List<RepoCategoryDTO> getRepoCategoryListForClient(RepoCategoryPageReqVO pageReqVO);

    /**
     * 获得知识库类别（封装）
     *
     * @param id 编号
     * @return 知识库类别
     */
    RepoCategoryDTO getRepoCategoryForClient(Long id);

}
