package cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer;

import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.bo.FileIngestionBO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.KNOWLEDGE_SEGMENTATION_FAILURE;

@Slf4j
@Component
public class RepoIngestionProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void fileIngest(String kId, String fileUrl) {
        FileIngestionBO fileIngestionBO = new FileIngestionBO().setKId(kId).setFileUrl(fileUrl);
        rocketMQTemplate.syncSend(FileIngestionBO.TOPIC, fileIngestionBO);
    }

}
