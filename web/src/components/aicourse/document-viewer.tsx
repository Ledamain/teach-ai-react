'use client'

import { useState, useEffect } from 'react'
import { Download, ZoomIn, ZoomOut, Loader2, AlertCircle } from 'lucide-react'
import styles from '@/styles/aicourse/documentViewer/index.module.css'

interface DocumentViewerProps {
  lessonId: string
}

interface DocumentData {
  content: string
  title: string
  lastModified?: string
}

// API接口预留 - 你可以根据实际后端实现修改这个函数
async function fetchDocumentContent(lessonId: string): Promise<DocumentData> {
  // TODO: 替换为实际的API调用
  // 示例：const response = await fetch(`/api/documents/${lessonId}`)
  // return response.json()
  
  // 模拟API响应 - 实际使用时请替换为真实的API调用
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        title: `课程文档 - ${lessonId}`,
        content: getPlaceholderContent(lessonId),
        lastModified: new Date().toISOString(),
      })
    }, 500)
  })
}

function getPlaceholderContent(lessonId: string): string {
  const contentMap: Record<string, string> = {
    'lesson-1-1': `
      <h1>通用模式下利用对应知识库实现点对点问答</h1>
      
      <h2>1. 学习目标</h2>
      <p>通过本节课的学习，您将掌握如何在通用模式下使用AI知识库进行高效的问答交互。</p>
      
      <h2>2. 知识要点</h2>
      <ul>
        <li>了解通用模式的基本概念和应用场景</li>
        <li>掌握知识库的构建方法</li>
        <li>学会使用点对点问答技术</li>
        <li>理解AI问答系统的工作原理</li>
      </ul>
      
      <h2>3. 实践步骤</h2>
      <p>首先，我们需要准备相关的知识库数据...</p>
      <p>接下来，配置AI模型的参数...</p>
      <p>最后，测试问答系统的响应效果...</p>
      
      <h2>4. 注意事项</h2>
      <p>在使用过程中，请注意以下几点：</p>
      <ol>
        <li>确保知识库数据的准确性和完整性</li>
        <li>合理设置问答系统的响应阈值</li>
        <li>定期更新和维护知识库内容</li>
      </ol>
      
      <h2>5. 课后练习</h2>
      <p>请尝试构建一个简单的知识库，并测试其问答效果。</p>
    `,
    'lesson-1-2': `
      <h1>专属模式下利用专属知识库实现面对面问答</h1>
      
      <h2>1. 学习目标</h2>
      <p>掌握专属模式的配置方法，学会创建和管理专属知识库。</p>
      
      <h2>2. 专属模式介绍</h2>
      <p>专属模式是一种定制化的AI交互方式，允许用户创建私有的知识库...</p>
      
      <h2>3. 知识库构建</h2>
      <p>构建专属知识库需要以下步骤：</p>
      <ul>
        <li>收集和整理相关文档</li>
        <li>进行数据预处理</li>
        <li>导入知识库系统</li>
        <li>配置检索参数</li>
      </ul>
    `,
    'lesson-4-3': `
    <html lang="zh-CN"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>自研AI计算机基础课程大纲图像生成平台使用教学计划书</title><style>* {margin: 0;padding: 0;box-sizing: border-box;font-family: "Microsoft YaHei", sans-serif;}body {background-color: #f5f5f5;line-height: 1.6;}.container {max-width: 1000px;margin: 0 auto;background: white;padding: 30px;border-radius: 8px;box-shadow: 0 2px 10px rgba(0,0,0,0.08);}h1 {text-align: center;color: #2c3e50;margin-bottom: 25px;font-size: 24px;border-bottom: 2px solid #3498db;padding-bottom: 10px;}h2 {color: #3498db;margin: 20px 0 10px;font-size: 20px;}h3 {color: #2c3e50;margin: 15px 0 8px;font-size: 17px;}p {margin: 8px 0;color: #333;}ul, ol {margin-left: 25px;color: #333;}li {margin: 6px 0;}.code-box {background-color: #f8f9fa;border-left: 4px solid #3498db;padding: 12px 15px;margin: 10px 0;font-family: monospace;white-space: pre-wrap;word-wrap: break-word;color: #2d3436;}.section {margin-bottom: 25px;}</style></head><body><div class="container"><h1>自研AI计算机基础课程大纲图像生成平台使用教学计划书</h1>
<div class="section"><h2>一、计划书概述</h2><p>&emsp;&emsp;本计划书核心是教会用户操作平台生成计算机基础课程大纲图片，重点完善操作步骤，配套基础关键字规范与问题解决方案，简洁实用。</p><p>平台核心功能：输入含关键字的指令，AI一键生成可视化大纲图片，无需专业设计能力，适配教学场景。</p></div>
<div class="section"><h2>二、教学核心目标</h2><ol><li>熟练掌握平台完整操作步骤，能独立完成图片生成与导出。</li><li>了解基础关键字规范，规避生成异常问题。</li></ol></div>
<div class="section"><h2>三、教学对象与适用场景</h2><h3>（一）教学对象</h3><p>教师、学生、教学管理人员等，零基础可上手，无需AI技术与设计经验。</p><h3>（二）适用场景</h3><p>教师制作课件、学生整理笔记、教学管理人员规范资料。</p></div>
<div class="section"><h2>四、核心教学内容</h2><h3>（一）平台操作基础流程</h3><ol><li>&emsp;&emsp;打开智汇伴学平台，等待平台完全加载完成，进入平台主界面，找到顶部导航栏中的「图像生成」选项，点击进入专属操作界面，界面将自动显示指令输入框、发送按钮等核心操作区域，无需额外跳转。</li><li>&emsp;&emsp;手把手教学生成示例：在指令输入框中，直接复制粘贴示例指令（可直接使用，无需修改）：生成计算机基础课程大纲图片 版式：上下分层 风格：简约黑白 只包含以下内容：1. 课程名称：计算机基础 2. 第一章：计算机概述 3. 第二章：操作系统 4. 第三章：办公软件，输入完成后，核对指令无错误、无遗漏核心信息（生成、只包含、计算机基础）。</li><li>&emsp;&emsp;核对无误后，点击输入框右侧的「发送」按钮，启动图片生成功能，期间无需进行任何额外操作，耐心等待3-5秒，平台将自动解析指令并生成图片；生成完成后，图片将在操作界面中间区域显示，可直观预览生成效果（重点查看内容是否完整、排版是否整齐、无乱码，本示例生成的图片将清晰呈现4项核心内容，上下分层排版，简约黑白风格）。</li><li>&emsp;&emsp;若预览效果不符合需求（如想更换风格、调整版式），返回指令输入框，修改示例指令即可（例：将“简约黑白”改为“科技风”，将“上下分层”改为“左右分栏”），修改完成后重新点击「发送」，再次生成图片，直至效果符合需求。</li><li>&emsp;&emsp;预览效果确认无误后，点击操作界面右下角的「导出图片」按钮，弹出保存路径选择窗口，选择本地指定文件夹（如桌面、课件文件夹），点击「确定」，即可将生成的计算机基础课程大纲图片保存至本地，完成全部生成操作。</li></ol>
<h3>（二）基础关键字规范</h3><p>核心关键字：生成/绘制（操作指令）、只包含（内容限制）、计算机基础（核心主题）；避免模糊形容词、特殊符号、冗长表述，规避生成异常。</p><p>可直接复制粘贴的关键字示例（搭配完整指令，无需修改可直接使用）：</p><div class="code-box">示例1：指令：生成计算机基础课程大纲图片 版式：上下分层 风格：简约黑白 只包含以下内容：1. 课程名称：计算机基础 2. 第一章：计算机概述 3. 第二章：操作系统 4. 第三章：办公软件</div><div class="code-box">示例2：指令：绘制计算机基础课程大纲图片 版式：左右分栏 风格：科技风 只包含以下内容：计算机基础 第一章：计算机概述 第二章：操作系统基础 第三章：办公软件应用</div><div class="code-box">示例3：指令：生成计算机基础课程大纲图片 风格：清新蓝色 只包含以下内容：计算机基础 第一章：硬件基础 第二章：软件基础 第三章：计算机安全</div>
<h3>（三）常见问题及解决方案</h3><ol><li>乱码：精简指令、删除特殊符号、添加“只包含”关键字，重新生成。</li><li>生成失败：确保指令含核心关键字，简化表述，重新发送。</li><li>排版混乱：使用规范版式关键字（上下分层、左右分栏），重新生成。</li></ol></div></div></body></html>
`,
    'lesson-4-4': `
    <!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>自研AI计算机基础在线教学视频生成平台使用教学计划书</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", sans-serif;
        }
        body {
            line-height: 1.6;
            color: #333;
        }
        h1 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 24px;
            font-size: 24px;
        }
        h2 {
            color: #2d3748;
            margin: 20px 0 12px;
            font-size: 20px;
            border-left: 4px solid #3498db;
            padding-left: 10px;
        }
        h3 {
            color: #2d3748;
            margin: 16px 0 10px;
            font-size: 18px;
        }
        h4 {
            font-size: 16px;
            margin: 12px 0 8px;
            color: #2d3748;
        }
        p {
            margin-bottom: 12px;
            text-align: justify;
        }
        ol, ul {
            margin-left: 24px;
            margin-bottom: 12px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 14px 0;
            font-size: 14px;
        }
        table, th, td {
            border: 1px solid #ddd;
        }
        th, td {
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f7fafc;
        }
        .success-case {
            background-color: #f0fdf4;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 12px;
        }
        .fail-case {
            background-color: #fff1f0;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 12px;
        }
    </style>
</head>
<body>
    <h1>自研AI计算机基础在线教学视频生成平台使用教学计划书</h1>

    <h2>一、计划书概述</h2>
    <p>本计划书旨在规范自研AI计算机基础在线教学视频生成平台的使用教学流程，核心目标是教会用户熟练操作本平台，成功生成可在线播放的计算机基础教学视频（如计算机概述、操作系统基础、办公软件操作等知识点讲解视频）。教学核心聚焦平台实操步骤、指令规范、案例演示及常见问题排查，帮助用户规避教学视频无法生成、画面错乱、声音异常、内容偏离计算机基础主题等问题，所有案例均围绕计算机基础教学场景设计，确保教学内容实用、易懂。</p>
    <p>本平台核心功能：用户输入符合规范的指令，平台通过自研AI模型解析指令，自动生成可在线播放的计算机基础教学视频（含清晰画面、标准讲解语音、知识点字幕、简单动画演示），无需专业视频剪辑能力、配音功底和教学设计经验，一键生成后可直接在线播放、分享，适配教师备课、学生自主学习、线上教学宣讲等场景，聚焦计算机基础全知识点，贴合教学需求。</p>

    <h2>二、教学核心目标</h2>
    <ol>
        <li>教会用户快速熟悉平台操作流程，掌握从指令输入到教学视频生成、在线播放、链接保存/导出的全步骤，能够独立完成计算机基础在线教学视频生成。</li>
        <li>帮助用户掌握平台指令的规范输入方法，明确指令需包含的核心信息（计算机基础知识点、视频需求等），能够精准输入指令，确保生成的教学视频贴合计算机基础教学场景、符合自身需求。</li>
        <li>引导用户识别教学视频生成过程中的常见问题，掌握简单的排查和修改方法，规避视频无法生成、画面错乱、声音异常、内容偏离知识点等问题。</li>
        <li>提供可直接参考的计算机基础教学视频生成案例和指令模板，方便用户快速套用，提升平台使用效率，确保生成的教学视频可正常在线播放、知识点准确、适配教学场景。</li>
    </ol>

    <h2>三、教学对象与适用场景</h2>
    <h3>（一）教学对象</h3>
    <p>所有需要使用本平台生成计算机基础在线教学视频的用户，包括计算机基础课程教师、教育培训机构工作人员、学生（用于知识点讲解展示）、企业内部计算机基础培训人员等，无需具备AI技术基础、视频剪辑能力、配音经验和专业教学设计能力，零基础可快速上手。</p>

    <h3>（二）适用场景</h3>
    <ol>
        <li>计算机基础教师：快速生成知识点教学视频（如鼠标操作、文件管理、办公软件基础等），用于课堂辅助教学、课后作业布置、线上答疑，节省备课时间。</li>
        <li>教育培训机构：生成计算机基础入门教学视频，用于线上招生宣讲、学员课前预习，适配零基础学员入门学习，降低教学成本。</li>
        <li>学生：生成计算机基础知识点讲解视频，用于课程作业、小组展示、知识点复盘，提升表达能力和对知识点的掌握程度。</li>
        <li>企业内部培训：生成员工计算机基础培训视频（如办公软件操作、计算机安全基础等），用于员工自主学习、新员工入职培训，规范培训内容和流程。</li>
    </ol>

    <h2>四、核心教学内容（重点：实操步骤与指令规范）</h2>
    <p>本平台的使用核心是「输入符合规范的指令」，指令需清晰、准确包含计算机基础知识点、视频相关需求，无需复杂表述，以下是详细教学内容，也是本计划书的重点，帮助用户快速上手、规避各类问题，所有内容均围绕计算机基础教学视频生成场景。</p>

    <h3>（一）平台操作基础流程</h3>
    <ol>
        <li>打开智汇伴学平台，主页即可进行对话。</li>
        <li>在指令输入框中，输入符合规范的指令，明确计算机基础知识点、视频时长、讲解风格、是否需要字幕等关键信息（参考后续案例和模板），输入完成后仔细核对，确认知识点准确、需求清晰，点击「生成教学视频」按钮。</li>
        <li>等待，平台自动生成可在线播放的计算机基础教学视频，生成完成后页面会显示“生成成功”提示，并出现「在线播放」「导出视频」两个按钮；点击「在线播放」，即可直接观看视频，核对内容和效果。</li>
        <li>若视频不符合需求（如知识点偏离、声音异常、无字幕），返回指令输入框，修改指令中的关键信息后重新生成；若生成成功且符合需求，点击「导出视频」可将视频保存至本地，用于线下播放、课件插入等场景。</li>
    </ol>

    <h3>（二）指令输入规范</h3>
    <p>指令是平台生成计算机基础教学视频的核心依据，输入规范直接决定视频能否正常生成、是否符合教学需求，无需复杂表述，只需包含3类关键信息，简洁、准确即可，具体规范如下，所有规范均贴合计算机基础教学场景：</p>
    <ol>
        <li>必须包含「计算机基础具体知识点」：明确告知平台要生成的教学视频对应的知识点，如计算机概述、鼠标基本操作、Windows系统文件管理、Word文档基础操作、Excel表格入门等，避免模糊表述（如“计算机基础视频”“办公软件教学”）。</li>
        <li>必须包含「在线播放」相关表述：明确要求生成可在线播放的教学视频，避免平台生成静态画面或无法播放的文件。</li>
        <li>可补充「视频需求」（可选）：根据教学需求，补充视频时长（如1-3分钟，适配知识点讲解）、讲解风格（如简洁通俗、严谨专业）、是否需要字幕、是否添加简单动画演示等，无需多余描述，贴合计算机基础教学场景即可。</li>
    </ol>

    <h3>（三）计算机基础在线教学视频生成案例教学</h3>
    <h4>1. 成功案例</h4>
    <div class="success-case">
        <p><strong>成功案例1：</strong></p>
        <p>指令为“生成可在线播放的计算机基础教学视频，知识点：鼠标基本操作（左键、右键、滚轮功能），视频时长1分钟，简洁通俗讲解，添加字幕”。</p>
        <p>生成效果为视频可正常在线播放，画面清晰，讲解语音标准、通俗易懂，精准讲解鼠标左键点击、右键菜单、滚轮滚动的核心功能，配有同步字幕，无画面错乱、声音异常，适配零基础学员入门学习。</p>
    </div>
    <div class="success-case">
        <p><strong>成功案例2：</strong></p>
        <p>指令为：“生成可在线播放的计算机基础教学视频，知识点：Word文档基础操作（新建、保存、字体设置），视频时长2分钟，严谨专业讲解，添加简单动画演示，带字幕”。</p>
        <p>生成效果为视频可正常在线播放，画面包含Word操作界面演示，动画清晰展示操作步骤，讲解语音专业，知识点准确，字幕同步，适配教师课堂辅助教学、学生自主学习。</p>
    </div>
    <div class="success-case">
        <p><strong>成功案例3：</strong></p>
        <p>指令为：“生成可在线播放的计算机基础教学视频，知识点：Windows系统文件管理（新建文件夹、复制、粘贴），视频时长1分30秒，简洁讲解，无需动画，带字幕”。</p>
        <p>生成效果为视频可正常在线播放，画面清晰展示系统操作步骤，讲解简洁明了，知识点准确，无多余内容，字幕清晰，适配企业新员工培训、学生基础练习。</p>
    </div>

    <h4>2. 失败案例</h4>
    <div class="fail-case">
        <p><strong>失败案例1：</strong></p>
        <p>错误指令为：“帮我做一个计算机基础教学视频，要好看一点，讲解清楚，时长随便”</p>
        <p>错误原因为指令未明确计算机基础具体知识点，使用模糊描述（好看、讲解清楚、时长随便），未清晰表达核心需求，生成问题为平台无法解析需求，生成失败，页面提示“指令不明确，请补充具体计算机基础知识点”，修改后正确指令为“生成可在线播放的计算机基础教学视频，知识点：计算机概述（定义、用途），视频时长1分钟，简洁讲解，带字幕”。</p>
    </div>
    <div class="fail-case">
        <p><strong>失败案例2：</strong></p>
        <p>错误指令为：“生成计算机基础视频，知识点：Excel操作，时长5分钟，要特别复杂，有很多特效，界面要华丽#在线播放”</p>
        <p>错误原因为指令包含特殊符号（#），需求过于复杂（过多特效、华丽界面），超出平台生成范围，且知识点表述模糊（Excel操作未明确具体内容），生成问题为生成后视频画面错乱、声音卡顿，部分知识点缺失，无法正常在线播放，修改后正确指令为“生成可在线播放的计算机基础教学视频，知识点：Excel表格入门（单元格选中、输入内容），视频时长2分钟，简洁讲解，带字幕”。</p>
    </div>
    <div class="fail-case">
        <p><strong>失败案例3：</strong></p>
        <p>错误指令为：“在线播放计算机基础教学视频，知识点：文件管理”</p>
        <p>错误原因为指令缺少核心操作指令（生成），表述不完整，生成问题为平台无法识别操作需求，无法生成视频，页面无响应，修改后正确指令为“生成可在线播放的计算机基础教学视频，知识点：Windows系统文件管理（删除、重命名文件），视频时长1分钟，简洁讲解”。</p>
    </div>

    <h3>（四）常见问题及解决方案（重点解决生成失败、无法播放问题）</h3>
    <table>
        <tr>
            <th>常见问题</th>
            <th>核心原因</th>
            <th>解决方案</th>
        </tr>
        <tr>
            <td>平台无法生成视频，提示“指令不明确”</td>
            <td>指令未包含计算机基础具体知识点，或表述模糊、无核心需求</td>
            <td>修改指令，补充明确的计算机基础知识点（如鼠标操作、Word基础），删除模糊描述，明确视频核心需求，确保指令简洁、有明确指向</td>
        </tr>
        <tr>
            <td>生成视频后，无法在线播放，出现报错</td>
            <td>指令包含特殊符号、需求过于复杂（如过多特效、过长时长），或指令字数超标</td>
            <td>删除指令中的特殊符号，简化需求（如去掉“过多特效”“华丽界面”），控制视频时长在1-3分钟（适配知识点讲解）</td>
        </tr>
        <tr>
            <td>生成的视频知识点与需求不符</td>
            <td>指令中计算机基础知识点表述模糊（如“办公软件操作”未明确是Word、Excel还是PowerPoint）</td>
            <td>修改指令，明确具体知识点（如“Word文档新建与保存”），避免模糊表述，重新生成</td>
        </tr>
        <tr>
            <td>视频声音异常（无声音、杂音）或字幕缺失</td>
            <td>指令未明确要求添加字幕，或平台生成过程中声音解析异常</td>
            <td>修改指令，补充“添加字幕”“清晰语音”等需求，重新生成；若仍有杂音，可简化视频需求，减少画面和声音的复杂程度</td>
        </tr>
        <tr>
            <td>视频画面错乱、操作演示不清晰</td>
            <td>指令中未明确操作演示需求，或知识点涉及复杂操作，超出平台简易生成范围</td>
            <td>补充“简单操作演示”“清晰画面”等需求，选择简单的计算机基础知识点（避免复杂操作），修改指令后重新生成</td>
        </tr>
    </table>
</body>
</html>
    `,
  }
  
  return contentMap[lessonId] || `
    <h1>课程文档</h1>
    <p>该课程的详细文档内容将在这里显示。</p>
    <p>请通过API接口加载实际的Word文档内容。</p>
    
    <h2>文档加载说明</h2>
    <p>您可以通过以下方式加载Word文档：</p>
    <ol>
      <li>调用后端API获取文档内容</li>
      <li>使用文档解析库转换Word格式</li>
      <li>将解析后的HTML内容渲染到此处</li>
    </ol>
    
    <h2>API接口示例</h2>
    <pre>GET /api/documents/{lessonId}</pre>
    <p>返回格式：</p>
    <pre>
{
  "title": "文档标题",
  "content": "HTML格式的文档内容",
  "lastModified": "2024-01-01T00:00:00Z"
}
    </pre>
  `
}

export function DocumentViewer({ lessonId }: DocumentViewerProps) {
  const [document, setDocument] = useState<DocumentData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [zoom, setZoom] = useState(100)

  useEffect(() => {
    async function loadDocument() {
      setLoading(true)
      setError(null)
      
      try {
        const data = await fetchDocumentContent(lessonId)
        setDocument(data)
      } catch (err) {
        setError('文档加载失败，请稍后重试')
        console.error('Failed to load document:', err)
      } finally {
        setLoading(false)
      }
    }
    
    loadDocument()
  }, [lessonId])

  const handleZoomIn = () => {
    setZoom((prev) => Math.min(prev + 10, 150))
  }

  const handleZoomOut = () => {
    setZoom((prev) => Math.max(prev - 10, 70))
  }

  const handleDownload = () => {
    // TODO: 实现文档下载功能
    // 可以调用API下载原始Word文档
    console.log('Download document:', lessonId)
  }

  if (loading) {
    return (
      <div className={styles.loadingWrapper}>
        <div className={styles.loadingContent}>
          <Loader2 className={styles.loadingIcon} />
          <span className={styles.loadingText}>加载文档中...</span>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className={styles.errorWrapper}>
        <div className={styles.errorContent}>
          <AlertCircle className={styles.errorIcon} />
          <span className={styles.errorText}>{error}</span>
          <button 
            onClick={() => window.location.reload()}
            className={styles.retryButton}
          >
            点击重试
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className={styles.container}>
      {/* Toolbar */}
      <div className={styles.toolbar}>
        <div className={styles.zoomControls}>
          <button
            onClick={handleZoomOut}
            className={styles.zoomButton}
            title="缩小"
          >
            <ZoomOut className={styles.zoomButtonIcon} />
          </button>
          <span className={styles.zoomValue}>
            {zoom}%
          </span>
          <button
            onClick={handleZoomIn}
            className={styles.zoomButton}
            title="放大"
          >
            <ZoomIn className={styles.zoomButtonIcon} />
          </button>
        </div>
        
        <button
          onClick={handleDownload}
          className={styles.downloadButton}
          title="下载文档"
        >
          <Download className={styles.downloadButtonIcon} />
          <span>下载</span>
        </button>
      </div>

      {/* Document Content */}
      <div className={styles.documentContent}>
        <div 
          className={styles.documentContentInner}
          style={{ fontSize: `${zoom}%` }}
          dangerouslySetInnerHTML={{ 
            __html: document?.content || '' 
          }}
        />
      </div>
    </div>
  )
}
