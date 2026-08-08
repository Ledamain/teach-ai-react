package cn.iocoder.teach-ai.module.clientSystem.controller.client.exercise;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.iocoder.teach-ai.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.framework.excel.core.util.ExcelUtils;
import cn.iocoder.teach-ai.module.clientChat.api.exercises.ExercisesApi;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.classesstudents.vo.ClassesStudentsPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseinfo.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.ExerciseResultPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.ExerciseResultRespVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.exerciseresult.vo.ExerciseResultSaveReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.exercise.vo.ClientExerciseResultRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.classesstudents.ClassesStudentsDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseresult.ExerciseResultDTO;
import cn.iocoder.teach-ai.module.clientSystem.service.classesstudents.ClassesStudentsService;
import cn.iocoder.teach-ai.module.clientSystem.service.exerciseresult.ExerciseResultService;import cn.iocoder.teach-ai.module.clientSystem.service.exerciseinfo.ExerciseInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.teach-ai.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.error;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 习题")
@RestController
@RequestMapping("/client-api/client-system/exercise")
@Validated
public class ClientExerciseController {

    @Resource
    private ExerciseInfoService exerciseInfoService;

    @Resource
    private ExerciseResultService exerciseResultService;

    @Resource
    private ClassesStudentsService classesStudentsService;

    @Resource
    private ExercisesApi exercisesApi;

    @PostMapping("/create")
    @Operation(summary = "创建练习题")
    public CommonResult<Long> createExerciseInfo(@Valid @RequestBody ExerciseInfoGenSaveReqVO createReqVO) {
        createReqVO.setContent(exercisesApi.getExamPaperJSON(createReqVO.getDescription()).getData()).setStatus(0L);
        Long exerciseId = exerciseInfoService.createExerciseInfo(createReqVO);
        return success(exerciseId);
    }

    @PostMapping("/post")
    @Operation(summary = "发布练习题")
    public CommonResult<Boolean> postExerciseInfo(@Valid @RequestBody ExerciseInfoSaveReqVO updateReqVO) {
        updateReqVO.setStatus(1L);
        String[] classesIds = updateReqVO.getClassesId().replace("_", "").replace("C", "").split(",");
        for (String classesId : classesIds) {
            List<ClassesStudentsDO> list = classesStudentsService.getClassesStudentsListOrigin(new ClassesStudentsPageReqVO().setClassesId(Long.parseLong(classesId)));
            for (ClassesStudentsDO student : list) {
                exerciseResultService.createExerciseResult(new ExerciseResultSaveReqVO().setExerciseId(updateReqVO.getId()).setStudentUserId(student.getStudentUserId()));
            }
        }
        exerciseInfoService.updateExerciseInfo(updateReqVO);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取练习题列表")
    public CommonResult<List<ExerciseInfoRespVO>> getExerciseInfoList(@Valid ExerciseInfoPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExerciseInfoDO> list = exerciseInfoService.getExerciseInfoPage(pageReqVO).getList();
        List<ExerciseInfoRespVO> listVO = BeanUtils.toBean(list, ExerciseInfoRespVO.class);
        for (ExerciseInfoRespVO exerciseInfo : listVO) {
            Long status = exerciseInfo.getStatus().equals(0L) ? 0L : (exerciseInfo.getStartTime() == null || exerciseInfo.getEndTime() == null) ? 0L : LocalDateTimeUtil.isIn(LocalDateTimeUtil.now(), exerciseInfo.getStartTime(), exerciseInfo.getEndTime()) ? 1L : 2L;
            exerciseInfo.setSubmissionCount(exerciseResultService.getSubmissionCount(exerciseInfo.getId())).setTotalStudents(exerciseResultService.getStudentCount(exerciseInfo.getId())).setStatus(status);
        }
        return success(listVO);
    }

    @GetMapping("/get")
    @Operation(summary = "获得练习题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ExerciseInfoRespVO> getExerciseInfo(@RequestParam("id") Long id) {
        ExerciseInfoDO exerciseInfo = exerciseInfoService.getExerciseInfo(id);
        ExerciseInfoRespVO respVO = BeanUtils.toBean(exerciseInfo, ExerciseInfoRespVO.class);
        Long status = respVO.getStatus().equals(0L) ? 0L : (respVO.getStartTime() == null || respVO.getEndTime() == null) ? 0L : LocalDateTimeUtil.isIn(LocalDateTimeUtil.now(), respVO.getStartTime(), respVO.getEndTime()) ? 1L : 2L;
        respVO.setSubmissionCount(exerciseResultService.getSubmissionCount(exerciseInfo.getId())).setTotalStudents(exerciseResultService.getStudentCount(exerciseInfo.getId())).setStatus(status);
        return success(respVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新练习题")
    public CommonResult<Boolean> updateExerciseInfo(@Valid @RequestBody ExerciseInfoSaveReqVO updateReqVO) {
        exerciseInfoService.updateExerciseInfo(updateReqVO);
        return success(true);
    }

    @GetMapping("/get-result")
    @Operation(summary = "获得评判结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ExerciseResultRespVO> getExerciseResult(@RequestParam("id") Long id) {
        ExerciseResultDO exerciseResult = exerciseResultService.getExerciseResult(id);
        return success(BeanUtils.toBean(exerciseResult, ExerciseResultRespVO.class));
    }

    @GetMapping("/list-result")
    @Operation(summary = "获得评判结果列表")
    public CommonResult<List<ClientExerciseResultRespVO>> getExerciseResultList(@Valid ExerciseResultPageReqVO pageReqVO){
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ExerciseResultDTO> list = exerciseResultService.getExerciseResultPage(pageReqVO).getList();
        List<ClientExerciseResultRespVO> bean = BeanUtils.toBean(list, ClientExerciseResultRespVO.class);
        bean.forEach(result -> {
            JSONObject entries = new JSONObject(result.getTranscript());
            Integer userScore = entries.getInt("userScore");
            result.setScore(userScore);
        });
        return success(bean);
    }

    @GetMapping("/student/list")
    @Operation(summary = "学生获取作业列表")
    public CommonResult<List<ExerciseInfoRespVO>> getStudentExerciseInfoList(@Valid ExerciseInfoPageReqVO pageReqVO) {
        ExerciseResultPageReqVO pageReqVO2 = new ExerciseResultPageReqVO();
        pageReqVO2.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO2.setStudentUserId(pageReqVO.getStudentUserId());
        pageReqVO2.setRepoCategoryId(pageReqVO.getRepoCategoryId());
        List<ExerciseResultDTO> list = exerciseResultService.getExerciseResultInfo(pageReqVO2).getList();
        List<ExerciseInfoDO> listDO = new ArrayList<>();
        for (ExerciseResultDTO exerciseResult : list) {
            ExerciseInfoDO info = exerciseInfoService.getExerciseInfo(exerciseResult.getExerciseId());
            Long status = info.getStatus().equals(0L) ? 0L : (info.getStartTime() == null || info.getEndTime() == null) ? 0L : LocalDateTimeUtil.isIn(LocalDateTimeUtil.now(), info.getStartTime(), info.getEndTime()) ? 1L : 2L;
            info.setStatus(status);
            listDO.add(info);
        }
        List<ExerciseInfoRespVO> bean = BeanUtils.toBean(listDO, ExerciseInfoRespVO.class);
        bean.forEach( exerciseInfoRespVO -> {
            log.info("查询出的数据：{}",exerciseInfoRespVO);

            JSONObject json = new JSONObject(exerciseInfoRespVO.getContent());
            Integer totalScore = json.getInt("totalScore");
            JSONArray questions = json.getJSONArray("questions");
            exerciseInfoRespVO.setTotalScore(totalScore);
            exerciseInfoRespVO.setQuestionCount(questions.size());

            ExerciseResultPageReqVO exerciseResultPageReqVO = new ExerciseResultPageReqVO();
            exerciseResultPageReqVO.setExerciseId(exerciseInfoRespVO.getId());
            exerciseResultPageReqVO.setStudentUserId(pageReqVO.getStudentUserId());
//            log.info("查询出的学生作业参数：{}",exerciseResultPageReqVO);
            exerciseResultService.getExerciseResultInfo(exerciseResultPageReqVO).getList().forEach( result -> {
//                log.info("查询出的学生数据：{}",result);
                exerciseInfoRespVO.setCompleted(result.getCompleted());
                // 封装得分
                if (exerciseInfoRespVO.getCompleted() == 1L){
                    JSONObject entries = new JSONObject(result.getTranscript());
                    Integer userScore = entries.getInt("userScore");
                    exerciseInfoRespVO.setUserScore(userScore);
                }
            });

        });
        return success(bean);
    }

    @GetMapping("/get-student")
    @Operation(summary = "学生获得练习题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ExerciseInfoRespVO> getExerciseInfoStudent(@RequestParam("id") Long id, @RequestParam("studentUserId") Long studentUserId) {
        ExerciseInfoDO exerciseInfo = exerciseInfoService.getExerciseInfo(id);
        ExerciseInfoRespVO respVO = BeanUtils.toBean(exerciseInfo, ExerciseInfoRespVO.class);
        Long status = respVO.getStatus().equals(0L) ? 0L : (respVO.getStartTime() == null || respVO.getEndTime() == null) ? 0L : LocalDateTimeUtil.isIn(LocalDateTimeUtil.now(), respVO.getStartTime(), respVO.getEndTime()) ? 1L : 2L;
        respVO.setSubmissionCount(exerciseResultService.getSubmissionCount(exerciseInfo.getId())).setTotalStudents(exerciseResultService.getStudentCount(exerciseInfo.getId())).setStatus(status);

        JSONObject json = new JSONObject(respVO.getContent());
        Integer totalScore = json.getInt("totalScore");
        JSONArray questions = json.getJSONArray("questions");
        respVO.setTotalScore(totalScore);
        respVO.setQuestionCount(questions.size());

        ExerciseResultPageReqVO exerciseResultPageReqVO = new ExerciseResultPageReqVO();
        exerciseResultPageReqVO.setExerciseId(id);
        exerciseResultPageReqVO.setStudentUserId(studentUserId);
        log.info("开始封装学生是否提交");
        exerciseResultService.getExerciseResultInfo(exerciseResultPageReqVO).getList().forEach( result -> {
            log.info("获得的result为:{}", result);
            respVO.setCompleted(result.getCompleted());

            // 封装得分
            if (respVO.getCompleted() == 1L){
                JSONObject entries = new JSONObject(result.getTranscript());
                Integer userScore = entries.getInt("userScore");
                respVO.setUserScore(userScore);
            }
        });
        return success(respVO);
    }

    @PutMapping("/student/update-result")
    @Operation(summary = "更新结果")
    public CommonResult<Boolean> updateExerciseResult(@Valid @RequestBody ExerciseResultSaveReqVO updateReqVO) {
        Long exerciseId = updateReqVO.getId();
        log.info("作业id:{}",exerciseId);
        log.info("学生id:{}",updateReqVO.getStudentUserId());
        Long resultId = 0L;
        for (ExerciseResultDTO exerciseResultDTO : exerciseResultService.getExerciseResultPage(new ExerciseResultPageReqVO().setExerciseId(exerciseId).setStudentUserId(updateReqVO.getStudentUserId())).getList()) {
            resultId = exerciseResultDTO.getId();
            log.info("学生作业结果id：{}",resultId);
        }

        ExerciseResultDO result = exerciseResultService.getExerciseResult(resultId);
        if (result.getCompleted() == 1L) {
            return error(400, "不允许重复提交");
        } else {
            updateReqVO.setId(result.getId()).setExerciseId(result.getExerciseId()).setStudentUserId(result.getStudentUserId()).setCompleted(1L);
            exerciseResultService.updateExerciseResult(updateReqVO);
        }
        return success(true);
    }


    @GetMapping("/student-analytics")
    @Operation(summary = "获取学生个人数据分析（硬编码模拟数据）")
    public CommonResult<StudentAnalyticsRespVO> getStudentAnalytics(
            @RequestParam("repoCategoryId") Long repoCategoryId,
            @RequestParam("studentUserId") String studentUserId) {
        // 模拟数据 — 基于课程分类微调
        String mainSubject = "人工智能导论";

        StudentAnalyticsRespVO resp = StudentAnalyticsRespVO.builder()
            .studentId(studentUserId)
            .studentName("王浩")
            .className("AI研一(1)班")
            .totalQuestions(48)
            .mainSubject(mainSubject)
            .lastActive("2026-07-16 14:30")
            .averageScore(82)
            .rankInClass(5)
            .totalStudents(87)
            .complexityDistribution(List.of(
                StudentAnalyticsRespVO.ComplexityDistItem.builder().level("简单").count(18).percentage(37.5).build(),
                StudentAnalyticsRespVO.ComplexityDistItem.builder().level("中等").count(22).percentage(45.8).build(),
                StudentAnalyticsRespVO.ComplexityDistItem.builder().level("困难").count(8).percentage(16.7).build()
            ))
            .learningTrend(List.of(
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-10").score(72).build(),
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-11").score(75).build(),
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-12").score(70).build(),
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-13").score(80).build(),
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-14").score(78).build(),
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-15").score(85).build(),
                StudentAnalyticsRespVO.LearningTrendItem.builder().date("07-16").score(82).build()
            ))
            .subjectDistribution(List.of(
                StudentAnalyticsRespVO.SubjectDistItem.builder().subject("CNN基础").count(12).percentage(25.0).build(),
                StudentAnalyticsRespVO.SubjectDistItem.builder().subject("RNN/LSTM").count(10).percentage(20.8).build(),
                StudentAnalyticsRespVO.SubjectDistItem.builder().subject("Transformer").count(8).percentage(16.7).build(),
                StudentAnalyticsRespVO.SubjectDistItem.builder().subject("反向传播").count(10).percentage(20.8).build(),
                StudentAnalyticsRespVO.SubjectDistItem.builder().subject("优化器").count(8).percentage(16.7).build()
            ))
            .recentAssignments(List.of(
                StudentAnalyticsRespVO.RecentAssignmentItem.builder().id("1").title("CNN课后练习").score(90).totalScore(100).submitTime("2026-07-15 16:20").build(),
                StudentAnalyticsRespVO.RecentAssignmentItem.builder().id("2").title("反向传播专题").score(78).totalScore(100).submitTime("2026-07-14 10:15").build(),
                StudentAnalyticsRespVO.RecentAssignmentItem.builder().id("3").title("Transformer注意力机制").score(68).totalScore(100).submitTime("2026-07-13 21:30").build(),
                StudentAnalyticsRespVO.RecentAssignmentItem.builder().id("4").title("深度学习综合测试").score(85).totalScore(100).submitTime("2026-07-12 18:00").build()
            ))
            .learningAdvice(List.of(
                "Transformer注意力机制得分偏低（68分），建议回到学习路径中完成'注意力机制原理'和'Transformer架构详解'两个节点",
                "反向传播专题存在波动，建议多做链式法则的实际演算练习",
                "整体趋势向好，近一周平均分从72提升到82，继续保持当前学习节奏",
                "建议每周至少完成2次综合测试以巩固阶段性成果"
            ))
            .build();

        return success(resp);
    }

    @GetMapping("/course-analytics")
    @Operation(summary = "获取课程数据分析")
    @Parameter(name = "repoCategoryId", description = "课程分类ID", required = true, example = "1")
    public CommonResult<CourseAnalyticsRespVO> getCourseAnalytics(
            @RequestParam("repoCategoryId") Long repoCategoryId) {
        CourseAnalyticsRespVO analytics = exerciseInfoService.getCourseAnalytics(repoCategoryId);
        return success(analytics);
    }
}
