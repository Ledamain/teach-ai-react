package cn.iocoder.teach-ai.module.clientSystem.mq.repo.producer;

import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDeleteDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RepoDeleteProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void fileDelete(String id, String fileName) {
        RepoDeleteDTO repoDeleteDTO = new RepoDeleteDTO().setId(id).setFileName(fileName);
        rocketMQTemplate.syncSend(RepoDeleteDTO.TOPIC, repoDeleteDTO);
    }

}
