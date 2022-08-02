package com.microtf.framework.dto;

import com.microtf.framework.dto.common.ResponseList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 带分页的数据
 * @author guliuzhong
 * @param <T>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "PageData",description = "带分页的数据")
public class ResponsePage <T extends Serializable> extends ResponseList<T> {
    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码",example = "1")
    private Integer current;
    /**
     * 每页数据
     */
    @ApiModelProperty(value = "每页数据条数",example = "10")
    private Integer pageSize;
    /**
     * 总数
     */
    @ApiModelProperty(value = "总条数",example = "100")
    private long total;
}
