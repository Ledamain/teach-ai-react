package cn.iocoder.teach-ai.module.clientSystem.service.repogroup;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repogroup.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repogroup.RepoGroupDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repogroup.RepoGroupMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * 课程文件夹 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class RepoGroupServiceImpl implements RepoGroupService {

    @Resource
    private RepoGroupMapper repoGroupMapper;

    @Override
    public Long createRepoGroup(RepoGroupSaveReqVO createReqVO) {
        // 插入
        RepoGroupDO repoGroup = BeanUtils.toBean(createReqVO, RepoGroupDO.class);
        repoGroupMapper.insert(repoGroup);

        // 返回
        return repoGroup.getId();
    }

    @Override
    public void updateRepoGroup(RepoGroupSaveReqVO updateReqVO) {
        // 校验存在
        validateRepoGroupExists(updateReqVO.getId());
        // 更新
        RepoGroupDO updateObj = BeanUtils.toBean(updateReqVO, RepoGroupDO.class);
        repoGroupMapper.updateById(updateObj);
    }

    @Override
    public void deleteRepoGroup(Long id) {
        // 校验存在
        validateRepoGroupExists(id);
        // 删除
        repoGroupMapper.deleteById(id);
    }

    @Override
        public void deleteRepoGroupListByIds(List<Long> ids) {
        // 删除
        repoGroupMapper.deleteByIds(ids);
        }


    private void validateRepoGroupExists(Long id) {
        if (repoGroupMapper.selectById(id) == null) {
            throw exception(REPO_GROUP_NOT_EXISTS);
        }
    }

    @Override
    public RepoGroupDO getRepoGroup(Long id) {
        return repoGroupMapper.selectById(id);
    }

    @Override
    public PageResult<RepoGroupDTO> getRepoGroupPage(RepoGroupPageReqVO pageReqVO) {
        return repoGroupMapper.selectPage(pageReqVO);
    }

    @Override
    public List<RepoGroupDO> getRepoGroupListByRepoCategoryId(Long repoCategoryId) {
        return repoGroupMapper.selectListByRepoCategoryId(repoCategoryId);
    }

}
