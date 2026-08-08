package cn.iocoder.teach-ai.module.clientChat.service.exercises;

import cn.iocoder.teach-ai.module.clientChat.api.exercises.dto.ExamPaperDTO;
import org.springframework.stereotype.Service;

@Service
public interface ExercisesService {

    ExamPaperDTO createExamPaper(String query);

    String createExamPaperJSON(String query);

}
