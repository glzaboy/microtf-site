package com.microtf.framework.services;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.microtf.framework.dto.login.LoginUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * Jwt 签名和验证
 * @author guliuzhong
 */
@Getter
@Setter
public class JwtService {
    /**
     * 有效期时间秒
     */
    private Integer expire;
    /**
     * 密匙
     */
    private String secret;
    /**
     * 签发人
     */
    private String iss;

    /**
     * 签名
     * @param loginUser 用户login结果
     * @return 签名结果Token
     */
    public String signToken(LoginUser loginUser){
        Date expTime = new Date(System.currentTimeMillis() + expire*1000L);
        return JWT.create().withExpiresAt(expTime).withNotBefore(new Date()).withIssuer(iss)
                .withJWTId(DigestUtils.md5DigestAsHex(new Date().toString().getBytes(StandardCharsets.UTF_8)).substring(0,8))
                .withClaim("loginId",loginUser.getLoginId())
                .withClaim("userId",loginUser.getUserId())
                .withClaim("loginType",loginUser.getLoginType().toString())
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * jwt签名解码
     * @param jwtToken 签名结果Token
     * @return jwt未经过有效性验证的结果
     */
    @SuppressWarnings("unused")
    public DecodedJWT decode(String jwtToken){
        return JWT.decode(jwtToken);
    }
    /**
     * 获取有效签名用户
     * @param jwtToken 签名结果Token
     * @return 用户信息
     */
    @SuppressWarnings("unused")
    public Optional<String> getUserId(String jwtToken){
        DecodedJWT decode = getJwtInfo(jwtToken);
        return decode.getAudience().stream().findFirst();
    }
    /**
     * jwt签名解码和有效验证
     * @param jwtToken 签名结果Token
     * @return jwt经过有效性验证的结果
     */
    public DecodedJWT getJwtInfo(String jwtToken) {
        JWTVerifier build = JWT.require(Algorithm.HMAC256(secret)).build();
        return build.verify(jwtToken);
    }
}
