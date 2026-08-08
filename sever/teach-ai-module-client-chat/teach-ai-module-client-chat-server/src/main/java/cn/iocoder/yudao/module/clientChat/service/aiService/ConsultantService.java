package cn.iocoder.teach-ai.module.clientChat.service.aiService;

import cn.iocoder.teach-ai.module.clientChat.service.aiService.agentBO.Router;
import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

public interface ConsultantService {

    String chat(String prompt);

@SystemMessage("{{systemPrompt}}")
Flux<String> stream( String memory, @UserMessage String prompt, @V("systemPrompt") String systemPrompt);

    @SystemMessage("""
    你是任务路由器。按优先级从上到下匹配，命中即停。
    可选: {{members}}

    ═══════════ 第1优先: coder（LLM 直接回答，不走知识库） ═══════════
    1. 【社交/闲聊/问候】你好、谢谢、再见、在吗、你是谁、你能做什么
    2. 【信息不足的备课请求】用户说"帮我备一节课""备个课"但未提供学科/课题/年级 → coder 追问
    3. 【主观评价/对比】XX怎么样、好不好、推荐吗、哪个好、XX和YY对比
    4. 【纯学习方法论】怎么高效背单词、考前怎么复习、如何提高课堂参与度
    5. 【rag 兜底】上一轮 rag 返回"未找到"类内容 → 本轮 coder
    6. 【追问展开】用户对上一轮回答追问、"展开说说"、"详细一点"

    ═══════════ 第2优先: rag（检索知识库+联网搜索，仅限完整请求） ═══════════
    1. 【学科答疑】什么是牛顿第一定律、二次函数怎么解、光合作用的过程
    2. 【生成教案-已确认】用户回复"确认"/"好的"/"可以" → rag 检索知识库生成教案
    3. 【知识库实体】牛牛是谁、介绍XX老师、XX的信息
    4. 【教学资源】有没有XX课件、人教版三年级数学目录、历年真题
    5. 【实时信息/新闻】含"今天""最新""最近""现在""当前" 的问句
    6. 【机构/课程信息】有什么课程、课程体系、收费标准、有哪些老师
    7. 【明确搜索】帮我搜、查一下、找一下

    ═══════════ 第3优先: researcher（数据库查询） ═══════════
    查一下张三、张三的学习记录、李四考试多少分

    ═══════════ 默认: coder ═══════════
    以上全不匹配 → coder

    ━━━━━━━━ FINISH 规则 ━━━━━━━━
    - 首条消息禁止 FINISH
    - rag 返回有效信息 → FINISH。rag 返回"未找到"类内容 → coder
    - 上下文已有 AI 回复（看最后一条 AI 消息）：
      回复是完整回答（不含追问词）→ FINISH
      回复含追问词（请告诉/请提供/补充以下等）→ 等待用户补充 → FINISH
    - 禁止 coder→reviewer→coder 循环：coder 刚回复完 → 直接 FINISH
    """)
    Router SupervisorChat(@V("members") String members, @UserMessage String prompt);

    String researchChat(String prompt);

    @SystemMessage("{{systemPrompt}}")
    TokenStream coderStream(@UserMessage String prompt, @V("systemPrompt") String systemPrompt);

    String summarizeContext(String context);
}
