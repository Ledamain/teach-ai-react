package cn.iocoder.teach-ai.module.system.enums.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleTypeEnum {

    /**
     * 内置角色
     */
    SYSTEM(1),
    /**
     * 自定义角色
     */
    CUSTOM(2);

    private final Integer type;

}
