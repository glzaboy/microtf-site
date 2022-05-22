package com.microtf.framework.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 文章内容
 *
 * @author guliuzhong
 */
@Getter
@Setter
@Entity
public class ArticleContentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "native", strategy = "native")
    private Integer contentId;
    @Comment("html内容")
    @Lob
    private String html;
}