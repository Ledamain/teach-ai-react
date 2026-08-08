package cn.iocoder.teach-ai.module.clientSystem.framework.rpc.config;

import cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.DigitalVideoApi;
import cn.iocoder.teach-ai.module.clientChat.api.exercises.ExercisesApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.hotquery.HotQueryApi;
import cn.iocoder.teach-ai.module.clientChat.api.image.ImageApi;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.PptApi;
import cn.iocoder.teach-ai.module.clientChat.api.video.VideoApi;
import cn.iocoder.teach-ai.module.clientChat.api.wordCloud.WordCloudApi;
import cn.iocoder.teach-ai.module.infra.api.config.ConfigApi;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {ChatHistoryApi.class, WordCloudApi.class, FileIngestionApi.class, PptApi.class, ExercisesApi.class, HotQueryApi.class, ImageApi.class, VideoApi.class, DigitalVideoApi.class, FileApi.class, ConfigApi.class, FileIngestionApi.class})
public class RpcConfiguration {
}
