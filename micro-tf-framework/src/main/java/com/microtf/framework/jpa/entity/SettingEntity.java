package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@Getter
@Setter
public final class SettingEntity extends AuditEntity {
    /**
     * 应用class限定命名
     */
    String className;
    @Column(length = 32)
    String name;
    /**
     * 设置值
     * 值只能为字符串，支持大文本
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Comment("JSON格式数据")
    String value;
}
