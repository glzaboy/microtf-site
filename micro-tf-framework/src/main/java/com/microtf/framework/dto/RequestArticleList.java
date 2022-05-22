package com.microtf.framework.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.microtf.framework.dto.common.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author guliuzhong
 */
@Data
@ApiModel(value = "RequestArticleList",description = "文章接口")
public class RequestArticleList implements Serializable {
    @ApiModelProperty(value = "名称",notes = "首页")
    String title;
    @JsonUnwrapped
    Page page;
}
