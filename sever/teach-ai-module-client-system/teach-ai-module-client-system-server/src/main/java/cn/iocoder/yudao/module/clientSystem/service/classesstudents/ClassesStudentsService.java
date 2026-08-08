package cn.iocoder.teach-ai.module.clientSystem.service.classesstudents;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.StudentsDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 班级学生 Service 接口
 *
 * @author waynelam
 */
public interface ClassesStudentsService {

    /**
     * 创建班级学生
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClassesStudents(@Valid ClassesStudentsSaveReqVO createReqVO);

    /**
     * 更新班级学生
     *
     * @param updateReqVO 更新信息
     */
    void updateClassesStudents(@Valid ClassesStudentsSaveReqVO updateReqVO);

    /**
     * 删除班级学生
     *
     * @param id 编号
     */
    void deleteClassesStudents(Long id);

    /**
    * 批量删除班级学生
    *
    * @param ids 编号
    */
    void deleteClassesStudentsListByIds(List<Long> ids);

    /**
     * 获得班级学生
     *
     * @param id 编号
     * @return 班级学生
     */
    ClassesStudentsDO getClassesStudents(Long id);

    /**
     * 获得班级学生分页
     *
     * @param pageReqVO 分页查询
     * @return 班级学生分页
     */
    PageResult<ClassesStudentsDO> getClassesStudentsPage(ClassesStudentsPageReqVO pageReqVO);

    /**
     * 获得班级学生列表
     *
     * @param pageReqVO 列表查询
      * @return 班级学生列表
     */
    List<StudentsDTO> getClassesStudentsList(ClassesStudentsPageReqVO pageReqVO);

    /**
     * 获得班级学生列表
     *
     * @param pageReqVO 列表查询
     * @return 班级学生列表
     */
    List<ClassesStudentsDO> getClassesStudentsListOrigin(ClassesStudentsPageReqVO pageReqVO);

    /**
     * 获得班级学生总数
     *
     * @param classesId 班级id
     * @return 班级学生列表
     */
    Long getClassesStudentsListTotal(Long classesId);

    /**
     * 通过用户id获得班级学生
     *
     * @param userId 用户id
     * @return 班级学生列表
     */
    ClassesStudentsDO getClassesStudentsByUserId(Long userId);

}
