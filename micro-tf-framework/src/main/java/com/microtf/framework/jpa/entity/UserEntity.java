package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户信息
 *
 * @author <a href="mailto:glzaboy@163.com">glzaboy@163.com</a>
 */
@Getter
@Setter
@Entity
public class UserEntity extends AuditEntity {
    /**
     * 显示名
     */
    @Column(length = 32)
    @Comment("显示名")
    String name;
    @Column(length = 64)
    @Comment("登录密匙")
    String loginToken;

    Date createDate;
    /**
     * 电话
     */
    @Column(length = 20)
    String phone;

    @ManyToOne
    @JoinColumn(name = "site_id", updatable = false)
    SiteEntity siteEntity;
    /**
     * 邮箱
     */
    @Column(length = 80)
    @Comment("邮箱")
    String email;
    /**
     * 头像
     */
    @Comment("头像")
    String avatar;

}
