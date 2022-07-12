package com.microtf.framework.dto.article;

import com.microtf.framework.dto.site.SiteDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 站点目录
 * @author guliuzhong
 */
@Data
public class CategoryDto implements Serializable {
    /**
     * Id
     */
    @ApiModelProperty(value = "ID")
    Long id;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    String name;
    /**
     * 启用
     */
    @ApiModelProperty(value = "启用")
    Boolean enable;

    @ApiModelProperty(value = "站点")
    SiteDto siteDto;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    Date updateTime;
}
