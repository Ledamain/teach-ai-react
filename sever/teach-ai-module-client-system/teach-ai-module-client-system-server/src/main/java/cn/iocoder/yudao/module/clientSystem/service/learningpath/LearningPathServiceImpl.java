package cn.iocoder.teach-ai.module.clientSystem.service.learningpath;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.exerciseinfo.ExerciseInfoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.learningpath.LearningPathNodeDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.repo.RepoDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.exerciseinfo.ExerciseInfoMapper;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningpath.LearningPathMapper;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.learningpath.LearningPathNodeMapper;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.repo.RepoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习路径服务实现（资源匹配增强版）。
 * <p>
 * 资源匹配策略从简单关键词升级为三层匹配：
 * 1. 语义关键词匹配（拆分中文词 + 扩展同义词）
 * 2. 资源类型精确匹配（文件扩展名 + 习题类型）
 * 3. 多资源推荐（每节点匹配多个候选资源，选最优）
 */
@Slf4j
@Service
public class LearningPathServiceImpl implements LearningPathService {

    @Resource
    private LearningPathMapper pathMapper;

    @Resource
    private LearningPathNodeMapper nodeMapper;

    @Resource
    private RepoMapper repoMapper;

    @Resource
    private ExerciseInfoMapper exerciseInfoMapper;

    // 学科术语同义词映射（用于扩展匹配）
    private static final Map<String, List<String>> SYNONYM_MAP = Map.of(
            "函数", List.of("映射", "关系式", "公式"),
            "方程", List.of("等式", "求解"),
            "几何", List.of("图形", "空间"),
            "概率", List.of("统计", "随机"),
            "力学", List.of("运动", "力"),
            "电学", List.of("电路", "电流", "电压"),
            "化学", List.of("反应", "元素", "分子"),
            "编程", List.of("代码", "算法", "程序"),
            "数据", List.of("信息", "统计", "分析"),
            "网络", List.of("协议", "通信", "互联")
    );

    @Override
    public LearningPathDO getById(Long id) {
        return pathMapper.selectById(id);
    }

    @Override
    public LearningPathDO getActiveByUserAndCategory(Long userId, Long repoCategoryId) {
        return pathMapper.selectByUserIdAndCategory(userId, repoCategoryId);
    }

    @Override
    public List<LearningPathDO> listByUserId(Long userId) {
        return pathMapper.selectByUserId(userId);
    }

    @Override
    public List<LearningPathDO> listActiveByUserId(Long userId) {
        return pathMapper.selectActiveByUserId(userId);
    }

    @Override
    public List<LearningPathNodeDO> getNodesByPathId(Long pathId) {
        return nodeMapper.selectByPathId(pathId);
    }

    @Override
    @Transactional
    public LearningPathDO createPath(Long userId, Long repoCategoryId, String repoCategoryName,
                                      String title, String description, List<LearningPathNodeDO> nodes) {
        LearningPathDO oldActive = pathMapper.selectByUserIdAndCategory(userId, repoCategoryId);
        if (oldActive != null) {
            oldActive.setStatus("archived");
            pathMapper.updateById(oldActive);
        }

        LearningPathDO path = LearningPathDO.builder()
                .userId(userId)
                .repoCategoryId(repoCategoryId)
                .repoCategoryName(repoCategoryName)
                .title(title)
                .description(description)
                .status("active")
                .totalNodes(nodes != null ? nodes.size() : 0)
                .completedNodes(0)
                .generatedAt(LocalDateTime.now())
                .build();
        pathMapper.insert(path);

        if (nodes != null) {
            List<RepoDO> repos = repoMapper.selectByCategoryId(repoCategoryId);
            List<ExerciseInfoDO> exercises = exerciseInfoMapper.selectByCategoryId(repoCategoryId);

            for (LearningPathNodeDO node : nodes) {
                node.setPathId(path.getId());
                if (node.getStatus() == null) node.setStatus("pending");
                matchResource(node, repos, exercises);
                nodeMapper.insert(node);
            }
        }

        log.info("创建学习路径: userId={}, category={}, pathId={}, nodes={}",
                userId, repoCategoryName, path.getId(), nodes != null ? nodes.size() : 0);
        return path;
    }

    /**
     * 三层资源匹配策略。
     * <p>
     * 第1层：精确关键词 + 同义词扩展匹配
     * 第2层：资源类型精确过滤
     * 第3层：综合评分（词匹配度 × 类型权重）选最优
     */
    private void matchResource(LearningPathNodeDO node, List<RepoDO> repos, List<ExerciseInfoDO> exercises) {
        String type = node.getResourceType();
        if (type == null) return;

        String nodeTitle = node.getTitle();
        if (nodeTitle == null) nodeTitle = "";

        // 扩展关键词：原始关键词 + 同义词
        List<String> keywords = extractKeywordsExtended(nodeTitle);

        switch (type) {
            case "doc":
            case "reading": {
                RepoDO bestDoc = findBestMatch(repos, keywords, "doc");
                if (bestDoc != null) {
                    node.setResourceId(bestDoc.getId());
                    node.setResourceName(bestDoc.getRepoTitle());
                }
                break;
            }
            case "video": {
                RepoDO bestVideo = findBestMatch(repos, keywords, "video");
                if (bestVideo != null) {
                    node.setResourceId(bestVideo.getId());
                    node.setResourceName(bestVideo.getRepoTitle());
                }
                break;
            }
            case "ppt": {
                RepoDO bestPpt = findBestMatch(repos, keywords, "ppt");
                if (bestPpt != null) {
                    node.setResourceId(bestPpt.getId());
                    node.setResourceName(bestPpt.getRepoTitle());
                }
                break;
            }
            case "exercise": {
                ExerciseInfoDO bestEx = null;
                int bestExScore = -1;
                for (ExerciseInfoDO ex : exercises) {
                    int score = computeMatchScore(ex.getExerciseName(), keywords);
                    if (score > bestExScore) {
                        bestExScore = score;
                        bestEx = ex;
                    }
                }
                if (bestEx != null) {
                    node.setResourceId(bestEx.getId());
                    node.setResourceName(bestEx.getExerciseName());
                }
                break;
            }
            default:
                break;
        }

        if (node.getResourceName() != null) {
            log.debug("资源匹配: 节点「{}」(type={}) → {}", nodeTitle, type, node.getResourceName());
        }
    }

    /** 提取中文关键词 + 同义词扩展 */
    private List<String> extractKeywordsExtended(String title) {
        if (title == null || title.isBlank()) return List.of();
        // 按分隔符拆分
        String[] parts = title.split("[，,、\\s与和及]+");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && trimmed.length() >= 2) {
                result.add(trimmed);
                // 查同义词
                for (var entry : SYNONYM_MAP.entrySet()) {
                    if (trimmed.contains(entry.getKey())) {
                        for (String syn : entry.getValue()) {
                            if (!result.contains(syn)) result.add(syn);
                        }
                    }
                }
                if (result.size() >= 8) break; // 扩展上限
            }
        }
        return result;
    }

    /** 计算资源名称与关键词的匹配分数（词覆盖度+长度加权） */
    private int computeMatchScore(String resourceName, List<String> keywords) {
        if (resourceName == null || keywords.isEmpty()) return 0;
        String lower = resourceName.toLowerCase();
        int score = 0;
        Set<String> matched = new HashSet<>();
        for (String kw : keywords) {
            if (lower.contains(kw.toLowerCase())) {
                if (matched.add(kw)) {
                    score += kw.length() * 10; // 长词匹配权重更高
                }
            }
        }
        // 独特词匹配加分
        return score + matched.size() * 5;
    }

    /**
     * 从资源列表中找到最佳匹配。
     * 优先按关键词评分 + 类型过滤，无匹配时 fallback 到首位同类型资源。
     */
    private RepoDO findBestMatch(List<RepoDO> repos, List<String> keywords, String typeFilter) {
        if (repos == null || repos.isEmpty()) return null;

        // 先按类型过滤
        List<RepoDO> typeMatched = repos.stream()
                .filter(r -> r.getRepoTitle() != null && isTypeMatch(r.getRepoTitle(), typeFilter))
                .collect(Collectors.toList());

        if (typeMatched.isEmpty()) return null;

        // 按关键词评分排序
        RepoDO best = null;
        int bestScore = -1;
        for (RepoDO repo : typeMatched) {
            int score = computeMatchScore(repo.getRepoTitle(), keywords);
            if (score > bestScore) {
                bestScore = score;
                best = repo;
            }
        }

        // 无关键词匹配时 fallback 到第一个同类型资源
        if (best == null && !typeMatched.isEmpty()) {
            best = typeMatched.get(0);
        }

        return best;
    }

    private static boolean isTypeMatch(String title, String typeFilter) {
        return switch (typeFilter) {
            case "video" -> title.endsWith(".mp4") || title.endsWith(".mov") || title.endsWith(".avi");
            case "ppt" -> title.endsWith(".ppt") || title.endsWith(".pptx");
            case "doc" -> !title.endsWith(".mp4") && !title.endsWith(".mov") && !title.endsWith(".avi")
                    && !title.endsWith(".ppt") && !title.endsWith(".pptx");
            default -> true;
        };
    }

    @Override
    public void updateNodeDependsOn(Long nodeId, Long dependsOn) {
        LearningPathNodeDO node = nodeMapper.selectById(nodeId);
        if (node == null) return;
        node.setDependsOn(dependsOn);
        nodeMapper.updateById(node);
    }

    @Override
    public void updateNodeStatus(Long nodeId, String status) {
        LearningPathNodeDO node = nodeMapper.selectById(nodeId);
        if (node == null) return;
        node.setStatus(status);
        nodeMapper.updateById(node);

        LearningPathDO path = pathMapper.selectById(node.getPathId());
        if (path != null) {
            List<LearningPathNodeDO> allNodes = nodeMapper.selectByPathId(path.getId());
            long completed = allNodes.stream().filter(n -> "completed".equals(n.getStatus())).count();
            path.setCompletedNodes((int) completed);
            if (completed == allNodes.size()) {
                path.setStatus("completed");
            }
            pathMapper.updateById(path);
        }
    }
}
