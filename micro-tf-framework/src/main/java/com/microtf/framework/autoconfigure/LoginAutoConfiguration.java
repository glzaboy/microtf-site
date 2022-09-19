package com.microtf.framework.autoconfigure;

import com.auth0.jwt.JWT;
import com.microtf.framework.compontent.LoginAop;
import com.microtf.framework.services.JwtService;
import com.microtf.framework.services.LoginAuth;
import com.microtf.framework.services.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;

/**
 * JWT自动配置
 *
 * @author glzab
 */
@ConditionalOnClass({JWT.class, LoginAop.class})
@EnableConfigurationProperties(LoginProperties.class)
@Slf4j
public class LoginAutoConfiguration {
    @Bean
    public JwtService jwtService(LoginProperties jwtProperties) {
        log.info("自动配置JwtService");
        JwtService jwtService = new JwtService();
        if (jwtProperties.getExpires() == null || jwtProperties.getExpires() <= 0) {
            jwtService.setExpire(86400);
        } else {
            jwtService.setExpire(jwtProperties.getExpires());
        }
        if (jwtProperties.getSecret() == null || "".equals(jwtProperties.getSecret())) {
            jwtService.setSecret(UUID.randomUUID().toString());
            log.info("生成jwt secret {}", jwtService.getSecret());
        } else {
            jwtService.setSecret(jwtProperties.getSecret());
        }

        jwtService.setIss(jwtProperties.getIss());
        return jwtService;
    }

    @Bean
    LoginService loginService(JwtService jwtService, LoginProperties jwtProperties, List<LoginAuth> loginAuths) {
        log.info("自动配置loginService");
        LoginService loginService = new LoginService();
        loginService.setEnableGuest(jwtProperties.getEnableGuest());
        loginService.setJwtService(jwtService);
        loginService.setLoginAuths(loginAuths.stream().filter(item -> jwtProperties.getEnableLoginType().contains(item.getLoginType())).toList());
        return loginService;
    }

}
