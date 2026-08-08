package cn.iocoder.teach-ai.module.clientChat.repository;


import cn.iocoder.teach-ai.module.clientChat.dal.dataobject.chat.ChatMemoryDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话记忆 Repository（对标 MyBatis-Plus 的 BaseMapper）
 */
@Repository
public interface ChatMemoryRepository extends MongoRepository<ChatMemoryDO, String> {

    // ========== 基础 CRUD 已自动继承，无需手写 ==========
    // findById(String memoryId)      // 按ID查询
    // save(ChatMemoryDO entity)      // 新增/更新（Upsert）
    // deleteById(String memoryId)    // 按ID删除
    // findAll()                      // 查询全部
    // count()                        // 统计总数

    // ========== 自定义方法名推导查询（零手写） ==========
    /**
     * 查询指定时间之后创建的会话记忆
     */
    List<ChatMemoryDO> findByCreateTimeAfter(LocalDateTime createTime);

    /**
     * 查询指定时间之后创建的会话记忆（分页）
     */
    Page<ChatMemoryDO> findByCreateTimeAfter(LocalDateTime createTime, Pageable pageable);

    /**
     * 分页查询指定时间范围内的会话记忆
     */
    Page<ChatMemoryDO> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);


    // ========== 新增：模糊匹配 ID（以指定前缀开头） ==========
    /**
     * 查询 ID 以指定前缀开头的会话记忆（类比 MySQL 的 LIKE '222%'）
     * @param prefix ID 前缀（如 "222"）
     * @return 匹配的会话记忆列表
     */
    List<ChatMemoryDO> findByMemoryIdStartingWith(String prefix);

    /**
     * 分页查询 ID 以指定前缀开头的会话记忆
     * @param prefix ID 前缀（如 "222"）
     * @param pageable 分页参数（页码、每页条数）
     * @return 分页结果
     */
    Page<ChatMemoryDO> findByMemoryIdStartingWith(String prefix, Pageable pageable);
}
