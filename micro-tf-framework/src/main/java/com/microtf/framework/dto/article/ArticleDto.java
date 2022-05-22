package com.microtf.framework.dto.article;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.microtf.framework.dto.site.SiteDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章
 * @author guliuzhong
 */
@Data
public class ArticleDto implements Serializable {
    /**
     * Id
     */
    @ApiModelProperty(value = "ID")
    Integer id;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    String title;
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    @JsonUnwrapped
    ArticleContentDto content;
    /**
     * 类目ID
     */
    List<Integer> categoryId=new ArrayList<>();

    List<CategoryDto> categoryDtoList=new ArrayList<>();

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
