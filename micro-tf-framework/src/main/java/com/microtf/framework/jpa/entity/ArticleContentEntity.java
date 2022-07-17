package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
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
public class ArticleContentEntity extends AuditEntity {
    @Comment("html内容")
    @Lob
    private String html;
}