package cn.iocoder.teach-ai.module.clientSystem.controller.client.user;

import cn.iocoder.teach-ai.framework.common.pojo.CommonResult;
import cn.iocoder.teach-ai.framework.common.pojo.PageParam;
import cn.iocoder.teach-ai.framework.common.util.object.BeanUtils;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserPageReqVO;
import cn.iocoder.teach-ai.module.clientSystem.controller.admin.user.vo.UserRespVO;
import cn.iocoder.teach-ai.module.clientSystem.dal.dataobject.user.UserDO;
import cn.iocoder.teach-ai.module.clientSystem.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.teach-ai.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "客户端接口 - 班级")
@RestController
@RequestMapping("/client-api/client-system/user-teacher")
@Validated
public class ClientTeacherController {

    @Resource
    private UserService userService;

    @GetMapping("/get-teacher-list")
    @Operation(summary = "查询教师列表")
    public CommonResult<List<UserRespVO>> getTeacherList() {
        UserPageReqVO pageReqVO = new UserPageReqVO();
        pageReqVO.setClientRole("1");
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserDO> list = userService.getUserPage(pageReqVO).getList();
        return success(BeanUtils.toBean(list, UserRespVO.class));
    }

}
