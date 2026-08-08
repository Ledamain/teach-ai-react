package cn.iocoder.teach-ai.module.clientChat.api.image.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult {
    private String status; // PENDING / DONE / ERROR
    private String result;
    private String errorMsg;
}
