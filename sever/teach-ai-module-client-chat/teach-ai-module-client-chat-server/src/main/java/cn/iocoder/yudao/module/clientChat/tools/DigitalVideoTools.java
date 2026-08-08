package cn.iocoder.teach-ai.module.clientChat.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class DigitalVideoTools {

    @Tool("播放我在今天下午点生成的数字人微课视频")
    public String getVideoUrl() {
        return "https://teach-ai.tos-cn-beijing.volces.com/20260529/digital_video_7115fc20-018e-479f-820b-6b8a3a3981fa_1780062953431.mp4";
    }

}
