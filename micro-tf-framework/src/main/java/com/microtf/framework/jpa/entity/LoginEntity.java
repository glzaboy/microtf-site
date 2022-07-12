package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 登录用户
 *
 * @author glzaboy
 */
@Entity
@Getter
@Setter
public class LoginEntity extends AuditEntity {
    @Column(length = 64)
    String appId;
    @Column(unique = true, length = 64)
    @Comment("登录ID")
    String loginId;

    String loginType;
    @ManyToOne
    @JoinColumn(updatable = false)
    UserEntity userEntity;

}
