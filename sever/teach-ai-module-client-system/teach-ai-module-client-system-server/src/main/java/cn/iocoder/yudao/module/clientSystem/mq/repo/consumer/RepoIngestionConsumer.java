package cn.iocoder.teach-ai.module.clientSystem.mq.repo.consumer;

import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer.bo.FileIngestionBO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.teach-ai.module.clientSystem.enums.admin.ErrorCodeConstants.KNOWLEDGE_SEGMENTATION_FAILURE;

@Component
@RocketMQMessageListener( // 重点：添加 @RocketMQMessageListener 注解，声明消费的 topic
        topic = FileIngestionBO.TOPIC,
        consumerGroup = FileIngestionBO.TOPIC + "_CONSUMER"
)
@Slf4j
public class RepoIngestionConsumer implements RocketMQListener<FileIngestionBO> {

    @Resource
    private FileIngestionApi fileIngestionApi;

    @Override
    public void onMessage(FileIngestionBO fileIngestionBO) {
        log.info("开始切分", fileIngestionBO);
        try {
            MultipartFile multipartFile = FileUtil.urlToMultipartFile(fileIngestionBO.getFileUrl());
            FileIngestionDTO fileIngestionDTO = new FileIngestionDTO().setFile(multipartFile).setKId(fileIngestionBO.getKId()).setFileUrl(fileIngestionBO.getFileUrl());
            fileIngestionApi.fileIngest(fileIngestionDTO);

        } catch (Exception e) {
            log.info("知识库切分失败，原因：{}", e.getMessage());
            throw exception(KNOWLEDGE_SEGMENTATION_FAILURE);
        }
    }
}
