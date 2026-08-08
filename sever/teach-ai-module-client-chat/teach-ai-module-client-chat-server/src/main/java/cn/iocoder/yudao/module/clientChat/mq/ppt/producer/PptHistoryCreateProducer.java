package cn.iocoder.teach-ai.module.clientChat.mq.ppt.producer;

import cn.iocoder.teach-ai.module.clientChat.mq.ppt.producer.bo.PptArtifaceExportParam;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PptHistoryCreateProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void createPptHistory(Long studentUserId, String exportTaskId) {

        PptArtifaceExportParam pptArtifaceExportParam = new PptArtifaceExportParam().setStudentUserId(studentUserId).setExportTaskId(exportTaskId);
        // 构建延迟消息
        Message<PptArtifaceExportParam> message = MessageBuilder.withPayload(pptArtifaceExportParam).build();

        rocketMQTemplate.syncSend(PptArtifaceExportParam.TOPIC, message, 3000, 6);
    }

}
