package cn.iocoder.teach-ai.module.clientSystem.service.classes;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * 班级 Service 接口
 *
 * @author waynelam
 */
public interface ClassesService {

    /**
     * 创建班级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClasses(@Valid ClassesSaveReqVO createReqVO);

    /**
     * 更新班级
     *
     * @param updateReqVO 更新信息
     */
    void updateClasses(@Valid ClassesSaveReqVO updateReqVO);

    /**
     * 删除班级
     *
     * @param id 编号
     */
    void deleteClasses(Long id);

    /**
    * 批量删除班级
    *
    * @param ids 编号
    */
    void deleteClassesListByIds(List<Long> ids);

    /**
     * 获得班级
     *
     * @param id 编号
     * @return 班级
     */
    ClassesDO getClasses(Long id);

    /**
     * 获得班级分页
     *
     * @param pageReqVO 分页查询
     * @return 班级分页
     */
    PageResult<ClassesDTO> getClassesPage(ClassesPageReqVO pageReqVO);

    /**
     * 获得班级列表
     *
      * @param pageReqVO 列表查询
      * @return 班级列表
     */
    List<ClassesDTO> getClassesList(ClassesPageReqVO pageReqVO);

    // ==================== 子表（班级学生） ====================

    /**
     * 获得班级学生列表
     *
     * @param classesId 班级id
     * @return 班级学生列表
     */
    List<ClassesStudentsDO> getClassesStudentsListByClassesId(Long classesId);

    /**
     * 获得班级学生列表
     *
     * @param classesId 班级id
     * @return 班级学生列表
     */
    List<ClassesStudentsDTO> getClassesStudentsListByClassesIdExt(Long classesId);


}
