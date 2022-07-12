package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 导航
 *
 * @author guliuzhong
 */
@Entity
@Getter
@Setter
public class NavEntity extends AuditEntity {
    /**
     * 名称
     */
    @Column(length = 64)
    @Comment("名称")
    String name;
    @Comment("链接地址")
    String link;
    @Column(length = 20)
    @Comment("链接类型")
    String type;
    @ManyToOne
    @JoinColumn(name = "site_id", updatable = false)
    SiteEntity siteEntity;
}
