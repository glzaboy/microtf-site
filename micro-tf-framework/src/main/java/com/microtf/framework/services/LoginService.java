package com.microtf.framework.services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.microtf.framework.dto.login.LoginStateDto;
import com.microtf.framework.dto.login.LoginType;
import com.microtf.framework.dto.login.LoginUser;
import com.microtf.framework.exceptions.LoginException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 登录控制器
 *
 * @author glzaboy
 */
@Validated
@Slf4j
public class LoginService {

    private final ThreadLocal<LoginStateDto> loginUserThreadLocal = new ThreadLocal<>();

    JwtService jwtService;
    @Getter
    @Setter
    Boolean enableGuest;

    @Setter
    List<LoginAuth> loginAuths = new ArrayList<>();


    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * 登录用户
     *
     * @param loginUser 登录对象
     * @return 返回登录后的token
     */
    public Optional<LoginUser> login(@NotNull @Valid LoginUser loginUser) throws LoginException {
        Optional<LoginAuth> first = loginAuths.stream().filter(item -> item.getLoginType().equals(loginUser.getLoginType())).findFirst();
        if (first.isEmpty()) {
            log.error("不支持的登录方式");
            throw new LoginException("不支持的登录方式");
        }
        return first.get().login(loginUser);

    }

    /**
     * 登录用户
     *
     * @param loginUser 登录对象
     * @return 返回登录后的token
     */
    public String signToken(LoginUser loginUser) {
        return jwtService.signToken(loginUser);
    }

    public void initLoginUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        String authorization = request.getHeader("Authorization");
        Optional<String> jwtOption = Optional.empty();
        String bearer = "Bearer ";
        if (authorization != null && authorization.startsWith(bearer)) {
            jwtOption = Optional.of(authorization.replace(bearer, ""));
        }
        if (jwtOption.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                LoginStateDto.LoginStateDtoBuilder loginStateDtoBuilder = LoginStateDto.builder();
                loginStateDtoBuilder.guest(true);
                loginUserThreadLocal.set(loginStateDtoBuilder.build());
                return;
            }
            Optional<Cookie> auth = Arrays.stream(request.getCookies()).filter(item -> "auth".equals(item.getName())).findFirst();
            if (auth.isPresent()) {
                Cookie cookie = auth.get();
                jwtOption = Optional.ofNullable(cookie.getValue());
            }
        }
        if (jwtOption.isPresent()) {
            try {
                DecodedJWT jwtInfo = jwtService.getJwtInfo(jwtOption.get());
                LoginStateDto.LoginStateDtoBuilder loginStateDtoBuilder = LoginStateDto.builder();
                if (jwtInfo != null) {
                    loginStateDtoBuilder.loginId(jwtInfo.getClaim("loginId").asString())
                            .userId(jwtInfo.getClaim("userId").asString())
                            .loginType(jwtInfo.getClaim("loginType").as(LoginType.class)).guest(false);
                } else {
                    loginStateDtoBuilder.guest(true);
                }
                loginUserThreadLocal.set(loginStateDtoBuilder.build());
            } catch (JWTVerificationException exception) {
                log.error("解码出错,设置用户为Guest原因{}", exception.getMessage());
                LoginStateDto.LoginStateDtoBuilder loginStateDtoBuilder = LoginStateDto.builder();
                loginStateDtoBuilder.guest(true);
                loginUserThreadLocal.set(loginStateDtoBuilder.build());
            }
        } else {
            LoginStateDto.LoginStateDtoBuilder loginStateDtoBuilder = LoginStateDto.builder();
            loginStateDtoBuilder.guest(true);
            loginUserThreadLocal.set(loginStateDtoBuilder.build());
        }
    }

    /**
     * 获取登录用户对象
     *
     * @return 登录的用户
     * @author guliuzhong
     */
    public LoginStateDto getLoginStateDto() {
        LoginStateDto loginStateDto = loginUserThreadLocal.get();
        if (loginStateDto != null) {
            return loginStateDto;
        }
        LoginStateDto.LoginStateDtoBuilder loginStateDtoBuilder = LoginStateDto.builder();
        loginStateDtoBuilder.guest(true);
        loginUserThreadLocal.set(loginStateDtoBuilder.build());
        return loginStateDtoBuilder.build();
    }

    /**
     * 获取登录用户ID
     *
     * @return 登录的用户
     * @author guliuzhong
     */
    public String getUserId() {
        return getLoginStateDto().getUserId();
    }

    /**
     * 判断用户是否是访客
     * 判断标准是否登录
     *
     * @return 是否是访客
     * @author guliuzhong
     */
    public boolean isGuest() {
        return getLoginStateDto().getGuest();
    }

    public void removeLoginUser() {
        loginUserThreadLocal.remove();
    }
}
