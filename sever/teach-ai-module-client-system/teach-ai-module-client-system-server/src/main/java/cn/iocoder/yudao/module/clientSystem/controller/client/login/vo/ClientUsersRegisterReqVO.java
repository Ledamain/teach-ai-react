package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.fhs.core.trans.vo.VO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Schema(description = "微信小程序 - 微信用户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClientUsersRegisterReqVO implements VO {

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "teach-aiyuanma")
    @NotEmpty(message = "登录昵称不能为空")
    private String nickname;

    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "teach-aiyuanma")
    @NotEmpty(message = "登录账号不能为空")
    @Length(min = 4, max = 30, message = "账号长度为 4-30 位")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,30}$", message = "账号格式为数字以及字母")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buzhidao")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;
}
