import { NextRequest, NextResponse } from 'next/server'

// 文档API接口
// 用于获取Word文档内容，支持后端文档解析和转换

interface DocumentResponse {
    success: boolean
    data?: {
        title: string
        content: string
        lastModified: string
        metadata?: {
            author?: string
            wordCount?: number
            pageCount?: number
        }
    }
    error?: string
}

export async function GET(
    request: NextRequest,
    { params }: { params: Promise<{ lessonId: string }> }
): Promise<NextResponse<DocumentResponse>> {
    const { lessonId } = await params

    try {
        // TODO: 在这里实现实际的文档获取逻辑
        //
        // 示例实现思路：
        // 1. 根据lessonId从数据库或文件系统获取Word文档路径
        // 2. 使用文档解析库（如mammoth.js）将Word文档转换为HTML
        // 3. 返回转换后的HTML内容
        //
        // 示例代码（需要安装mammoth库）：
        // import mammoth from 'mammoth'
        // import fs from 'fs'
        // import path from 'path'
        //
        // const documentPath = path.join(process.cwd(), 'documents', `${lessonId}.docx`)
        // const buffer = fs.readFileSync(documentPath)
        // const result = await mammoth.convertToHtml({ buffer })
        // const htmlContent = result.value

        // 模拟响应 - 请替换为实际的文档处理逻辑
        const mockContent = getMockDocumentContent(lessonId)

        return NextResponse.json({
            success: true,
            data: {
                title: `课程文档 - ${lessonId}`,
                content: mockContent,
                lastModified: new Date().toISOString(),
                metadata: {
                    author: '课程团队',
                    wordCount: 1500,
                    pageCount: 5,
                },
            },
        })
    } catch (error) {
        console.error('Document fetch error:', error)
        return NextResponse.json(
            {
                success: false,
                error: '文档获取失败',
            },
            { status: 500 }
        )
    }
}

// POST方法 - 用于上传或更新文档
export async function POST(
    request: NextRequest,
    { params }: { params: Promise<{ lessonId: string }> }
): Promise<NextResponse<{ success: boolean; message?: string; error?: string }>> {
    const { lessonId } = await params

    try {
        const formData = await request.formData()
        const file = formData.get('file') as File | null

        if (!file) {
            return NextResponse.json(
                { success: false, error: '未找到上传的文件' },
                { status: 400 }
            )
        }

        // TODO: 实现文档上传和存储逻辑
        //
        // 示例实现思路：
        // 1. 验证文件类型（仅允许.docx, .doc等）
        // 2. 将文件保存到服务器或云存储
        // 3. 更新数据库中的文档记录
        //
        // 示例代码：
        // const buffer = Buffer.from(await file.arrayBuffer())
        // const uploadPath = path.join(process.cwd(), 'documents', `${lessonId}.docx`)
        // fs.writeFileSync(uploadPath, buffer)

        console.log(`Received document upload for lesson: ${lessonId}`, {
            fileName: file.name,
            fileSize: file.size,
            fileType: file.type,
        })

        return NextResponse.json({
            success: true,
            message: '文档上传成功',
        })
    } catch (error) {
        console.error('Document upload error:', error)
        return NextResponse.json(
            { success: false, error: '文档上传失败' },
            { status: 500 }
        )
    }
}

// 模拟文档内容 - 实际使用时请删除此函数
function getMockDocumentContent(lessonId: string): string {
    return `
    <h1>课程文档</h1>
    <p>这是 <strong>${lessonId}</strong> 的课程文档内容。</p>
    
    <h2>文档API说明</h2>
    <p>此API接口已预留好，支持以下功能：</p>
    <ul>
      <li><strong>GET</strong> - 获取文档内容</li>
      <li><strong>POST</strong> - 上传/更新文档</li>
    </ul>
    
    <h2>集成Word文档解析</h2>
    <p>推荐使用以下方式解析Word文档：</p>
    <ol>
      <li>安装 mammoth.js 库：<code>npm install mammoth</code></li>
      <li>读取Word文档文件</li>
      <li>调用 mammoth.convertToHtml() 转换为HTML</li>
      <li>返回HTML内容供前端显示</li>
    </ol>
    
    <h2>使用示例</h2>
    <pre>
import mammoth from 'mammoth'
import fs from 'fs'

const buffer = fs.readFileSync('document.docx')
const result = await mammoth.convertToHtml({ buffer })
const html = result.value
    </pre>
    
    <p>请根据实际需求修改此API的实现逻辑。</p>
  `
}
