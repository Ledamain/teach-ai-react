package cn.iocoder.teach-ai.module.clientChat.service.exercises;

import cn.iocoder.teach-ai.module.clientChat.api.exercises.dto.ExamPaperDTO;
import cn.iocoder.teach-ai.module.clientChat.service.exercises.aiService.ExercisesAiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.EXERCISES_EXCEPTION;

@Slf4j
@Service
public class ExercisesServiceImpl implements ExercisesService{

    @Resource
    private ExercisesAiService exercisesAiService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public ExamPaperDTO createExamPaper(String query) {
        try {
            String exam = exercisesAiService.exercisesChat(query);
            ExamPaperDTO examPaperDTO = objectMapper.readValue(exam, ExamPaperDTO.class);
            return examPaperDTO;
        } catch (JsonProcessingException e) {
            log.error("练习题生成错误：{}",e.getMessage());
            throw exception(EXERCISES_EXCEPTION);
        }
    }

    @Override
    public String createExamPaperJSON(String query) {
        return exercisesAiService.exercisesChat(query);
    }
}
