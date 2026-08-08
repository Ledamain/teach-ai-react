package cn.iocoder.teach-ai.module.clientChat.framework.rpc.config;

import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientSystem.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.conversion.ConversionApi;
import cn.iocoder.teach-ai.module.clientSystem.api.countrecord.CountRecordApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.profile.StudentProfileApi;
import cn.iocoder.teach-ai.module.clientSystem.api.learningpath.LearningPathApi;
import cn.iocoder.teach-ai.module.clientSystem.api.systemmessage.SystemMessageApi;
import cn.iocoder.teach-ai.module.infra.api.config.ConfigApi;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {ChatHistoryApi.class, ConversionApi.class, CountRecordApi.class, SystemMessageApi.class, FileApi.class, PptHistoryApi.class, ConfigApi.class, StudentProfileApi.class, LearningPathApi.class})
public class RpcConfiguration {
}
