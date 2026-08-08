package cn.iocoder.teach-ai.module.clientSystem.service.assessment;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.assessment.AssessmentReportDO;

import java.util.List;

public interface AssessmentReportService {

    /** 保存评估报告 */
    AssessmentReportDO saveReport(Long userId, AssessmentReportDO report);

    /** 查询用户最新评估报告 */
    AssessmentReportDO getLatestByUserId(Long userId);

    /** 查询用户评估历史 */
    List<AssessmentReportDO> getHistoryByUserId(Long userId, int limit);
}
