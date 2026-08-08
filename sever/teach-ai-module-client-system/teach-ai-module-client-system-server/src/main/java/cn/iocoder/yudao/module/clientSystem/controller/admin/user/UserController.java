package cn.iocoder.teach-ai.module.clientSystem.controller.admin.user;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.*;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.module.clientSystem.service.user.UserService;

@Tag(name = "管理后台 - 客户端用户")
@RestController
@RequestMapping("/client-system/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/create")
    @Operation(summary = "创建客户端用户")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserSaveReqVO createReqVO) {
        return success(userService.createUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户端用户")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserSaveReqVO updateReqVO) {
        userService.updateUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户端用户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('clientSystem:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除客户端用户")
                @PreAuthorize("@ss.hasPermission('clientSystem:user:delete')")
    public CommonResult<Boolean> deleteUserList(@RequestParam("ids") List<Long> ids) {
        userService.deleteUserListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得客户端用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        UserDO user = userService.getUser(id);
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        userVO.setNicknameWithNum(user.getNickname() + "-" + user.getClientNum());
        return success(userVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户端用户分页")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageReqVO) {
        PageResult<UserDO> pageResult = userService.getUserPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户端用户 Excel")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserExcel(@Valid UserPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserDO> list = userService.getUserPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "客户端用户.xls", "数据", UserRespVO.class,
                        BeanUtils.toBean(list, UserRespVO.class));
    }

    @GetMapping("/get-user-count")
    @Operation(summary = "获得用户数量")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:query')")
    public CommonResult<Long> getUserCount() {
        Long count = userService.getUserCount();
        return success(count);
    }

    @GetMapping("/get-student-list")
    @Operation(summary = "查询学生列表")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:query')")
    public CommonResult<List<UserRespVO>> getStudentList() throws IOException {
        UserPageReqVO pageReqVO = new UserPageReqVO();
        pageReqVO.setClientRole("0");
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserDO> list = userService.getUserPage(pageReqVO).getList();
        List<UserRespVO> userRespVOList = BeanUtils.toBean(list, UserRespVO.class);
        userRespVOList.forEach(item -> {
            item.setNicknameWithNum(item.getNickname() + "-" + item.getClientNum());
        });
        return success(userRespVOList);
    }

    @GetMapping("/get-teacher-list")
    @Operation(summary = "查询教师列表")
    @PreAuthorize("@ss.hasPermission('clientSystem:user:query')")
    public CommonResult<List<UserRespVO>> getTeacherList() throws IOException {
        UserPageReqVO pageReqVO = new UserPageReqVO();
        pageReqVO.setClientRole("1");
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserDO> list = userService.getUserPage(pageReqVO).getList();
        List<UserRespVO> userRespVOList = BeanUtils.toBean(list, UserRespVO.class);
        userRespVOList.forEach(item -> {
            item.setNicknameWithNum(item.getNickname() + "-" + item.getClientNum());
        });
        return success(userRespVOList);
    }

}
