package cn.iocoder.teach-ai.module.clientChat.service.ppt;

import cn.iocoder.teach-ai.module.clientChat.api.ppt.dto.*;
import cn.iocoder.teach-ai.module.clientChat.mq.ppt.producer.PptHistoryCreateProducer;
import cn.iocoder.teach-ai.module.clientChat.utils.ALiyunClientHelper;
import static cn.iocoder.teach-ai.framework.common.exception.util.ServiceExceptionUtil.exception;
import com.alibaba.fastjson.JSON;
import com.aliyun.sdk.service.aimiaobi20230801.AsyncClient;
import com.aliyun.sdk.service.aimiaobi20230801.models.*;
import com.google.common.collect.ImmutableMap;
import darabonba.core.ResponseIterator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static cn.iocoder.teach-ai.module.clientChat.enums.ErrorCodeConstants.PPT_EXPORT_EXCEPTION;
import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

@Slf4j
@Service
public class PptGenerationServicePublicImpl implements PptGenerationService {

    @Value("${alibaba-ppt.workspace-id}")
    private String workspaceId;


    @Resource
    private PptHistoryCreateProducer pptHistoryCreateProducer;

    @Override
    public SseEmitter runPptOutlineGeneration(String query) {

        log.info("开始生成ppt大纲，workspaceId:{}",workspaceId);

        AsyncClient client = ALiyunClientHelper.getClient();
        RunPptOutlineGenerationRequest runRequest = RunPptOutlineGenerationRequest.builder()
                .workspaceId(workspaceId)
                .prompt(query)
                .build();

        SseEmitter emitter = new SseEmitter(300000L);

        CompletableFuture.runAsync(() -> {
            ResponseIterator<RunPptOutlineGenerationResponseBody> iterator = client.runPptOutlineGenerationWithResponseIterable(runRequest).iterator();
            while (iterator.hasNext()) {

                try {

                    RunPptOutlineGenerationResponseBody eventData = iterator.next();
                    System.out.println(new Date() + " === " + JSON.toJSONString(eventData));

                    String eventName = eventData.getHeader().getEvent();
                    String taskId = eventData.getHeader().getTaskId();
                    String text = eventData.getPayload().getOutput().getText();

                    Map<String, String> data = ImmutableMap.of("taskId", taskId, "text", text);

                    emitter.send(event().name(eventName).data(data));

                } catch (Exception e) {
                    log.error("createTask error", e);
                    e.printStackTrace();
                    emitter.completeWithError(e);
                }
            }

            emitter.complete();
        });

        return emitter;
    }

    @Override
    public InitiatePptCreationResp initiatePptCreation(String taskId, String outline) {
        AsyncClient client = ALiyunClientHelper.getClient();
        InitiatePptCreationRequest request = InitiatePptCreationRequest.builder()
                .workspaceId(workspaceId)
                .taskId(taskId)
                .outline(outline)
                .build();
        CompletableFuture<InitiatePptCreationResponse> future = client.initiatePptCreation(request);
        try {
            InitiatePptCreationResponse response = future.get();
            System.out.println("result: " + JSON.toJSONString(response));
            return InitiatePptCreationResp.builder().appKey(response.getBody().getData().getAppKey()).secret(response.getBody().getData().getCode()).build();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BindPptArtifactResp bindPptArtifact(String taskId, Integer artifactId) {
        AsyncClient client = ALiyunClientHelper.getClient();
        BindPptArtifactRequest request = BindPptArtifactRequest.builder()
                .workspaceId(workspaceId)
                .taskId(taskId)
                .artifactId(artifactId)
                .build();
        CompletableFuture<BindPptArtifactResponse> future = client.bindPptArtifact(request);
        try {
            BindPptArtifactResponse response = future.get();
            System.out.println("result: " + JSON.toJSONString(response));
            return BindPptArtifactResp.builder().taskId(response.getBody().getData().getTaskId()).build();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetPptConfigResp getPptConfig(String taskId, String outline) {
        AsyncClient client = ALiyunClientHelper.getClient();
        GetPptConfigRequest request = GetPptConfigRequest.builder()
                .workspaceId(workspaceId)
                .build();
        CompletableFuture<GetPptConfigResponse> future = client.getPptConfig(request);
        try {
            GetPptConfigResponse response = future.get();
            System.out.println("result: " + JSON.toJSONString(response));
            return GetPptConfigResp.builder().appKey(response.getBody().getData().getAppKey()).code(response.getBody().getData().getCode()).build();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExportPptArtifactResp exportPptArtifact(Integer artifactId) {
        AsyncClient client = ALiyunClientHelper.getClient();
        ExportPptArtifactRequest request = ExportPptArtifactRequest.builder()
                .workspaceId(workspaceId)
                .pptArtifactId(Long.valueOf(artifactId))
                .build();
        CompletableFuture<ExportPptArtifactResponse> future = client.exportPptArtifact(request);
        try {
            ExportPptArtifactResponse response = future.get();
            System.out.println("result: " + JSON.toJSONString(response));
            return ExportPptArtifactResp.builder().exportTaskId(response.getBody().getData().getExportTaskId()).build();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean getPptArtifactExportResult(Long clientUserId, String exportTaskId) {
        try {
            log.info("开始创建ppt生成记录：任务ID： {}，学生id： {}", exportTaskId,clientUserId);
            pptHistoryCreateProducer.createPptHistory(clientUserId, exportTaskId);
            return true;
        } catch (Exception e) {
            log.error("创建ppt生成记录异常：{}", e.getMessage());
            throw exception(PPT_EXPORT_EXCEPTION);
        }
    }
}
