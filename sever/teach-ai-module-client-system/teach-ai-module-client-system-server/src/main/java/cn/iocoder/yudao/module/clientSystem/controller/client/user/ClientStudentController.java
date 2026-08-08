package cn.iocoder.teach-ai.module.clientSystem.controller.client.user;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.ClassesPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.ClassesRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.ClassesStudentsPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.user.vo.StudentsRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.StudentsDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.module.clientSystem.service.classes.ClassesService;
import cn.iocoder.teach-ai.module.clientSystem.service.classesstudents.ClassesStudentsService;
import cn.iocoder.teach-ai.module.clientSystem.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 班级")
@RestController
@RequestMapping("/client-api/client-system/user-student")
@Validated
public class ClientStudentController {

    @Resource
    private ClassesService classesService;

    @Resource
    private ClassesStudentsService classesStudentsService;


    @GetMapping("/list-by-course")
    @Operation(summary = "根据课程id获得学生列表")
    public CommonResult<List<StudentsRespVO>> getClassesPage(@RequestParam Long courseId) {
        ArrayList<StudentsDTO> studentsDTOS = new ArrayList<>();
        // 所有班级
        List<ClassesDTO> classesList = classesService.getClassesList(new ClassesPageReqVO());
        classesList.forEach(classesDTO -> {
            // 班级所学所有学科
            for (String s : classesDTO.getRepoCategoryIds().split(",")) {
                // 如果这个班级学这个学科
                if (String.valueOf(courseId).equals(s)){
                    // 获得班级id
                    Long classesDTOId = classesDTO.getId();
                    List<StudentsDTO> classesStudentsList = classesStudentsService.getClassesStudentsList(new ClassesStudentsPageReqVO().setClassesId(classesDTOId));
                    studentsDTOS.addAll(classesStudentsList);
                }
            }

        });
        return success(BeanUtils.toBean(studentsDTOS, StudentsRespVO.class));
    }

}
