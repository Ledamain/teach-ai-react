package cn.iocoder.teach-ai.module.clientChat.api.exercises.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整套试卷实体（对应最外层 JSON）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamPaperDTO {
    // 试卷标题
    private String title;
    // 试卷描述
    private String description;
    // 总分
    private Integer totalScore;
    // 题目列表
    private List<ExamQuestionDTO> questions;

}
