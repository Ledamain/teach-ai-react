package cn.iocoder.teach-ai.module.clientChat.framework.agent.subagent.rag;

import lombok.extern.slf4j.Slf4j;
import cn.iocoder.teach-ai.module.clientChat.framework.agent.State;
import org.springframework.stereotype.Service;

/**
 * RAG 子图边条件 — 判断生成答案还是回退。
 * 规则: 有文档 → generate; 无文档 → 直接 generate (GenerateAgent 会产"未找到")
 * 不再循环改写，由父图 supervisor 路由 coder 兜底。
 */
@Service
@Slf4j
public class DecideToGenerateAgent {

    public String apply(State state) {
        var docs = state.ragDocuments();
        if (docs.isEmpty() || docs.stream().allMatch(String::isBlank)) {
            log.debug("---无相关文档，直接走 generate → 父图 coder 兜底---");
        } else {
            log.debug("---有相关文档，进入生成---");
        }
        return "generate";
    }
}
