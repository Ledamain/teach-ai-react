import { request } from '@/utils/request';
import {
    BindPptArtifactResp,
    ExportPptArtifactResp, GetPptArtifactExportResultResp,
    InitiatePptCreationResp,
    PageResponseBO, pptExportParam,
    TemplateBO
} from "@/types/ppt/PptType";



export default {
    // SSE 大纲
    sseUrl(query: string, templateId?: string) {
        let u = `client-api/ppt/runPptOutlineGeneration?query=${encodeURIComponent(query)}`;
        if (templateId) u += `&templateId=${templateId}`;
        return u;
    },

    // 模板列表
    getTemplate() {
        return request.get<PageResponseBO<TemplateBO>>('/ppt/template');
    },

    // 创建PPT
    initiatePptCreation(data: { taskId: string; outline: string; templateId?: string }) {
        return request.post<InitiatePptCreationResp>('/ppt/initiatePptCreation', data);
    },

    bindPptArtifact(data: { taskId: string; artifactId: string }) {
        return request.post<BindPptArtifactResp>('/ppt/bindPptArtifact', data);
    },

    exportPptArtifact(data: { artifactId: string }) {
        return request.post<ExportPptArtifactResp>('/ppt/exportPptArtifact', data);
    },

    getPptArtifactExportResult(data: pptExportParam) {
        return request.post<boolean>('/ppt/getPptArtifactExportResult', data);
    },
};