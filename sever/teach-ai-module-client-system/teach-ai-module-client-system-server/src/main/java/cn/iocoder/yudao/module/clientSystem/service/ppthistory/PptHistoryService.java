package cn.iocoder.teach-ai.module.clientSystem.service.ppthistory;

import java.util.*;

import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDTO;
import jakarta.validation.*;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.ppthistory.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.ppthistory.PptHistoryDO;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;

/**
 * PPT历史记录 Service 接口
 *
 * @author waynelam
 */
public interface PptHistoryService {

    /**
     * 创建PPT历史记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPptHistory(@Valid PptHistorySaveReqVO createReqVO);

    /**
     * 更新PPT历史记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePptHistory(@Valid PptHistorySaveReqVO updateReqVO);

    /**
     * 删除PPT历史记录
     *
     * @param id 编号
     */
    void deletePptHistory(Long id);

    /**
    * 批量删除PPT历史记录
    *
    * @param ids 编号
    */
    void deletePptHistoryListByIds(List<Long> ids);

    /**
     * 获得PPT历史记录
     *
     * @param id 编号
     * @return PPT历史记录
     */
    PptHistoryDO getPptHistory(Long id);

    /**
     * 获得PPT历史记录
     *
     * @param fileName 文件名
     * @return PPT历史记录
     */
    PptHistoryDO getPptHistoryByFileName(String fileName);

    /**
     * 获得PPT历史记录分页
     *
     * @param pageReqVO 分页查询
     * @return PPT历史记录分页
     */
    PageResult<PptHistoryDTO> getPptHistoryPage(PptHistoryPageReqVO pageReqVO);

    /**
     * 获得PPT历史记录分页
     *
     * @param historyDTO 分页查询
     * @return PPT历史记录分页
     */
    List<cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO> getPptHistoryList(cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO historyDTO);

}
