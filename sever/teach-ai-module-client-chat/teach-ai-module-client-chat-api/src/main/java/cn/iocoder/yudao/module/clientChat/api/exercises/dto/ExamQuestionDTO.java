package cn.iocoder.teach-ai.module.clientChat.api.exercises.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单个题目实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDTO {
    // 题目ID
    private Integer id;
    // 题目类型 single/multiple/judge/short
    private String type;
    // 题干
    private String title;
    // 选项（简答可为 null）
    private List<String> options;
    // 答案：单选/判断/简答用 String，多选用 List<String>
    private Object answer;
    // 分值
    private Integer score;
    // 解析
    private String analysis;

}
