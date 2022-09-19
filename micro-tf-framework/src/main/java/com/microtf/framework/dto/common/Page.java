package com.microtf.framework.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页参数
 *
 * @author guliuzhong
 * @version 1.0
 */
@Data
public class Page {
    @ApiModelProperty(value = "分页大小", example = "2")
    Integer pageSize;
    @ApiModelProperty(value = "每页条数", example = "20")
    Integer current;
}
