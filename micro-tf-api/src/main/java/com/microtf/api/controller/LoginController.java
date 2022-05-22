package com.microtf.api.controller;

import com.microtf.framework.annotations.Login;
import com.microtf.framework.dto.Response;
import com.microtf.framework.dto.login.CurrentUser;
import com.microtf.framework.dto.login.LoginResponse;
import com.microtf.framework.dto.login.LoginStateDto;
import com.microtf.framework.dto.login.LoginUser;
import com.microtf.framework.exceptions.LoginException;
import com.microtf.framework.services.LoginService;
import com.microtf.framework.services.UserService;
import com.microtf.framework.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 用户信息
 * @author guliuzhong
 */
@Api(value = "用户",tags = "login")
@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {
    LoginService loginService;

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login",method = {RequestMethod.POST},produces = {"application/json"})
    @ApiResponse(code = 200,message = "登录成功")
    public LoginResponse login(@RequestBody LoginUser loginUserIn){
        LoginResponse loginUserOut=new LoginResponse();
        loginUserOut.setType(loginUserIn.getLoginType().toString());
        try {
            Optional<LoginUser> login = loginService.login(loginUserIn);
            if(login.isPresent()){
                LoginUser loginUser = login.get();
                loginUserOut.setType(loginUser.getLoginType().toString());
                String token = loginService.signToken(loginUser);
                loginUserOut.setStatus("ok");
                loginUserOut.setAuth(token);
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                assert requestAttributes != null;
                HttpServletResponse response = requestAttributes.getResponse();
                Cookie cookie=new Cookie("auth",token);
                cookie.setMaxAge(86400);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                assert response != null;
                response.addCookie(cookie);
            }
        } catch (LoginException e) {
            log.warn("有用户登录但是失败了原因{}",e.getMessage());
            loginUserOut.setStatus("error");
        }
        return loginUserOut;
    }
    @Login
    @RequestMapping(value = "/currentUser",method = {RequestMethod.GET},produces = {"application/json"})
    public Response<CurrentUser> currentUser(){
        CurrentUser currentUser=new CurrentUser();
        LoginStateDto loginStateDto = loginService.getLoginStateDto();
        userService.getUserDto(loginStateDto.getUserId()).ifPresent(user->{
            currentUser.setName(user.getName());
            currentUser.setUserid(String.valueOf(user.getId()));
            currentUser.setEmail(user.getEmail());
            currentUser.setAvatar(user.getAvatar());

        });
        return ResponseUtil.responseData(currentUser);
    }
    @Login
    @RequestMapping(value = "/logout",method = {RequestMethod.GET},produces = {"application/json"})
    public Response<String> logout(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Cookie cookie=new Cookie("auth",null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        assert requestAttributes != null;
        assert requestAttributes.getResponse() != null;
        requestAttributes.getResponse().addCookie(cookie);
        return ResponseUtil.responseData("OK");
    }
}
