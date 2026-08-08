package cn.iocoder.teach-ai.module.clientChat.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.mcp.client.McpCallContext;
import dev.langchain4j.mcp.client.transport.McpOperationHandler;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.protocol.McpClientMessage;
import dev.langchain4j.mcp.protocol.McpInitializeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * McpTransport 包装器：
 * 1. 兼容 LangChain4j 1.17.1-beta27 McpTransport 新接口（支持McpCallContext重载）
 * 2. 将 image 类型 content 转换为 text（适配小度拍照图片，data URL 直接嵌入 LLM 上下文）
 * 3. 将 resource 二进制资源转换为文本摘要（解决GitHub MCP Unsupported content type: "resource" 报错）
 */
public class McpHandlingTransport implements McpTransport {

    private static final Logger log = LoggerFactory.getLogger(McpHandlingTransport.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    /** 超过此大小的图片 data URL 将仅保留元数据，避免 LLM 上下文爆炸（约 375KB 原始图片） */
    private static final int MAX_DATA_URL_LENGTH = 500_000;

    private final McpTransport delegate;

    public McpHandlingTransport(McpTransport delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start(McpOperationHandler handler) {
        delegate.start(handler);
    }

    @Override
    public CompletableFuture<JsonNode> initialize(McpInitializeRequest request) {
        return delegate.initialize(request);
    }

    // 旧重载：McpClientMessage
    @Override
    public CompletableFuture<JsonNode> executeOperationWithResponse(McpClientMessage message) {
        return delegate.executeOperationWithResponse(message)
                .thenApply(this::transformAllContent);
    }

    // 新版新增重载：McpCallContext
    @Override
    public CompletableFuture<JsonNode> executeOperationWithResponse(McpCallContext context) {
        return delegate.executeOperationWithResponse(context)
                .thenApply(this::transformAllContent);
    }

    // 旧无响应重载
    @Override
    public void executeOperationWithoutResponse(McpClientMessage message) {
        delegate.executeOperationWithoutResponse(message);
    }

    // 新版新增无响应重载
    @Override
    public void executeOperationWithoutResponse(McpCallContext context) {
        delegate.executeOperationWithoutResponse(context);
    }

    @Override
    public void checkHealth() {
        delegate.checkHealth();
    }

    @Override
    public void onFailure(Runnable callback) {
        delegate.onFailure(callback);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    /**
     * 统一转换入口：同时处理 image + resource 两种不被默认解析器支持的类型
     */
    private JsonNode transformAllContent(JsonNode root) {
        if (root == null || !root.isObject()) {
            return root;
        }
        ObjectNode rootObj = (ObjectNode) root;

        JsonNode resultNode = rootObj.get("result");
        if (resultNode == null || !resultNode.isObject()) {
            return root;
        }

        JsonNode contentNode = resultNode.get("content");
        if (contentNode == null || !contentNode.isArray()) {
            return root;
        }

        ArrayNode contentArray = (ArrayNode) contentNode;
        boolean needTransform = false;
        for (JsonNode item : contentArray) {
            JsonNode typeNode = item.get("type");
            if (typeNode == null) continue;
            String type = typeNode.asText();
            if ("image".equals(type) || "resource".equals(type)) {
                needTransform = true;
                break;
            }
        }
        if (!needTransform) {
            return root;
        }

        ArrayNode newContent = mapper.createArrayNode();
        for (JsonNode item : contentArray) {
            JsonNode typeNode = item.get("type");
            if (typeNode == null) {
                newContent.add(item);
                continue;
            }
            String contentType = typeNode.asText();
            switch (contentType) {
                case "image":
                    newContent.add(convertImageToText(item));
                    break;
                case "resource":
                    newContent.add(convertResourceToText(item));
                    break;
                default:
                    newContent.add(item);
            }
        }

        ((ObjectNode) resultNode).set("content", newContent);
        return rootObj;
    }

    /**
     * image → text：将 data URL 直接嵌入 Markdown 图片语法，让 LLM 可以"看到"图片。
     * 超过 MAX_DATA_URL_LENGTH 的图片仅保留元数据。
     */
    private ObjectNode convertImageToText(JsonNode imageItem) {
        String data = imageItem.has("data") ? imageItem.get("data").asText() : "";
        String mimeType = imageItem.has("mimeType") ? imageItem.get("mimeType").asText() : "image/png";

        int imageBytes = 0;
        try {
            imageBytes = Base64.getDecoder().decode(data).length;
        } catch (Exception e) {
            log.warn("无法解码图片base64: {}", e.getMessage());
        }
        String sizeDesc = formatSize(imageBytes);
        String dataUrl = String.format("data:%s;base64,%s", mimeType, data);
        String textContent;

        if (dataUrl.length() > MAX_DATA_URL_LENGTH) {
            textContent = String.format(
                    "[📷 小度拍摄的照片]\n- 格式: %s\n- 大小: %s (%d bytes)\n- (图片数据过大，已省略完整内容，仅展示元数据)",
                    mimeType, sizeDesc, imageBytes
            );
            log.info("image content 转换(仅元数据): mimeType={}, size={}, dataUrlLength={}",
                    mimeType, sizeDesc, dataUrl.length());
        } else {
            // 图片较小，保留完整 data URL，让 LLM 可以"看到"图片
            textContent = String.format(
                    "[📷 小度拍摄的照片 (%s, %s)]\n![拍摄的照片](%s)",
                    mimeType, sizeDesc, dataUrl
            );
            log.info("image content 转换(data URL): mimeType={}, size={}, dataLength={}",
                    mimeType, sizeDesc, data.length());
        }

        ObjectNode textNode = mapper.createObjectNode();
        textNode.put("type", "text");
        textNode.put("text", textContent);
        return textNode;
    }

    /** resource二进制资源 → text摘要，解决GitHub MCP报错 */
    private ObjectNode convertResourceToText(JsonNode resourceItem) {
        JsonNode resourceObj = resourceItem.get("resource");
        String uri = "";
        String mimeType = "";
        long blobByteLen = 0;

        if (resourceObj != null && resourceObj.isObject()) {
            uri = resourceObj.has("uri") ? resourceObj.get("uri").asText() : "";
            mimeType = resourceObj.has("mimeType") ? resourceObj.get("mimeType").asText() : "unknown";
            String blob = resourceObj.has("blob") ? resourceObj.get("blob").asText() : "";
            try {
                blobByteLen = Base64.getDecoder().decode(blob).length;
            } catch (Exception e) {
                log.warn("resource blob解码失败: {}", e.getMessage());
            }
        }

        String sizeDesc = formatSize((int) blobByteLen);
        String textContent = String.format(
                "[📦 MCP二进制资源文件]\n" +
                        "- 文件URI: %s\n" +
                        "- MIME类型: %s\n" +
                        "- 文件大小: %s (%d bytes)\n" +
                        "- 二进制内容已省略，无法直接读取文本",
                uri, mimeType, sizeDesc, blobByteLen
        );

        ObjectNode textNode = mapper.createObjectNode();
        textNode.put("type", "text");
        textNode.put("text", textContent);
        log.info("转换resource为text, uri={}, mimeType={}, size={}", uri, mimeType, sizeDesc);
        return textNode;
    }

    private static String formatSize(int bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
