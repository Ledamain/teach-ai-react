-- ========== 学生画像表 ==========
-- 支持 ≥7 维度的动态学生画像，随学随新

CREATE TABLE IF NOT EXISTS `client_student_profile` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '学生用户ID',
    
    -- 维度1: 知识基础
    `knowledge_level` varchar(32) DEFAULT NULL COMMENT '知识水平评级(novice/intermediate/advanced/expert)',
    `knowledge_summary` varchar(500) DEFAULT NULL COMMENT '知识基础概述',
    `mastered_tags` text COMMENT '已掌握知识点标签 JSON数组',
    
    -- 维度2: 认知风格
    `cognitive_style` varchar(32) DEFAULT NULL COMMENT '认知风格(field_dependent/field_independent/mixed)',
    `cognitive_style_desc` varchar(500) DEFAULT NULL COMMENT '认知风格描述',
    
    -- 维度3: 学习风格
    `learning_style` varchar(32) DEFAULT NULL COMMENT '学习风格(visual/auditory/kinesthetic/reading_writing/mixed)',
    `learning_style_desc` varchar(500) DEFAULT NULL COMMENT '学习风格描述',
    
    -- 维度4: 易错点偏好
    `error_preference_summary` varchar(500) DEFAULT NULL COMMENT '易错偏好概述',
    `error_tags` text COMMENT '易错类型标签 JSON数组',
    
    -- 维度5: 注意力特征
    `attention_level` varchar(16) DEFAULT NULL COMMENT '注意力水平(high/medium/low)',
    `best_study_time` varchar(50) DEFAULT NULL COMMENT '最佳学习时段',
    `attention_span_minutes` int DEFAULT NULL COMMENT '单次专注时长(分钟)',
    
    -- 维度6: 学习节奏
    `learning_pace` varchar(16) DEFAULT NULL COMMENT '学习节奏(fast/medium/slow)',
    `weekly_study_minutes` int DEFAULT NULL COMMENT '周均学习时长(分钟)',
    `preferred_session_minutes` int DEFAULT NULL COMMENT '偏好每次学习时长(分钟)',
    
    -- 维度7: 兴趣方向
    `interest_tags` text COMMENT '兴趣标签 JSON数组',
    `interest_summary` varchar(500) DEFAULT NULL COMMENT '兴趣方向描述',
    
    -- 维度8: 薄弱知识点
    `weak_point_tags` text COMMENT '薄弱知识点标签 JSON数组',
    `weak_point_detail` text COMMENT '薄弱点详细描述',
    
    -- 元信息
    `profile_version` int NOT NULL DEFAULT 1 COMMENT '画像版本号',
    `conversation_count` int NOT NULL DEFAULT 0 COMMENT '已分析的对话总数',
    `last_extract_time` datetime DEFAULT NULL COMMENT '最近一次画像提取时间',
    
    `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生画像表';


-- ========== 学生画像历史快照表 ==========
-- 每次画像更新时保存快照，用于展示演变趋势

CREATE TABLE IF NOT EXISTS `client_student_profile_history` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '学生用户ID',
    `profile_version` int NOT NULL COMMENT '画像版本号',
    `memory_id` varchar(128) DEFAULT NULL COMMENT '触发更新的对话ID',
    `snapshot_json` longtext COMMENT '画像快照JSON(完整StudentProfileDO字段)',
    `change_summary` varchar(500) DEFAULT NULL COMMENT '本次变化摘要',
    
    `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id_version` (`user_id`, `profile_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生画像历史快照表';
