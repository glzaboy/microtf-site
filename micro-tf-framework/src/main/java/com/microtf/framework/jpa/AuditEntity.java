package com.microtf.framework.jpa;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 基础数据表
 * 包含数据库审计和版本功能
 *
 * @author glzaboy
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(nullable = false)
    @Comment("主键")
    private Long id;
    @CreatedDate
    @Comment("创建时间")
    Date createTime;
    @CreatedBy
    @Comment("创建人ID")
    Long createdBy;
    @LastModifiedDate
    @Comment("更新时间")
    Date updateTime;
    @LastModifiedBy
    @Comment("更新人ID")
    Long updateBy;
    @Version
    @Comment("版本")
    Long version;
}
