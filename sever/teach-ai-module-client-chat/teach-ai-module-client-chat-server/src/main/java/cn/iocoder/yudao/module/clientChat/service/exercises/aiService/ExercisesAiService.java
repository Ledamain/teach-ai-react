package cn.iocoder.teach-ai.module.clientChat.service.exercises.aiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, //指定手动装配
        chatModel = "openAiChatModel" //配置阻塞式对话模型
)
public interface ExercisesAiService {

    @SystemMessage("你是一个生成习题试卷的智能助手，根据用户要求生成试卷，要求生成的试卷包含单选题：type: single、多选题：type: multiple、判断题：type: judge，不允许输出其他类型题目，直接给我试卷的json数据，输出内容不允许换行，以下为示例：{\n" +
            "  \"title\": \"前端基础综合测验\",\n" +
            "  \"description\": \"包含单选、多选、判断题，无简答题，适合前端直接渲染\",\n" +
            "  \"totalScore\": 100,\n" +
            "  \"questions\": [\n" +
            "    {\n" +
            "      \"id\": 1,\n" +
            "      \"type\": \"single\",\n" +
            "      \"title\": \"下列哪个是 JavaScript 的原始数据类型？\",\n" +
            "      \"options\": [\n" +
            "        \"Array\",\n" +
            "        \"Object\",\n" +
            "        \"String\",\n" +
            "        \"Function\"\n" +
            "      ],\n" +
            "      \"answer\": \"String\",\n" +
            "      \"score\": 10,\n" +
            "      \"analysis\": \"String 是基本数据类型，其余均为引用类型。\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 2,\n" +
            "      \"type\": \"single\",\n" +
            "      \"title\": \"HTML 中用于定义段落的标签是？\",\n" +
            "      \"options\": [\n" +
            "        \"<div>\",\n" +
            "        \"<p>\",\n" +
            "        \"<span>\",\n" +
            "        \"<section>\"\n" +
            "      ],\n" +
            "      \"answer\": \"<p>\",\n" +
            "      \"score\": 10,\n" +
            "      \"analysis\": \"<p> 标签专门用于表示文本段落。\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 3,\n" +
            "      \"type\": \"single\",\n" +
            "      \"title\": \"CSS 中哪个属性可以改变文字颜色？\",\n" +
            "      \"options\": [\n" +
            "        \"text-color\",\n" +
            "        \"font-color\",\n" +
            "        \"color\",\n" +
            "        \"background\"\n" +
            "      ],\n" +
            "      \"answer\": \"color\",\n" +
            "      \"score\": 10,\n" +
            "      \"analysis\": \"CSS 使用 color 属性设置文字颜色。\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 4,\n" +
            "      \"type\": \"multiple\",\n" +
            "      \"title\": \"下列属于 CSS 盒模型组成部分的有？\",\n" +
            "      \"options\": [\n" +
            "        \"margin\",\n" +
            "        \"padding\",\n" +
            "        \"border\",\n" +
            "        \"content\"\n" +
            "      ],\n" +
            "      \"answer\": [\n" +
            "        \"margin\",\n" +
            "        \"padding\",\n" +
            "        \"border\",\n" +
            "        \"content\"\n" +
            "      ],\n" +
            "      \"score\": 20,\n" +
            "      \"analysis\": \"标准盒模型包含内容、内边距、边框、外边距。\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 5,\n" +
            "      \"type\": \"multiple\",\n" +
            "      \"title\": \"下列哪些是 HTTP 请求方法？\",\n" +
            "      \"options\": [\n" +
            "        \"GET\",\n" +
            "        \"POST\",\n" +
            "        \"FETCH\",\n" +
            "        \"DELETE\"\n" +
            "      ],\n" +
            "      \"answer\": [\n" +
            "        \"GET\",\n" +
            "        \"POST\",\n" +
            "        \"DELETE\"\n" +
            "      ],\n" +
            "      \"score\": 20,\n" +
            "      \"analysis\": \"FETCH 是浏览器 API，并非 HTTP 请求方法。\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 6,\n" +
            "      \"type\": \"judge\",\n" +
            "      \"title\": \"在 JavaScript 中，null 和 undefined 是完全相同的。\",\n" +
            "      \"options\": [\n" +
            "        \"正确\",\n" +
            "        \"错误\"\n" +
            "      ],\n" +
            "      \"answer\": \"错误\",\n" +
            "      \"score\": 10,\n" +
            "      \"analysis\": \"null 表示空对象，undefined 表示未定义，二者类型不同。\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 7,\n" +
            "      \"type\": \"judge\",\n" +
            "      \"title\": \"CSS 中 display: none 会使元素脱离文档流。\",\n" +
            "      \"options\": [\n" +
            "        \"正确\",\n" +
            "        \"错误\"\n" +
            "      ],\n" +
            "      \"answer\": \"正确\",\n" +
            "      \"score\": 10,\n" +
            "      \"analysis\": \"display: none 不占据空间，visibility: hidden 仍占据空间。\"\n" +
            "    }\n" +
            "  ]\n" +
            "}")
    String exercisesChat(String prompt);

}
