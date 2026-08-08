package cn.iocoder.teach-ai.module.clientSystem.controller.client.repo.vo;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.repo.vo.RepoRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.teach-ai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
public class ClientRepoGroupDetailRespVO {

    @Schema(description = "课程文件夹id", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    private Long id;

    @Schema(description = "课程文件夹名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    private String repoGroupName;

    @Schema(description = "课程文件夹描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "10251")
    private String repoGroupDescription;

    @Schema(description = "文件数量", example = "10251")
    private Integer fileCount;

    @Schema(description = "知识库列表")
    private List<RepoRespVO> repoList;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime updateTime;

}
