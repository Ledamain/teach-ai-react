package cn.iocoder.teach-ai.module.clientSystem.service.repocategory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repocategory.RepoCategoryMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.REPO_CATEGORY_NOT_EXISTS;

/**
 * 知识库类别 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class RepoCategoryServiceImpl implements RepoCategoryService {

    @Resource
    private RepoCategoryMapper repoCategoryMapper;

    @Override
    public Long createRepoCategory(RepoCategorySaveReqVO createReqVO) {
        // 插入
        RepoCategoryDO repoCategory = BeanUtils.toBean(createReqVO, RepoCategoryDO.class);
        repoCategoryMapper.insert(repoCategory);

        // 返回
        return repoCategory.getId();
    }

    @Override
    public void updateRepoCategory(RepoCategorySaveReqVO updateReqVO) {
        // 校验存在
        validateRepoCategoryExists(updateReqVO.getId());
        // 更新
        RepoCategoryDO updateObj = BeanUtils.toBean(updateReqVO, RepoCategoryDO.class);
        repoCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteRepoCategory(Long id) {
        // 校验存在
        validateRepoCategoryExists(id);
        // 删除
        repoCategoryMapper.deleteById(id);
    }

    @Override
        public void deleteRepoCategoryListByIds(List<Long> ids) {
        // 删除
        repoCategoryMapper.deleteByIds(ids);
        }


    private void validateRepoCategoryExists(Long id) {
        if (repoCategoryMapper.selectById(id) == null) {
            throw exception(REPO_CATEGORY_NOT_EXISTS);
        }
    }

    @Override
    public RepoCategoryDO getRepoCategory(Long id) {
        return repoCategoryMapper.selectById(id);
    }

    @Override
    public PageResult<RepoCategoryDTO> getRepoCategoryPage(RepoCategoryPageReqVO pageReqVO) {
        return repoCategoryMapper.selectPage(pageReqVO);
    }

    @Override
    public List<RepoCategoryDO> getRepoCategoryList(RepoCategoryPageReqVO pageReqVO) {
        return repoCategoryMapper.selectList(pageReqVO);
    }

    @Override
    public List<RepoCategoryDTO> getRepoCategoryListForClient(RepoCategoryPageReqVO pageReqVO) {
        return repoCategoryMapper.selectListForClient(pageReqVO);
    }

    @Override
    public RepoCategoryDTO getRepoCategoryForClient(Long id) {
        return repoCategoryMapper.getRepoCategoryForClient(id);
    }

}
