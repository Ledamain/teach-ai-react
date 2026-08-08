package cn.iocoder.teach-ai.module.clientSystem.controller.client.digitalvideo.vo;

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
