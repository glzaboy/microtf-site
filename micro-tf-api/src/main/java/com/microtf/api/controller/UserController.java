package com.microtf.api.controller;

import com.microtf.framework.annotations.Login;
import com.microtf.framework.dto.Response;
import com.microtf.framework.dto.user.UserDto;
import com.microtf.framework.exceptions.BizException;
import com.microtf.framework.services.UserService;
import com.microtf.framework.utils.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


/**
 * 用户控制器
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy</a>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Login
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    public Response<UserDto> info() {
        Optional<UserDto> loginUser = userService.getCurrentUserDto();
        if (loginUser.isPresent()) {
            return ResponseUtil.responseData(loginUser.get());
        } else {
            throw new BizException("用户不存在");
        }
    }

    @Login
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    @ApiOperation("保存用户信息")
    public Response<String> save(@RequestBody UserDto userDto) {
        Optional<UserDto> loginUser = userService.getCurrentUserDto();
        if (loginUser.isPresent()) {
            UserDto userDto1 = loginUser.get();
            userDto.setId(userDto1.getId());
            userService.save(userDto);
            return ResponseUtil.responseData("成功");
        } else {
            throw new BizException("用户不存在");
        }
    }
}
