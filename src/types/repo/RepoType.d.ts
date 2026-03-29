// 知识库类型定义
interface KnowledgeFile {
    id: string;
    repoTitle: string;
}
interface KnowledgeCategory {
    id: string;
    repoCategoryName: string;
    repoDTOS: KnowledgeFile[];
}