package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 网站分类
 *
 * @author guliuzhong
 */
@Entity
@Getter
@Setter
public class CategoryEntity extends AuditEntity {
    @Column(length = 30)
    String name;
    /**
     * 启用状态
     */
    @Comment("是否开启")
    Boolean enable;

    @ManyToOne
    @JoinColumn(name = "site_id", updatable = false)
    SiteEntity siteEntity;
    /**
     * 创建时间
     */
    @Comment("创建时间")
    Date createTime;
    /**
     * 更新时间
     */
    @Comment("更新时间")
    Date updateTime;
}
