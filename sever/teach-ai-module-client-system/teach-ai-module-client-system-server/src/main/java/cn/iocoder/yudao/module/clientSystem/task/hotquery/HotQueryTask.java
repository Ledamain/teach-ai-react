package cn.iocoder.teach-ai.module.clientSystem.task.hotquery;

import cn.iocoder.teach-ai.module.clientChat.api.hotquery.HotQueryApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HotQueryTask {

    @Resource
    private HotQueryApi hotQueryApi;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 每小时执行一次（0分0秒执行，比如 1:00, 2:00, 3:00...）
    @Scheduled(cron = "0 0 * * * ?")
    public void doEveryHour() {
        log.info("每小时执行一次：" + System.currentTimeMillis());
        if (redisTemplate.opsForValue().get("hot_query") ==  null) {
            String checkedData = hotQueryApi.getHotQuery().getCheckedData();
            redisTemplate.opsForValue().set("hot_query", checkedData, 2, TimeUnit.HOURS);
        }
    }
}
