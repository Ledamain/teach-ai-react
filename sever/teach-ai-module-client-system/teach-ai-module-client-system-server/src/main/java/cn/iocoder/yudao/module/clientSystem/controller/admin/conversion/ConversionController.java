package cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion;

import cn.hutool.json.JSONUtil;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.ChatHistoryApi;
import cn.iocoder.teach-ai.module.clientChat.api.chathistory.dto.ChatMemoryDTO;
import cn.iocoder.teach-ai.module.clientChat.api.wordCloud.WordCloudApi;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;

import java.time.Duration;
import java.util.*;
import java.io.IOException;

import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

import cn.iocoder.teach-ai.framework.excel.core.util.ExcelUtils;

import cn.iocoder.teach-ai.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.teach-ai.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.conversion.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.conversion.ConversionDO;
import cn.iocoder.teach-ai.module.clientSystem.service.conversion.ConversionService;

@Tag(name = "管理后台 - 会话历史")
@RestController
@RequestMapping("/client-system/conversion")
@Validated
public class ConversionController {

    @Resource
    private ConversionService conversionService;

    @Resource
    private ChatHistoryApi chatHistoryApi;

    @Resource
    private WordCloudApi wordCloudApi;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/create")
    @Operation(summary = "创建会话历史")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:create')")
    public CommonResult<Long> createConversion(@Valid @RequestBody ConversionSaveReqVO createReqVO) {
        return success(conversionService.createConversion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会话历史")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:update')")
    public CommonResult<Boolean> updateConversion(@Valid @RequestBody ConversionSaveReqVO updateReqVO) {
        conversionService.updateConversion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会话历史")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:delete')")
    public CommonResult<Boolean> deleteConversion(@RequestParam("id") Long id) {
        conversionService.deleteConversion(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除会话历史")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:delete')")
    public CommonResult<Boolean> deleteConversionList(@RequestParam("ids") List<Long> ids) {
        conversionService.deleteConversionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会话历史")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<ConversionRespVO> getConversion(@RequestParam("id") Long id) {
        ConversionDO conversion = conversionService.getConversion(id);
        return success(BeanUtils.toBean(conversion, ConversionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得会话历史分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<PageResult<ConversionRespVO>> getConversionPage(@Valid ConversionPageReqVO pageReqVO) {
        PageResult<ConversionDTO> pageResult = conversionService.getConversionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ConversionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会话历史 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportConversionExcel(@Valid ConversionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ConversionDTO> list = conversionService.getConversionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "会话历史.xls", "数据", ConversionRespVO.class,
                        BeanUtils.toBean(list, ConversionRespVO.class));
    }

    @GetMapping("/get-content")
    @Operation(summary = "获得会话内容")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<ChatMemoryDTO> getConversionContent(@RequestParam("id") Long id) {
        ConversionDO conversion = conversionService.getConversion(id);
        String memoryId = "用户" + conversion.getClientUserId() + "_" + conversion.getConversionId();
        return chatHistoryApi.getChatHistory(memoryId);
    }

    @GetMapping("/get-word-cloud")
    @Operation(summary = "获得会话热点词")
    @PreAuthorize("@ss.hasPermission('clientSystem:conversion:query')")
    public CommonResult<String> getConversionContent() {
        if (stringRedisTemplate.opsForValue().get("wordCloudData") == null) {
            List<ConversionDTO> list = conversionService.getConversionRecentWeek();
            String content = "";
            for (ConversionDTO conversion : list) {
                content = content.concat("&" + conversion.getTitle());
            }
            content = content.substring(1);
            System.out.println(content);
            String json = wordCloudApi.wordCloudGen(content).getData();
            stringRedisTemplate.opsForValue().set("wordCloudData", json, Duration.ofDays(1));
        }
        return success(stringRedisTemplate.opsForValue().get("wordCloudData"));
    }
}
