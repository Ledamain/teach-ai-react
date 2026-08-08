package cn.iocoder.teach-ai.module.clientChat.mcp;

import cn.iocoder.teach-ai.module.clientChat.tools.DigitalVideoTools;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.skills.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;


import cn.iocoder.teach-ai.module.clientChat.mcp.tool.ToolCallInterceptor;

@Slf4j
@Configuration
public class McpConfiguration {

    private static final String MCP_PROXY_PATH = "/Users/apple/.local/bin/mcp-proxy";
    private static final String ACCESS_TOKEN = "121.23938b67a4850cdef64fb1a9cfd9dcae.YH85ZtTYsp19T_fAFGGQeQr-YZfv8O-lQcB14eA.NKOnZA";

    // 小度mcp
//    @Bean
//    public McpTransport getMcpTransport() {
//        // 和终端命令一一对应：--headers 后 key、value 分两个元素
//        List<String> command = List.of(
//                MCP_PROXY_PATH,
//                "https://xiaodu.baidu.com/dueros_mcp_server/mcp/",
//                "--headers",
//                "ACCESS_TOKEN",
//                ACCESS_TOKEN,
//                "--transport",
//                "streamablehttp"
//        );
//
//        StdioMcpTransport rawTransport = new StdioMcpTransport
//                .Builder()
//                .command(command)
//                .logEvents(true) // 打印完整MCP收发报文，方便排查schema null问题
//                .build();
//
//        // 包装传输层：将 image 类型 content 转换为 text（base64 data URL），
//        // 解决 langchain4j ToolExecutionHelper 不支持 image 类型的问题
//        return new McpHandlingTransport(rawTransport);
//    }

    // 小度mcp
    @Bean
    public McpTransport getMcpTransport() {
        StreamableHttpMcpTransport transport = new StreamableHttpMcpTransport.Builder()
                .url("https://xiaodu.baidu.com/dueros_mcp_server/mcp/")
                .customHeaders(Map.of("ACCESS_TOKEN", ACCESS_TOKEN))
                .logRequests(true)
                .logResponses(true)
                .timeout(Duration.ofDays(60))
                .build();
        return new McpHandlingTransport(transport);
    }

    // github mcp
//    @Bean
//    public McpTransport getGitHubMcpSport() {
//        StreamableHttpMcpTransport transport = new StreamableHttpMcpTransport.Builder()
//                .url("https://api.githubcopilot.com/mcp/")
//                .timeout(Duration.ofDays(60))
//                .logRequests(true)
//                .logResponses(true)
//                .customHeaders(Map.of("Authorization", "Bearer " + "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"))
//                .build();
//        return new McpHandlingTransport(transport);
//    }

    @Bean
    public McpClient mcpClient(@Qualifier("getMcpTransport") McpTransport mcpTransport) {
        return new DefaultMcpClient.Builder()
                .key("XIAODUMcpCLient")
                .transport(mcpTransport)
                .build();
    }

//    @Bean
//    public McpClient githubMcpClient(@Qualifier("getGitHubMcpSport") McpTransport mcpTransport) {
//        return new DefaultMcpClient.Builder()
//                .key("GITHUBMcpCLient")
//                .transport(mcpTransport)
//                .build();
//    }

    // skill加载器
//    @Bean
//    public ToolProvider getSkillToolProvider () {
//        Path skillPath = Path.of("teach-ai-module-client-chat/teach-ai-module-client-chat-server/skills");
//        log.info("技能绝对路径：{}", skillPath.toAbsolutePath());
//        log.info("目录是否存在：{}", Files.exists(skillPath));
//        Skills skills = Skills.from(FileSystemSkillLoader.loadSkills(skillPath)) ;
//        log.info("加载技能：{}", skills.formatAvailableSkills());
//        return skills.toolProvider();
//    }

//    @Bean
//    public ToolProvider getSkillToolProvider () {
//        log.info("开始从classpath加载skills资源目录");
//        List<Skill> skillList = ClassPathSkillLoader.loadSkills("skills");
//        log.info("ClassPathSkillLoader 读取到技能总数：{}", skillList.size());
//        // 遍历打印每个技能路径，判断是否被识别
//        skillList.forEach(skill -> log.info("识别到技能实例：{}", skill));
//
//        // 兜底逻辑：无技能不阻断启动
//        if (skillList.isEmpty()) {
//            log.error("classpath下存在SKILL.md文件，但加载器返回空列表，请检查SKILL.md格式规范");
//        }
//
//        Skills skills = Skills.from(skillList);
//        log.info("成功加载技能集合：{}", skills.formatAvailableSkills());
//        return skills.toolProvider();
//    }

    // Tools
//    @Resource
//    private DigitalVideoTools digitalVideoTools;
//
//    @Bean
//    public ToolProvider getSkillToolProviderTest () {
//        Skill skill = Skill.builder()
//                .name("incident-response")
//                .description("Step-by-step runbook for diagnosing and resolving production incidents")
//                .content("""
//                When a production alert fires:
//                1. Call `fetchRecentLogs(serviceName)` to retrieve the last 5 minutes of logs.
//                2. Call `checkServiceHealth(serviceName)` to get current health metrics.
//                3. Based on the findings, call `createIncidentTicket(summary, severity)`.
//                4. If severity is CRITICAL, also call `pageOnCall(incidentId)`.
//                """)
//                .tools(digitalVideoTools)
//                .build();
//        return skill.toolProviders().get(0);
//    }


    @Bean
    public McpToolProvider getMcpToolProvider(@Qualifier("mcpClient") McpClient mcpClient) {
        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .failIfOneServerFails(false)
                .toolWrapper(ToolCallInterceptor.create())
                .build();
    }
}
