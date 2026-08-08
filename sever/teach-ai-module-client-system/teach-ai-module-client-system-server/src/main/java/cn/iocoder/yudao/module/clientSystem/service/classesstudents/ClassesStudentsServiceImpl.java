package cn.iocoder.teach-ai.module.clientSystem.service.classesstudents;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.StudentsDTO;
import java.util.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.classesstudents.ClassesStudentsMapper;
import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientSystem.enums.client.ErrorCodeConstants.*;

/**
 * 班级学生 Service 实现类
 *
 * @author waynelam
 */
@Service
@Validated
public class ClassesStudentsServiceImpl implements ClassesStudentsService {

    @Resource
    private ClassesStudentsMapper classesStudentsMapper;

    @Override
    public Long createClassesStudents(ClassesStudentsSaveReqVO createReqVO) {
        // 插入
        ClassesStudentsDO classesStudents = BeanUtils.toBean(createReqVO, ClassesStudentsDO.class);
        classesStudentsMapper.insert(classesStudents);

        // 返回
        return classesStudents.getId();
    }

    @Override
    public void updateClassesStudents(ClassesStudentsSaveReqVO updateReqVO) {
        // 校验存在
        validateClassesStudentsExists(updateReqVO.getId());
        // 更新
        ClassesStudentsDO updateObj = BeanUtils.toBean(updateReqVO, ClassesStudentsDO.class);
        classesStudentsMapper.updateById(updateObj);
    }

    @Override
    public void deleteClassesStudents(Long id) {
        // 校验存在
        validateClassesStudentsExists(id);
        // 删除
        classesStudentsMapper.deleteById(id);
    }

    @Override
        public void deleteClassesStudentsListByIds(List<Long> ids) {
        // 删除
        classesStudentsMapper.deleteByIds(ids);
        }


    private void validateClassesStudentsExists(Long id) {
        if (classesStudentsMapper.selectById(id) == null) {
            throw exception(CLASSES_STUDENTS_NOT_EXISTS);
        }
    }

    @Override
    public ClassesStudentsDO getClassesStudents(Long id) {
        return classesStudentsMapper.selectById(id);
    }

    @Override
    public PageResult<ClassesStudentsDO> getClassesStudentsPage(ClassesStudentsPageReqVO pageReqVO) {
        return classesStudentsMapper.selectPage(pageReqVO);
    }

    @Override
    public List<StudentsDTO> getClassesStudentsList(ClassesStudentsPageReqVO pageReqVO) {
        return classesStudentsMapper.selectListRecord(pageReqVO);
    }

    public List<ClassesStudentsDO> getClassesStudentsListOrigin(ClassesStudentsPageReqVO pageReqVO) {
        return classesStudentsMapper.selectList(pageReqVO);
    }

    @Override
    public Long getClassesStudentsListTotal(Long classesId){
        return classesStudentsMapper.selectCount(new LambdaQueryWrapper<ClassesStudentsDO>().eq(ClassesStudentsDO::getClassesId, classesId));
    }

    @Override
    public ClassesStudentsDO getClassesStudentsByUserId(Long userId) {
        return classesStudentsMapper.selectOne(new LambdaQueryWrapper<ClassesStudentsDO>().eq(ClassesStudentsDO::getStudentUserId, userId));
    }

}
