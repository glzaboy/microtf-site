package com.microtf.framework.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 页面信息返回
 *
 * @author glzab
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "ResponseDto", description = "接口返回数据对象")
public class Response<T extends Serializable> extends BaseResponse implements Serializable {
    /**
     * response data
     */
    @ApiModelProperty(value = "数据", notes = "可以为pageDate数据或直接数据对象")
    private T data;

}
