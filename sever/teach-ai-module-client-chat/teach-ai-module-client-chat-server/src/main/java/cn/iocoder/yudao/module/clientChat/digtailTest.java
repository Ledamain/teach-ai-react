package cn.iocoder.teach-ai.module.clientChat;

import com.alibaba.dashscope.audio.omni.*;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.gson.JsonObject;
import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class digtailTest {
    static class SequentialAudioPlayer {
        private final SourceDataLine line;
        private final Queue<byte[]> audioQueue = new ConcurrentLinkedQueue<>();
        private final Thread playerThread;
        private final AtomicBoolean shouldStop = new AtomicBoolean(false);

        public SequentialAudioPlayer() throws LineUnavailableException {
            AudioFormat format = new AudioFormat(23000, 16, 1, true, false);
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            playerThread = new Thread(() -> {
                while (!shouldStop.get()) {
                    byte[] audio = audioQueue.poll();
                    if (audio != null) {
                        line.write(audio, 0, audio.length);
                    } else {
                        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                    }
                }
            }, "AudioPlayer");
            playerThread.start();
        }

        public void play(String base64Audio) {
            audioQueue.add(Base64.getDecoder().decode(base64Audio));
        }
        public void close() {
            shouldStop.set(true);
            try { playerThread.join(1000); } catch (InterruptedException ignored) {}
            line.drain();
            line.close();
        }
    }

    public static void main(String[] args) {
        try {
            SequentialAudioPlayer player = new SequentialAudioPlayer();
            AtomicBoolean shouldStop = new AtomicBoolean(false);

            OmniRealtimeParam param = OmniRealtimeParam.builder()
                    .model("qwen3.5-omni-plus-realtime")
                    .apikey("sk-a47d641f6a2141c0ba4cde9cd1c5b3eb")
                    .url("wss://dashscope.aliyuncs.com/api-ws/v1/realtime")
                    .build();

            OmniRealtimeConversation conversation = new OmniRealtimeConversation(param, new OmniRealtimeCallback() {
                @Override public void onOpen() {
                    System.out.println("连接已建立");
                }
                @Override public void onClose(int code, String reason) {
                    System.out.println("连接已关闭");
                    shouldStop.set(true);
                }
                @Override public void onEvent(JsonObject event) {
                    String type = event.get("type").getAsString();
                    if ("response.audio.delta".equals(type)) {
                        player.play(event.get("delta").getAsString());
                    } else if ("response.audio_transcript.done".equals(type)) {
                        System.out.println("[LLM] " + event.get("transcript").getAsString());
                    } else if ("response.done".equals(type)) {
                        JsonObject response = event.getAsJsonObject("response");
                        if (response != null && response.has("usage")) {
                            JsonObject usage = response.getAsJsonObject("usage");
                            if (usage.has("plugins")) {
                                JsonObject plugins = usage.getAsJsonObject("plugins");
                                if (plugins.has("search")) {
                                    JsonObject search = plugins.getAsJsonObject("search");
                                    System.out.println("[Search] count=" + search.get("count").getAsInt()
                                            + ", strategy=" + search.get("strategy").getAsString());
                                }
                            }
                        }
                    }
                }
            });

            conversation.connect();
            conversation.updateSession(OmniRealtimeConfig.builder()
                    .modalities(Arrays.asList(OmniRealtimeModality.AUDIO, OmniRealtimeModality.TEXT))
                    .voice("Raymond")
                    .enableTurnDetection(true)
                    .enableInputAudioTranscription(true)
                    .parameters(Map.of(
                            "instructions", "身份：计算机专业专属数字人讲师\\n\" +\n" +
                                    "                        \"人设：专业严谨、接地气讲解，兼顾理论与实操，适配院校专业课、技能实训教学\\n\" +\n" +
                                    "                        \"能力：讲解编程代码、计算机原理、网络架构、数据库、系统开发、软硬件运维、算法逻辑等内容，解析习题、排查程序问题、梳理专业考点\\n\" +\n" +
                                    "                        \"话术规范：\\n\" +\n" +
                                    "                        \"1.开场规范问好，课堂语气专业亲和\\n\" +\n" +
                                    "                        \"2.重难点着重剖析，搭配实例辅助理解\\n\" +\n" +
                                    "                        \"3.解答疑问点明问题根源，给出可行解决思路\\n\" +\n" +
                                    "                        \"4.课程结尾归纳核心技术要点，分享实用学习技巧\\n\" +\n" +
                                    "                        \"5.语句自然贴合真人讲课节奏，适配数字人实时播报\",",
                            "enable_search", true,
                            "search_options", Map.of("enable_source", true)
                    ))
                    .build()
            );

            System.out.println("联网搜索已启用，请开始说话（按Ctrl+C退出）...");
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            TargetDataLine mic = AudioSystem.getTargetDataLine(format);
            mic.open(format);
            mic.start();

            ByteBuffer buffer = ByteBuffer.allocate(3200);
            while (!shouldStop.get()) {
                int bytesRead = mic.read(buffer.array(), 0, buffer.capacity());
                if (bytesRead > 0) {
                    conversation.appendAudio(Base64.getEncoder().encodeToString(buffer.array()));
                }
                Thread.sleep(20);
            }

            conversation.close(1000, "正常结束");
            player.close();
            mic.close();
        } catch (NoApiKeyException e) {
            System.err.println("未找到API KEY: 请设置环境变量 DASHSCOPE_API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
