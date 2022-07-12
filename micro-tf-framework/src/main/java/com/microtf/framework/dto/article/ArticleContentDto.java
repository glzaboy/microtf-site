package com.microtf.framework.dto.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章内容
 * @author guliuzhong
 */
@Data
public class ArticleContentDto implements Serializable {
    /**
     * Id
     */
    @ApiModelProperty(value = "ID")
    Long contentId;
    /**
     * 启用
     */
    @ApiModelProperty(value = "html")
    String html;
}
