package cn.iocoder.teach-ai.module.infra.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageResult;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.DigitalVideoApi;
import cn.iocoder.teach-ai.module.clientChat.api.digitalvideo.dto.DigitalVideoFileDTO;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.FileIngestionApi;
import cn.iocoder.teach-ai.module.clientChat.api.fileIngestion.dto.FileIngestionDTO;
import cn.iocoder.teach-ai.module.clientChat.enums.fileEmbedding.FileTypeEnum;
import cn.iocoder.teach-ai.module.infra.controller.admin.file.vo.file.*;
import cn.iocoder.teach-ai.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.teach-ai.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;
import static cn.iocoder.teach-ai.module.infra.framework.file.core.utils.FileTypeUtils.writeAttachment;

@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private FileIngestionApi fileIngestionApi;

    @Resource
    private DigitalVideoApi digitalVideoApi;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    public CommonResult<String> uploadFile(@Valid FileUploadReqVO uploadReqVO,  @RequestHeader(value = "fileflag", required = false) String fileflag) throws Exception {
        MultipartFile file = uploadReqVO.getFile();

        byte[] content = IoUtil.readBytes(file.getInputStream());
        String fileUrl = fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), file.getContentType());

        // TODO 判断是否为文件客户端文件上传 (后续使用消息队列)
        if (fileflag != null){
            String flag = fileflag.substring(14);
            if (flag.startsWith("PPT_FILE_UPLOAD")){
                String pptMemoryId = flag.substring(16);
                pptMemoryId = URLDecoder.decode(pptMemoryId,StandardCharsets.UTF_8);
                fileIngestionApi.fileIngest(new FileIngestionDTO(file, pptMemoryId,null,fileUrl));
            }else {
                String memoryId = flag;
                memoryId = URLDecoder.decode(memoryId, StandardCharsets.UTF_8);

                if (!memoryId.isEmpty())
                    fileIngestionApi.fileIngest(new FileIngestionDTO(file, memoryId,null,fileUrl));
            }
        }

        return success(fileUrl);
    }

    @PostMapping("/upload-ppt")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    public CommonResult<String> uploadFile(
            // 1. 文件必须单独用 @RequestParam 接收
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "fileflag", required = false) String fileflag
    ) throws Exception {

        // 2. 手动 set 进 DTO
        DigitalVideoFileDTO uploadReqVO = new DigitalVideoFileDTO();
        uploadReqVO.setFile(file);

        // 3. 调用远程接口
        return digitalVideoApi.uploadPptx(uploadReqVO);
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Parameters({
            @Parameter(name = "name", description = "文件名称", required = true),
            @Parameter(name = "directory", description = "文件目录")
    })
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<FileRespVO> getFile(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(fileService.getFile(id), FileRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
        fileService.deleteFile(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除文件")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFileList(@RequestParam("ids") List<Long> ids) throws Exception {
        fileService.deleteFileList(ids);
        return success(true);
    }

    @GetMapping("/{configId}/get/**")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "下载文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable("configId") Long configId) throws Exception {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        // 解码，解决中文路径的问题
        // https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        // https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/1432/
        path = URLUtil.decode(path, StandardCharsets.UTF_8, false);

        // 读取内容
        byte[] content = fileService.getFileContent(configId, path);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        writeAttachment(response, path, content);
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<PageResult<FileRespVO>> getFilePage(@Valid FilePageReqVO pageVO) {
        PageResult<FileDO> pageResult = fileService.getFilePage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileRespVO.class));
    }

}
