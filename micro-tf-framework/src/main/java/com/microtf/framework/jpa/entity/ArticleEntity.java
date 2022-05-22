package com.microtf.framework.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

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
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Integer id;
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
    @OneToOne
    ArticleContentEntity content;
    Date createTime;
    Date updateTime;
}