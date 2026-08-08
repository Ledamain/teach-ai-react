package cn.iocoder.teach-ai.module.clientChat.repository.store;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        //根据会话id取出对应json字符串
        String json = stringRedisTemplate.opsForValue().get(memoryId.toString());
        //将json反序列化为list
        List<ChatMessage> chatMessages = ChatMessageDeserializer.messagesFromJson(json);
        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {

        //将list转化为json
        String json = ChatMessageSerializer.messagesToJson(list);
        //将list存储到redis，并设置一天过期
        stringRedisTemplate.opsForValue().set(memoryId.toString(), json, Duration.ofDays(1));

    }

    @Override
    public void deleteMessages(Object memoryId) {

        stringRedisTemplate.delete(memoryId.toString());

    }
}
