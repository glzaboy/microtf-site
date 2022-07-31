package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 文章内容
 *
 * @author guliuzhong
 */
@Getter
@Setter
@Entity
public class ArticleEntity extends AuditEntity {
    /**
     * 标题
     */
    @Comment("标题")
    private String title;
    @ManyToMany
    @JoinTable(name = "ArticleEntity_categoryEntityList",
            joinColumns = @JoinColumn(name = "ArticleEntity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "categoryEntityList_id", referencedColumnName = "id"))
    List<CategoryEntity> categoryEntityList = new java.util.ArrayList<>();
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn
    ArticleContentEntity content;
    Date createTime;
    Date updateTime;
    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(updatable = false)
    SiteEntity siteEntity;
}