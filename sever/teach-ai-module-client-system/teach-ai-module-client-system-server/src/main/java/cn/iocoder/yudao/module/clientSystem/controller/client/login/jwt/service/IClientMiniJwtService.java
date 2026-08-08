package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service;


/**
 * @author weijiayu
 * @date 2025/4/22 22:05
 */
public interface IClientMiniJwtService {

    String createToken(String userId);

    Boolean verifyToken(String token);

    String parseUserId(String token);

    String parseTypeId(String token);

    String parseURole(String token);

    String createDigitalToken();
}
