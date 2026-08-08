package cn.iocoder.teach-ai.module.clientSystem.enums;

import cn.iocoder.teach-ai.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * @author 芋道源码
 */
public class ApiConstants {

    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "client-system-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX +  "/client-system";

    public static final String VERSION = "1.0.0";

}
