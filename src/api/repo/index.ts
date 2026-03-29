import {request} from "@/utils/request";

export async function getKnowledgeList() {
    return request.get<KnowledgeCategory>('/repo/list', {
        params: {
            pageReqVO: null
        },
    })
}