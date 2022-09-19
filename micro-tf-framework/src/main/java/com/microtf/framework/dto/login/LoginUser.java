package com.microtf.framework.dto.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 登录用户
 *
 * @author guliuzhong
 */
@Data
@ApiModel(value = "LoginEntity", description = "接口返回数据对象")
public class LoginUser {
    /**
     * 登录凭据ID
     */
    @ApiModelProperty(value = "登录凭据ID", notes = "邮箱，用户名，微信唯一ID")
    @NotBlank(message = "登录凭据，不能为空")
    String loginId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", notes = "系统通过此ID进行用户定位")
    String userId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户名", notes = "显示的用户名")
    String name;
    /**
     * 登录方式
     */
    LoginType loginType;
    /**
     * 密码
     * 可选
     */
    @NotNull
    @Length(min = 6, message = "密码不合法")
    String passwd;
}
