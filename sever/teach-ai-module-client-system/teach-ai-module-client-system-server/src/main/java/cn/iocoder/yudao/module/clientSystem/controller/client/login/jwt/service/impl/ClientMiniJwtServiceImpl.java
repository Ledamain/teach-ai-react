package cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.JWTConfiguration;
import cn.iocoder.teach-ai.module.clientSystem.controller.client.login.jwt.service.IClientMiniJwtService;
import cn.iocoder.teach-ai.module.infra.api.config.ConfigApi;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weijiayu
 * @date 2025/4/22 22:09
 */
@Service
public class ClientMiniJwtServiceImpl implements IClientMiniJwtService {

    private static final String JWT_KEY_USER_ID = "userId";
    private static final String JWT_KEY_USERTYPE_ID = "typeId";
    private static final String JWT_KEY_UROLE_ID = "uRole";

    // jwt密钥
    @Value("${token.secret:abcdefghijklmnopqrstuvwxyzaa}")
    private String key;
//    // jwt有效期。单位分钟
//    @Value("${token.expireTime:300}")
//    private int expireTime;

    @Value("${digital-human.app-id}")
    private String appId;

    @Value("${digital-human.app-key}")
    private String appKey;

    @Resource
    private ConfigApi configApi;

    @Override
    public String createToken(String userId) {
        Map<String, Object> payload = new HashMap<>();
//        payload.put(JWTPayload.SUBJECT, userInfo);
        payload.put(JWT_KEY_USER_ID, userId);
        /**
         * @see cn.hutool.jwt.JWTValidator#validateDate(JWTPayload, Date, long)
         */
        payload.put(JWTPayload.EXPIRES_AT, DateTime.now().offset(DateField.MINUTE, JWTConfiguration.EXPIRE_MINUTE));
        return "Bearer " + JWTUtil.createToken(payload, key.getBytes());
    }

//    @Override
//    public String createToken(WxUsersRoleSaveVO wxUsersRoleSaveVO,String userId) {
//        Map<String, Object> payload = new HashMap<>();
////        payload.put(JWTPayload.SUBJECT, userInfo);
//        payload.put(JWT_KEY_USER_ID, userId);
////        payload.put(JWT_KEY_USERTYPE_ID, wxUsersRoleSaveVO.getId());
////        payload.put(JWT_KEY_UROLE_ID, wxUsersRoleSaveVO.getURole());
//        /**
//         * @see cn.hutool.jwt.JWTValidator#validateDate(JWTPayload, Date, long)
//         */
//        payload.put(JWTPayload.EXPIRES_AT, DateTime.now().offset(DateField.MINUTE, expireTime));
//        return "Bearer " + JWTUtil.createToken(payload, key.getBytes());
//    }

    @Override
    public Boolean verifyToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        jwt.setKey(key.getBytes());
        // 校验有效期和签名
//        return jwt.verify();
        return jwt.validate(0);
    }

    @Override
    public String parseUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        Object obj = jwt.getPayload(JWT_KEY_USER_ID);
        return obj == null ? "" : obj.toString();
    }

    @Override
    public String parseTypeId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        Object obj = jwt.getPayload(JWT_KEY_USERTYPE_ID);
        return obj == null ? "-1" : obj.toString();
    }

    @Override
    public String parseURole(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        Object obj = jwt.getPayload(JWT_KEY_UROLE_ID);
        return obj == null ? "" : obj.toString();
    }

    @Override
    public String createDigitalToken() {
        return ClientMiniJwtServiceImpl.createSig(appId, appKey, 3600);
    }

    public static String createSig(String appId, String appKey, int sigExp) {
        // 过期时间：当前时间 + sigExp 秒
        Date expiresDate = DateUtil.offsetSecond(new Date(), sigExp);

        // 创建 HMAC256 签名器（和原代码完全一致）
        JWTSigner signer = JWTSignerUtil.hs256(appKey.getBytes());

        return JWT.create()
                .setIssuedAt(new Date())        // 签发时间
                .setExpiresAt(expiresDate)      // 过期时间
                .setPayload("appId", appId)     // 自定义参数
                .sign(signer);                  // 传入签名器
    }
}
