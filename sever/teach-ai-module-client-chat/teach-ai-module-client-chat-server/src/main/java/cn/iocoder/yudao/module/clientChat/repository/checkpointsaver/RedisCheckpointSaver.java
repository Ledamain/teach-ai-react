package cn.iocoder.teach-ai.module.clientChat.repository.checkpointsaver;

import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.checkpoint.AbstractCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.Checkpoint;
import org.bsc.langgraph4j.serializer.StateSerializer;
import org.bsc.langgraph4j.serializer.std.CheckpointListSerializer;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Base64;
import java.util.LinkedList;

public class RedisCheckpointSaver extends AbstractCheckpointSaver {

    private static final String KEY_PREFIX = "lg4j:checkpoint:";

    private final StringRedisTemplate stringRedisTemplate;
    private final CheckpointListSerializer serializer;

    public RedisCheckpointSaver(StringRedisTemplate stringRedisTemplate,
                                StateSerializer<State> stateSerializer) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.serializer = new CheckpointListSerializer(stateSerializer);
    }

    @Override
    protected LinkedList<Checkpoint> loadCheckpoints(RunnableConfig runnableConfig) throws Exception {
        String key = KEY_PREFIX + threadId(runnableConfig);
        String base64 = stringRedisTemplate.opsForValue().get(key);
        if (base64 == null) {
            return new LinkedList<>();
        }
        byte[] bytes = Base64.getDecoder().decode(base64);
        return serializer.bytesToObject(bytes);
    }

    @Override
    protected void insertedCheckpoint(RunnableConfig runnableConfig,
                                       LinkedList<Checkpoint> list,
                                       Checkpoint checkpoint) throws Exception {
        list.add(checkpoint);
        saveList(runnableConfig, list);
    }

    @Override
    protected void updatedCheckpoint(RunnableConfig runnableConfig,
                                      LinkedList<Checkpoint> list,
                                      Checkpoint checkpoint) throws Exception {
        if (!list.isEmpty()) {
            list.removeLast();
        }
        list.add(checkpoint);
        saveList(runnableConfig, list);
    }

    @Override
    protected Tag releaseCheckpoints(RunnableConfig runnableConfig,
                                      LinkedList<Checkpoint> list) throws Exception {
        return null;
    }

    private void saveList(RunnableConfig config, LinkedList<Checkpoint> list) throws Exception {
        String key = KEY_PREFIX + threadId(config);
        byte[] bytes = serializer.objectToBytes(list);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        stringRedisTemplate.opsForValue().set(key, base64);
        stringRedisTemplate.expire(key, Duration.ofDays(7));
    }
}
