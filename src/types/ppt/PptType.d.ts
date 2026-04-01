export interface TemplateBO {
    id: string;
    previewUrl: string;
}

export interface PageResponseBO<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
}

export interface InitiatePptCreationResp {
    appKey: string;
    secret: string;
    taskId: string;
}

export interface BindPptArtifactResp {}
export interface ExportPptArtifactResp { exportTaskId: string; }
export interface GetPptArtifactExportResultResp { exportFileLink: string[]; }