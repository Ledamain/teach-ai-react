package cn.iocoder.teach-ai.module.clientChat.api.exercises;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.module.clientChat.api.exercises.dto.ExamPaperDTO;
import cn.iocoder.teach-ai.module.clientChat.service.exercises.ExercisesService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Valid
public class ExercisesApiImpl implements ExercisesApi{

    @Resource
    private ExercisesService exercisesService;

    @Override
    public CommonResult<ExamPaperDTO> getExamPaper(String query) {
        return CommonResult.success(exercisesService.createExamPaper(query));
    }

    @Override
    public CommonResult<String> getExamPaperJSON(String query) {
        return CommonResult.success(exercisesService.createExamPaperJSON(query));
    }
}
