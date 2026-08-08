package cn.iocoder.teach-ai.module.infra.framework.rpc.config;

import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.DigitalVideoApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "infraRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileIngestionApi.class, DigitalVideoApi.class})
public class RpcConfiguration {
}
