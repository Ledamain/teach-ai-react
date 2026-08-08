-- ========== 学习路径主表 ==========
CREATE TABLE IF NOT EXISTS `client_learning_path` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL COMMENT '学生用户ID',
    `repo_category_id` bigint NOT NULL COMMENT '学科ID',
    `repo_category_name` varchar(100) DEFAULT NULL COMMENT '学科名称(冗余)',
    `title` varchar(200) DEFAULT NULL COMMENT '路径标题',
    `description` varchar(500) DEFAULT NULL COMMENT '路径描述',
    `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT 'active/completed/archived',
    `total_nodes` int DEFAULT 0 COMMENT '总节点数',
    `completed_nodes` int DEFAULT 0 COMMENT '已完成节点数',
    `generated_at` datetime DEFAULT NULL COMMENT '生成时间',
    `creator` varchar(64) DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater` varchar(64) DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` tinyint NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_category` (`user_id`, `repo_category_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路径主表';

-- ========== 学习路径节点表 ==========
CREATE TABLE IF NOT EXISTS `client_learning_path_node` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `path_id` bigint NOT NULL COMMENT '所属路径ID',
    `order_index` int NOT NULL COMMENT '节点序号(从1开始)',
    `title` varchar(200) DEFAULT NULL COMMENT '节点标题',
    `description` varchar(500) DEFAULT NULL COMMENT '节点描述',
    `resource_type` varchar(20) DEFAULT NULL COMMENT '资源类型: doc/video/exercise/ppt/reading',
    `resource_id` bigint DEFAULT NULL COMMENT '关联资源ID',
    `resource_name` varchar(200) DEFAULT NULL COMMENT '资源名称',
    `depends_on` bigint DEFAULT NULL COMMENT '前置节点ID',
    `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT 'pending/in_progress/completed',
    `estimated_minutes` int DEFAULT NULL COMMENT '预计学习时长(分钟)',
    `creator` varchar(64) DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updater` varchar(64) DEFAULT NULL,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` tinyint NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_path_id` (`path_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路径节点表';
