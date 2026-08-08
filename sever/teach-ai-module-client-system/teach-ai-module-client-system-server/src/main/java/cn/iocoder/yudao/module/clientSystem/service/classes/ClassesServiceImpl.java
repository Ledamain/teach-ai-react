package cn.iocoder.teach-ai.module.clientSystem.service.classes;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;

import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.classes.ClassesMapper;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.classesstudents.ClassesStudentsMapper;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.teach-ai.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * 班级 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class ClassesServiceImpl implements ClassesService {

    @Resource
    private ClassesMapper classesMapper;
    @Resource
    private ClassesStudentsMapper classesStudentsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createClasses(ClassesSaveReqVO createReqVO) {
        // 插入
        ClassesDO classes = BeanUtils.toBean(createReqVO, ClassesDO.class);
        classesMapper.insert(classes);


//        // 插入子表
//        createClassesStudentsList(classes.getId(), createReqVO.getClassesStudentss());
        // 返回
        return classes.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClasses(ClassesSaveReqVO updateReqVO) {
        // 校验存在
        validateClassesExists(updateReqVO.getId());
        // 更新
        ClassesDO updateObj = BeanUtils.toBean(updateReqVO, ClassesDO.class);
        classesMapper.updateById(updateObj);

        // 更新子表
//        updateClassesStudentsList(updateReqVO.getId(), updateReqVO.getClassesStudentss());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClasses(Long id) {
        // 校验存在
        validateClassesExists(id);
        // 删除
        classesMapper.deleteById(id);

        // 删除子表
        deleteClassesStudentsByClassesId(id);
    }

    @Override
        @Transactional(rollbackFor = Exception.class)
    public void deleteClassesListByIds(List<Long> ids) {
        // 删除
        classesMapper.deleteByIds(ids);

    // 删除子表
            deleteClassesStudentsByClassesIds(ids);
    }


    private void validateClassesExists(Long id) {
        if (classesMapper.selectById(id) == null) {
            throw exception(CLASSES_NOT_EXISTS);
        }
    }

    @Override
    public ClassesDO getClasses(Long id) {
        return classesMapper.selectById(id);
    }

    @Override
    public PageResult<ClassesDTO> getClassesPage(ClassesPageReqVO pageReqVO) {
        return classesMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ClassesDTO> getClassesList(ClassesPageReqVO pageReqVO) {
        return classesMapper.selectList(pageReqVO);
    }

    // ==================== 子表（班级学生） ====================

    @Override
    public List<ClassesStudentsDO> getClassesStudentsListByClassesId(Long classesId) {
        return classesStudentsMapper.selectListByClassesId(classesId);
    }

    @Override
    public List<ClassesStudentsDTO> getClassesStudentsListByClassesIdExt(Long classesId) {
        return classesStudentsMapper.selectListByClassesIdExt(classesId);
    }

    private void createClassesStudentsList(Long classesId, List<ClassesStudentsDO> list) {
        list.forEach(o -> o.setClassesId(classesId).clean());
        classesStudentsMapper.insertBatch(list);
    }

    private void updateClassesStudentsList(Long classesId, List<ClassesStudentsDO> list) {
	    list.forEach(o -> o.setClassesId(classesId).clean());
	    List<ClassesStudentsDO> oldList = classesStudentsMapper.selectListByClassesId(classesId);
	    List<List<ClassesStudentsDO>> diffList = diffList(oldList, list, (oldVal, newVal) -> {
            boolean same = ObjectUtil.equal(oldVal.getId(), newVal.getId());
            if (same) {
                newVal.setId(oldVal.getId()).clean(); // 解决更新情况下：updateTime 不更新
            }
            return same;
	    });

	    // 第二步，批量添加、修改、删除
	    if (CollUtil.isNotEmpty(diffList.get(0))) {
	        classesStudentsMapper.insertBatch(diffList.get(0));
	    }
	    if (CollUtil.isNotEmpty(diffList.get(1))) {
	        classesStudentsMapper.updateBatch(diffList.get(1));
	    }
	    if (CollUtil.isNotEmpty(diffList.get(2))) {
	        classesStudentsMapper.deleteByIds(convertList(diffList.get(2), ClassesStudentsDO::getId));
	    }
    }

    private void deleteClassesStudentsByClassesId(Long classesId) {
        classesStudentsMapper.deleteByClassesId(classesId);
    }

	private void deleteClassesStudentsByClassesIds(List<Long> classesIds) {
        classesStudentsMapper.deleteByClassesIds(classesIds);
	}

}
