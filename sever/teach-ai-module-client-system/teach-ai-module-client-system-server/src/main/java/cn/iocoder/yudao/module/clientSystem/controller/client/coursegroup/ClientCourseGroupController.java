package cn.iocoder.teach-ai.module.clientSystem.controller.client.coursegroup;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.coursegroup.vo.CourseGroupRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.coursegroup.CourseGroupDO;
import cn.iocoder.teach-ai.module.clientSystem.service.coursegroup.CourseGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 班级")
@RestController
@RequestMapping("/client-api/client-system/course-group")
@Validated
public class ClientCourseGroupController {

    @Resource
    private CourseGroupService courseGroupService;

    @GetMapping("/list")
    @Operation(summary = "获得课程组列表")
    public CommonResult<List<CourseGroupRespVO>> getCourseGroupList() {
        List<CourseGroupDO> list = courseGroupService.getCourseGroupList();
        return success(BeanUtils.toBean(list, CourseGroupRespVO.class));
    }

}
