package com.microtf.framework.jpa.entity;

import com.microtf.framework.jpa.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 站点设置
 *
 * @author glzaboy
 */
@Entity
@Getter
@Setter
public class SiteEntity extends AuditEntity {

    /**
     * 绘本馆名称30个字符
     */
    @Column(nullable = false, length = 30)
    @Comment("绘本馆名称30个字符")
    String name;
    /**
     * 分馆名称
     */
    @Column(length = 30)
    @Comment("分馆名称")
    String subName;

    /**
     * 联系人
     */
    @Column(length = 30)
    @Comment("联系人")
    String concatName;

    /**
     * 联系电话
     */
    @Column(length = 40)
    @Comment("联系电话")
    String telephone;
    /**
     * 绘本馆地址
     */
    @Column(length = 512)
    @Comment("绘本馆地址")
    String address;
    /**
     * 宣传语
     */
    @Column(length = 512)
    @Comment("宣传语")
    String memo;

}
