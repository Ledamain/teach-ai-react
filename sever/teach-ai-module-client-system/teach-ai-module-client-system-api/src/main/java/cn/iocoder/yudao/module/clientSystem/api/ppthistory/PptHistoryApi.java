package cn.iocoder.teach-ai.module.clientSystem.api.ppthistory;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.module.clientSystem.api.ppthistory.dto.PptHistoryDTO;
import cn.iocoder.teach-ai.module.clientSystem.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME, primary = false) // ① @FeignClient 注解，primary = false 避免与本地实现冲突
@Tag(name = "RPC 服务 - 客户端用户") // ② Swagger 接口文档
public interface PptHistoryApi {

    String PREFIX = ApiConstants.PREFIX + "/ppt-history";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建PPT历史记录")
    CommonResult<Long> createPptHistory(@Valid @RequestBody PptHistoryDTO createReqVO);

    @PostMapping(PREFIX + "/get-by-file-name")
    @Operation(summary = "根据文件名查询")
    CommonResult<PptHistoryDTO> getPptHistoryByFileName( @RequestParam String fileName);

    @PostMapping(PREFIX + "/list")
    @Operation(summary = "查询历史文件列表")
    CommonResult<List<PptHistoryDTO>> getPptHistoryList(@RequestBody PptHistoryDTO pageReqVO);

}
