package cn.iocoder.teach-ai.module.clientChat.framework.langchain4j.config;

import cn.iocoder.teach-ai.module.clientChat.framework.langchain4j.retriever.HybridContentRetriever;
import cn.iocoder.teach-ai.module.clientChat.framework.langchain4j.retriever.PptHybridContentRetriever;
import cn.iocoder.teach-ai.module.clientChat.repository.store.MongoChatMemoryStore;
import cn.iocoder.teach-ai.module.clientChat.repository.store.RedisChatMemoryStore;
import cn.iocoder.teach-ai.module.clientChat.service.aiService.ConsultantService;
import cn.iocoder.teach-ai.module.clientChat.tools.DigitalVideoTools;
import cn.iocoder.teach-ai.module.clientChat.tools.FileHistoryTools;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CommonConfiguration {


    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;  //注入会话记忆store（缓存）

    @Resource
    private MongoChatMemoryStore mongoChatMemoryStore; //（持久化）

    // 从配置文件读取 API Key 和 Base URL
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String openAiApiKey;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String openAiBaseUrl;

    @Value("${langchain4j.open-ai.embedding-model.model-name}")
    private String openAiModelName;

    @Value("${langchain4j.community.milvus.host}")
    private String host;
    @Value("${langchain4j.community.milvus.port}")
    private Integer port;
    @Value("${langchain4j.community.milvus.database-name}")
    private String databaseName;
    @Value("${langchain4j.community.milvus.collection-name}")
    private String collectionName;
    @Value("${langchain4j.community.milvus.username}")
    private String username;
    @Value("${langchain4j.community.milvus.password}")
    private String password;

    private MilvusServiceClient milvusServiceClient;

    // 流式对话主配置
    @Resource
    private StreamingChatModel openAiStreamingChatModel;

    @Resource
    private OpenAiChatModel openAiChatModel;

    // Tools
    @Resource
    private DigitalVideoTools digitalVideoTools;

    @Resource
    private FileHistoryTools fileHistoryTools;

//    // skill的toolprovider
//    @Qualifier("getSkillToolProviderTest")
//    private ToolProvider getSkillToolProviderTest;



    //配置会话记忆
    @Bean
    public ChatMemory chatMemory() {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        return chatMemory;
    }


//    @Bean
//    public OpenAiStreamingChatModel openAiStreamingChatModel() {
//        AuthropicStr.builder()
//                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
//                .modelName(CLAUDE_3_7_SONNET_20250219)
//                .thinkingType("enabled")
//                .thinkingBudgetTokens(1024)
//                .maxTokens(1024 + 100)
//                .returnThinking(true)
//                .sendThinking(true)
//                .build();
//    }

    //配置会话记忆提供者
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(mongoChatMemoryStore)  //配置会话记忆store
                        .build();
            }
        };
        return chatMemoryProvider;
    }

    // 【解决mac无法切割问题】创建 OpenAI Embedding 模型（向量模型）
    @Bean
    @Primary  //强调在Spring Bean容器中使用这个，而不是系统内部的
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .baseUrl(openAiBaseUrl)
                .maxSegmentsPerBatch(25)
                .modelName(openAiModelName)  // 性价比高的模型
                .build();
    }

    // 【解决mac无法切割问题】使用按字符切分的 DocumentSplitter,避免依赖 tokenizer
    @Bean
    public DocumentSplitter documentSplitter() {
        return DocumentSplitters.recursive(
                300,  // 每段最大字符数
                50    // 重叠字符数
        );
    }

    //构建向量数据库操作对象
//    @Bean
//    public RedisEmbeddingStore redisEmbeddingStore() {
//
//        // 1. 构建 Metadata 配置的 Map
//        Map<String, SchemaField> metadataConfig = new HashMap<>();
//        // 使用 TagField 告诉 Redis：这两个字段要作为精准匹配的标签建立索引！
//        metadataConfig.put("memory_id", new TagField(FieldName.of("$.memory_id").as("memory_id")));
//        metadataConfig.put("kb_id", new TagField(FieldName.of("$.kb_id").as("kb_id")));
//
//        // 2. 将组装好的 Map 喂给 builder
//        return RedisEmbeddingStore.builder()
//                .host(host)
//                .port(port)
//                .dimension(1536) // 阿里云 text-embedding-v2 的维度
//                .metadataConfig(metadataConfig)
//                .build();
//    }
    @Bean
    public MilvusEmbeddingStore milvusEmbeddingStore() {
        return MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .databaseName(databaseName)
                .collectionName(collectionName)
                .dimension(1536) // 与 text-embedding-v2 一致
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .withAuthorization(username,password)
                .build();
        return new MilvusServiceClient(connectParam);
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel, MilvusEmbeddingStore milvusEmbeddingStore) {
        return new HybridContentRetriever(milvusEmbeddingStore, embeddingModel);
    }

    @Bean
    public ContentRetriever PptContentRetriever(EmbeddingModel embeddingModel, MilvusEmbeddingStore milvusEmbeddingStore) {
        return new PptHybridContentRetriever(milvusEmbeddingStore, embeddingModel);
    }

    @Bean
    public ConsultantService getConsultantService(ChatMemory chatMemory, ChatMemoryProvider chatMemoryProvider, ContentRetriever contentRetriever, McpToolProvider mcpToolProvider) {
        return AiServices.builder(ConsultantService.class)
                .streamingChatModel(openAiStreamingChatModel)
                .chatModel(openAiChatModel)
//                .chatMemory(chatMemory)
//                .chatMemoryProvider(chatMemoryProvider)
//                .contentRetriever(contentRetriever)
                .toolProviders(mcpToolProvider)
                .tools(digitalVideoTools, fileHistoryTools)
                .build();
    }
}
