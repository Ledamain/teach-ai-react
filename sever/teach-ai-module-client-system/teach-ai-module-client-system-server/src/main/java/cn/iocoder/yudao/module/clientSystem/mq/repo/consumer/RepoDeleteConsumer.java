package cn.iocoder.teach-ai.module.clientSystem.mq.repo.consumer;

import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDeleteDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener( // 重点：添加 @RocketMQMessageListener 注解，声明消费的 topic
        topic = RepoDeleteDTO.TOPIC,
        consumerGroup = RepoDeleteDTO.TOPIC + "_CONSUMER"
)
@Slf4j
public class RepoDeleteConsumer implements RocketMQListener<RepoDeleteDTO> {

    @Resource
    private FileIngestionApi fileIngestionApi;


    @Override
    public void onMessage(RepoDeleteDTO deleteDTO) {
        log.info("开始删除", deleteDTO);
        fileIngestionApi.deleteByKbId(deleteDTO.getId(),deleteDTO.getFileName());
    }
}
