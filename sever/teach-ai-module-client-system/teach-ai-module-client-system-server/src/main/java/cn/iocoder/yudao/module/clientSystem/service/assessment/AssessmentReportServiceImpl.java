package cn.iocoder.teach-ai.module.clientSystem.service.assessment;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.assessment.AssessmentReportDO;
import cn.iocoder.teach-ai.module.clientSystem.dal.mysql.assessment.AssessmentReportMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentReportServiceImpl implements AssessmentReportService {

    @Resource
    private AssessmentReportMapper mapper;

    @Override
    public AssessmentReportDO saveReport(Long userId, AssessmentReportDO report) {
        report.setUserId(userId);
        mapper.insert(report);
        return report;
    }

    @Override
    public AssessmentReportDO getLatestByUserId(Long userId) {
        return mapper.selectLatestByUserId(userId);
    }

    @Override
    public List<AssessmentReportDO> getHistoryByUserId(Long userId, int limit) {
        List<AssessmentReportDO> all = mapper.selectByUserIdAndPeriod(userId, null, null);
        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
    }
}
