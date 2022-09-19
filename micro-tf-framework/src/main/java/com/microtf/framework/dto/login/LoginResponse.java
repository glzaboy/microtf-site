package com.microtf.framework.dto.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户
 *
 * @author guliuzhong
 */
@Data
@ApiModel(value = "LoginResponse", description = "接口返回数据对象")
public class LoginResponse implements Serializable {
    /**
     * 用户名
     */
    @ApiModelProperty(value = "登录token", notes = "登录token")
    String auth;
    /**
     * 登录状态
     */
    @ApiModelProperty(value = "登录状态", example = "ok")
    private String status;
    /**
     * 登录方式
     */
    @ApiModelProperty(value = "登录方式", example = "mobile")
    private String type;
    /**
     * 用户权限
     */
    @ApiModelProperty(value = "用户权限", example = "admin")
    private String currentAuthority;
}
