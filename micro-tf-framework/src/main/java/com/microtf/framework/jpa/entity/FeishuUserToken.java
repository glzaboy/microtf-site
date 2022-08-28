package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.Entity;

/**
 * 飞书用户存储
 * @author guliuzhong
 */
@Entity
@Getter
@Setter
public class FeishuUserToken  extends AuditEntity {
    /**
     * 应用ID
     */
    @Comment("应用ID")
    String appId;
    /**
     * 用户在应用内的唯一标识
     */
    String openId;
    /**
     * user_access_token，用于获取用户资源
     */
    String accessToken;
    /**
     * 刷新用户 access_token 时使用的 token
     */
    String refreshToken;
}
