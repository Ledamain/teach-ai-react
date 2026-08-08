package cn.iocoder.teach-ai.module.clientSystem.controller.client.repocategory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.ClassesPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.ClassesStudentsPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.RepoCategoryPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.RepoCategoryRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repocategory.vo.RepoCategorySaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repocategory.RepoCategoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.StudentsDTO;
import cn.iocoder.teach-ai.module.clientSystem.service.classes.ClassesService;
import cn.iocoder.teach-ai.module.clientSystem.service.classesstudents.ClassesStudentsService;
import cn.iocoder.teach-ai.module.clientSystem.service.repocategory.RepoCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 学科")
@RestController
@RequestMapping("/client-api/client-system/repo-category")
@Validated
public class ClientRepoCategoryController {

    @Resource
    private RepoCategoryService repoCategoryService;

    @Resource
    private ClassesService classesService;

    @Resource
    private ClassesStudentsService classesStudentsService;

    @GetMapping("/list")
    @Operation(summary = "获得学科列表")
    public CommonResult<List<RepoCategoryRespVO>> getRepoCategoryList(@RequestParam(required = false) Long teacherUserId) {
        // 所有学科
        List<RepoCategoryDTO> list = repoCategoryService.getRepoCategoryListForClient(new RepoCategoryPageReqVO().setTeacherUserId(teacherUserId));
        // 所有班级
        List<ClassesDTO> classesList = classesService.getClassesList(new ClassesPageReqVO());
        list.forEach(repoCategoryDTO -> {
            // 学生总数默认0
            repoCategoryDTO.setStudentCount(0L);
            // 获得学科id
            String courseId = String.valueOf(repoCategoryDTO.getId());
            classesList.forEach(classesDTO -> {
                // 班级所学所有学科
                for (String s : classesDTO.getRepoCategoryIds().split(",")) {
                    // 如果这个班级学这个学科
                    if (courseId.equals(s)){
                        // 学科总人数+
                        repoCategoryDTO.setStudentCount(repoCategoryDTO.getStudentCount()+classesService.getClassesStudentsListByClassesIdExt(classesDTO.getId()).size());
                    }
                }

            });
        });
        return success(BeanUtils.toBean(list, RepoCategoryRespVO.class));
    }
    @GetMapping("/list-by-student-id")
    @Operation(summary = "根据学生id获得学科列表")
    public CommonResult<List<RepoCategoryRespVO>> getRepoCategoryListByStudentId(@RequestParam(required = false) Long studentUserId) {
        // 所有班级
        List<ClassesDTO> classesList = classesService.getClassesList(new ClassesPageReqVO());
        log.info("学生id:{}",studentUserId);
        List<RepoCategoryRespVO> list = new ArrayList<>();
        // 遍历学生所有班级
        ClassesStudentsPageReqVO classesStudentsPageReqVO = new ClassesStudentsPageReqVO().setStudentUserId(studentUserId);
        for (StudentsDTO studentsDTO : classesStudentsService.getClassesStudentsList(classesStudentsPageReqVO)) {
            log.info("学生班级:{}",studentsDTO);
            // 获取班级id
            Long classesId = studentsDTO.getClassesId();
            // 获得班级所有学科
            String repoCategoryIds = classesService.getClasses(classesId).getRepoCategoryIds();
            log.info("班级学科:{}",repoCategoryIds);
            for (String s : repoCategoryIds.split(",")) {
                // 获得学科id
                Long repoCategoryId = Long.valueOf(s);
                // 获得学科
                RepoCategoryDTO repoCategoryForClient = repoCategoryService.getRepoCategoryForClient(repoCategoryId);
                // 学生总数默认0
                repoCategoryForClient.setStudentCount(0L);
                classesList.forEach(classesDTO -> {
                    // 班级所学所有学科
                    for (String o : classesDTO.getRepoCategoryIds().split(",")) {
                        // 如果这个班级学这个学科
                        if (s.equals(o)){
                            // 学科总人数+
                            repoCategoryForClient.setStudentCount(repoCategoryForClient.getStudentCount()+classesService.getClassesStudentsListByClassesIdExt(classesDTO.getId()).size());
                        }
                    }

                });
                // 添加到列表
                list.add(BeanUtils.toBean(repoCategoryForClient, RepoCategoryRespVO.class));
            }
        }
        log.info("学科原始列表:{}",list);
        List<RepoCategoryRespVO> bean = list.stream().distinct().toList();
        return success(bean);
    }

    @PostMapping("/create")
    @Operation(summary = "创建知识库类别（学科）")
    public CommonResult<Long> createRepoCategory(@Valid @RequestBody RepoCategorySaveReqVO createReqVO) {
        return success(repoCategoryService.createRepoCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新知识库类别（学科）")
    public CommonResult<Boolean> updateRepoCategory(@Valid @RequestBody RepoCategorySaveReqVO updateReqVO) {
        repoCategoryService.updateRepoCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识库类别")
    public CommonResult<Boolean> deleteRepoCategory(@RequestParam("id") Long id) {
        repoCategoryService.deleteRepoCategory(id);
        return success(true);
    }

}
