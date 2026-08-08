package cn.iocoder.teach-ai.module.clientChat.mq.ppt.consumer;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.teach-ai.framework.common.util.file.FileUtil;
import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.GetPptArtifactExportResultResp;
import cn.iocoder.teach-ai.module.clientChat.mq.ppt.producer.bo.PptArtifaceExportParam;
import cn.iocoder.teach-ai.module.clientChat.utils.ALiyunClientHelper;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import cn.iocoder.teach-ai.module.infra.api.file.FileApi;
import com.alibaba.fastjson.JSON;
import com.aliyun.sdk.service.aimiaobi20230801.AsyncClient;
import com.aliyun.sdk.service.aimiaobi20230801.models.GetPptArtifactExportResultRequest;
import com.aliyun.sdk.service.aimiaobi20230801.models.GetPptArtifactExportResultResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RocketMQMessageListener( // 重点：添加 @RocketMQMessageListener 注解，声明消费的 topic
        topic = PptArtifaceExportParam.TOPIC,
        consumerGroup = PptArtifaceExportParam.TOPIC + "_CONSUMER"
)
@Slf4j
public class RepoDeleteConsumer implements RocketMQListener<PptArtifaceExportParam> {

    @Resource
    private PptHistoryApi pptHistoryApi;

    @Value("${alibaba-ppt.workspace-id}")
    private String workspaceId;

    @Resource
    private FileApi fileApi;

    @Override
    public void onMessage(PptArtifaceExportParam pptArtifaceExportParam) {
        log.info("开始创建", pptArtifaceExportParam);

        String exportTaskId = pptArtifaceExportParam.getExportTaskId();

        log.info("ppt生成id：{}",exportTaskId);
        AsyncClient client = ALiyunClientHelper.getClient();
        GetPptArtifactExportResultRequest request = GetPptArtifactExportResultRequest.builder()
                .workspaceId(workspaceId)
                .exportTaskId(exportTaskId)
                .build();
        CompletableFuture<GetPptArtifactExportResultResponse> future = client.getPptArtifactExportResult(request);
        try {
            GetPptArtifactExportResultResponse response = future.get();
            System.out.println("result: " + JSON.toJSONString(response));
            GetPptArtifactExportResultResp build = GetPptArtifactExportResultResp.builder().exportFileLink(response.getBody().getData().getExportFileLink()).build();

            try {
                // 存储生成的ppt
                for (String pptUrl : build.getExportFileLink()) {
                    MultipartFile file = FileUtil.urlToMultipartFile(pptUrl);
                    byte[] content = IoUtil.readBytes(file.getInputStream());
                    String fileName = file.getOriginalFilename();
                    String fileUrl = fileApi.createFile(content, fileName);

                    PptHistoryDTO pptHistoryDTO = new PptHistoryDTO().setPptTitle(fileName).setPptFile(fileUrl).setClientUserId(pptArtifaceExportParam.getStudentUserId()).setPptFiletype("0");
                    pptHistoryApi.createPptHistory(pptHistoryDTO);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }}
}
