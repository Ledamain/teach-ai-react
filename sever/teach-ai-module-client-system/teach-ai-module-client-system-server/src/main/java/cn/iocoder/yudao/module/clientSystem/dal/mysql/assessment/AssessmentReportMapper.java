package cn.iocoder.teach-ai.module.clientSystem.dal.mysql.assessment;

import cn.iocoder.teach-ai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.teach-ai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.assessment.AssessmentReportDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AssessmentReportMapper extends BaseMapperX<AssessmentReportDO> {

    /** 查询用户最新评估报告 */
    default AssessmentReportDO selectLatestByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<AssessmentReportDO>()
                .eq(AssessmentReportDO::getUserId, userId)
                .orderByDesc(AssessmentReportDO::getCreateTime)
                .last("LIMIT 1"));
    }

    /** 查询用户某时间段内的评估报告 */
    default List<AssessmentReportDO> selectByUserIdAndPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        return selectList(new LambdaQueryWrapperX<AssessmentReportDO>()
                .eq(AssessmentReportDO::getUserId, userId)
                .ge(AssessmentReportDO::getCreateTime, start)
                .le(AssessmentReportDO::getCreateTime, end)
                .orderByDesc(AssessmentReportDO::getCreateTime));
    }
}
