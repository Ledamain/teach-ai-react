package cn.iocoder.teach-ai.module.clientSystem.controller.client.classes;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.ClassesPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classes.vo.ClassesRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classes.ClassesDTO;
import cn.iocoder.teach-ai.module.clientSystem.service.classes.ClassesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 班级")
@RestController
@RequestMapping("/client-api/client-system/classes")
@Validated
public class ClientClassesController {

    @Resource
    private ClassesService classesService;

    @GetMapping("/list")
    @Operation(summary = "获得班级列表")
    public CommonResult<List<ClassesRespVO>> getClassesList(@Valid ClassesPageReqVO pageReqVO) {
        return success(BeanUtils.toBean(classesService.getClassesList(pageReqVO), ClassesRespVO.class));
    }

    @GetMapping("/list-by-repo-category")
    @Operation(summary = "根据学科id获得班级列表")
    public CommonResult<List<ClassesRespVO>> getClassesListByRepoCategoryId(@RequestParam Long repoCategoryId) {
        // 1. 获取所有班级
        List<ClassesDTO> classesList = classesService.getClassesList(new ClassesPageReqVO());

        // 2. 过滤：只保留包含该学科的班级（正确逻辑，不会抛异常）
        List<ClassesDTO> filteredList = classesList.stream()
                .filter(classesDTO -> {
                    // 分割学科ID
                    String[] repoCategoryIds = classesDTO.getRepoCategoryIds().split(",");
                    // 判断是否包含目标学科ID
                    return Arrays.asList(repoCategoryIds).contains(String.valueOf(repoCategoryId));
                })
                .toList();

        // 3. 转换并返回
        return success(BeanUtils.toBean(filteredList, ClassesRespVO.class));
    }

}
