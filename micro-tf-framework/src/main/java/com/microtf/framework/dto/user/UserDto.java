package com.microtf.framework.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 * @author glzaboy
 */
@Data
public class UserDto implements Serializable {
    Integer id;
    /**
     * 显示名
     */
    String name;
    Date createDate;
    /**
     * 电话
     */
    String phone;
    /**
     * 邮箱
     */
    String email;
    /**
     * 头像
     */
    String avatar;
}
