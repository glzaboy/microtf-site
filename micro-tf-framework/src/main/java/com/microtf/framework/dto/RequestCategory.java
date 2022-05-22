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
@ApiModel(value = "RequestCategory",description = "类目查询接口")
public class RequestCategory implements Serializable {
    @ApiModelProperty(value = "类目名称",notes = "首页")
    String name;
    @JsonUnwrapped
    Page page;
}
