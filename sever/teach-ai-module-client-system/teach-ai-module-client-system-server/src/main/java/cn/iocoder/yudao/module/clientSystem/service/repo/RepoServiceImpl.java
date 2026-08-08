package cn.iocoder.teach-ai.module.clientSystem.service.repo;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repo.RepoMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.REPO_NOT_EXISTS;

/**
 * 知识库 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class RepoServiceImpl implements RepoService {

    @Resource
    private RepoMapper repoMapper;

    @Override
    public Long createRepo(RepoSaveReqVO createReqVO) {
        // 插入
        RepoDO repo = BeanUtils.toBean(createReqVO, RepoDO.class);
        repoMapper.insert(repo);

        // 返回
        return repo.getId();
    }

    @Override
    public void updateRepo(RepoSaveReqVO updateReqVO) {
        // 校验存在
        validateRepoExists(updateReqVO.getId());
        // 更新
        RepoDO updateObj = BeanUtils.toBean(updateReqVO, RepoDO.class);
        repoMapper.updateById(updateObj);
    }

    @Override
    public void deleteRepo(Long id) {
        // 校验存在
        validateRepoExists(id);
        // 删除
        repoMapper.deleteById(id);
    }

    @Override
        public void deleteRepoListByIds(List<Long> ids) {
        // 删除
        repoMapper.deleteByIds(ids);
        }


    private void validateRepoExists(Long id) {
        if (repoMapper.selectById(id) == null) {
            throw exception(REPO_NOT_EXISTS);
        }
    }

    @Override
    public RepoDO getRepo(Long id) {
        return repoMapper.selectById(id);
    }

    @Override
    public PageResult<RepoDTO> getRepoPage(RepoPageReqVO pageReqVO) {
        return repoMapper.selectPage(pageReqVO);
    }


}
