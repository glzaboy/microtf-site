package com.microtf.framework.dto.login;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前用户信息
 * @author glzaboy
 */
@Data
public class CurrentUser implements Serializable {
    /**
     * 用户名
     */
    String name;
    /**
     * 头偈
     */
    String avatar;
    /**
     * 用户ID
     */
    String userid;
    /**
     * 邮箱
     */
    String email;
    /**
     * 签名
     */
    String signature;
    /**
     * title
     */
    String title;
    /**
     * 组
     */
    String group;
    /**
     * 信息记数
     */
    Integer notifyCount;
    /**
     * 未读记数
     */
    Integer unreadCount;
    /**
     * 国家
     */
    String country;
    /**
     * 权限
     */
    String access;
    /**
     * 地址
     */
    String address;
    /**
     * 电话
     */
    String phone;
//    tags?: { key?: string; label?: string }[];
//    geographic?: {
//        province?: { label?: string; key?: string };
//        city?: { label?: string; key?: string };
//    };
}
