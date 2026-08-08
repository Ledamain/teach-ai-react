package cn.iocoder.teach-ai.module.clientChat.tools;

import cn.iocoder.teach-ai.module.clientChat.utils.ClientUserContext;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.PptHistoryApi;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileHistoryTools {

    @Resource
    private PptHistoryApi pptHistoryApi;

    @Tool("获取生成的教学资源")
    public List<PptHistoryDTO> getHistoryList() {
        String currentUserId = ClientUserContext.getCurrentUserId();
        return pptHistoryApi.getPptHistoryList(new PptHistoryDTO().setClientUserId(Long.valueOf(currentUserId))).getCheckedData();
    }

}
