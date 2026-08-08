package cn.iocoder.teach-ai.module.clientChat.framework.mongo.config;

import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
public class MongoIndexConfiguration {

    @Resource
    private MongoTemplate mongoTemplate;

    @Bean
    public CommandLineRunner createMongoIndexes() {
        return args -> {
            try {
                // 1. 获取 ChatMemoryDO 对应的索引操作对象
                IndexOperations indexOps = mongoTemplate.indexOps(ChatMemoryDO.class);

                // 2. 定义要创建的索引
                // 2.1 按 createTime 降序索引
                Index createTimeIndex = new Index().on("createTime", Sort.Direction.DESC)
                        .named("idx_chat_memory_create_time");

                // 2.2 按 userId 索引（用于查询用户的所有会话）
                Index userIdIndex = new Index().on("userId", Sort.Direction.ASC)
                        .named("idx_chat_memory_user_id");

                // 3. 检查索引是否已存在，不存在则创建
                if (!isIndexExists(indexOps, "idx_chat_memory_create_time")) {
                    indexOps.createIndex(createTimeIndex);
                    log.info("MongoDB 索引创建成功: idx_chat_memory_create_time");
                } else {
                    log.debug("MongoDB 索引已存在: idx_chat_memory_create_time");
                }

                if (!isIndexExists(indexOps, "idx_chat_memory_user_id")) {
                    indexOps.createIndex(userIdIndex);
                    log.info("MongoDB 索引创建成功: idx_chat_memory_user_id");
                } else {
                    log.debug("MongoDB 索引已存在: idx_chat_memory_user_id");
                }
            } catch (Exception e) {
                log.error("MongoDB 索引创建失败", e);
            }
        };
    }

    /**
     * 辅助方法：检查指定名称的索引是否存在
     */
    private boolean isIndexExists(IndexOperations indexOps, String indexName) {
        // 获取所有已存在的索引信息
        List<IndexInfo> indexInfoList = indexOps.getIndexInfo();
        // 遍历检查索引名是否匹配
        Optional<IndexInfo> indexInfo = indexInfoList.stream()
                .filter(info -> indexName.equals(info.getName()))
                .findFirst();
        return indexInfo.isPresent();
    }

}
