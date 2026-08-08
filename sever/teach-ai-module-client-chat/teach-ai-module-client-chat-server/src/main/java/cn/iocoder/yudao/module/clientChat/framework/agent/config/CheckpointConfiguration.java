package cn.iocoder.teach-ai.module.clientChat.framework.agent.config;

import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import cn.iocoder.teach-ai.module.clientChat.repository.checkpointsaver.RedisCheckpointSaver;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.serializer.StateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class CheckpointConfiguration {

    @Bean
    public StateSerializer<State> stateSerializer() {
        return State.serializer();
    }

    @Bean
    public RedisCheckpointSaver redisCheckpointSaver(StringRedisTemplate stringRedisTemplate,
                                                     StateSerializer<State> stateSerializer) {
        return new RedisCheckpointSaver(stringRedisTemplate, stateSerializer);
    }

    @Bean
    public CompileConfig compileConfig(RedisCheckpointSaver redisCheckpointSaver) {
        return CompileConfig.builder()
                .checkpointSaver(redisCheckpointSaver)
                .recursionLimit(50)
                .build();
    }

}
