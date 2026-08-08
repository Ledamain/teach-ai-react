package cn.iocoder.teach-ai.module.clientSystem.controller.admin.exercise;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo.CourseAnalyticsRespVO;
import cn.iocoder.teach-ai.module.clientSystem.service.exerciseinfo.ExerciseInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

/**
 * 课程数据分析 Controller
 *
 * @author waynelam
 */
@Tag(name = "管理后台 - 课程数据分析")
@RestController
@RequestMapping("/client-system/exercise")
@Validated
public class ExerciseAnalyticsController {

    @Resource
    private ExerciseInfoService exerciseInfoService;

    @GetMapping("/course-analytics")
    @Operation(summary = "获取课程数据分析")
    @Parameter(name = "repoCategoryId", description = "课程分类ID", required = true, example = "1")
    public CommonResult<CourseAnalyticsRespVO> getCourseAnalytics(
            @RequestParam("repoCategoryId") Long repoCategoryId) {
        CourseAnalyticsRespVO analytics = exerciseInfoService.getCourseAnalytics(repoCategoryId);
        return success(analytics);
    }
}
