package com.microtf.framework.dto.site;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 站点信息
 *
 * @author glzaboy
 */
@Data
public class SiteDto implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 绘本馆名称30个字符
     */
    @NotBlank(message = "绘本馆名称不能为空")
    @ApiModelProperty(value = "绘本馆", notes = "30个字符")
    String name;
    /**
     * 分馆名称
     */
    @NotBlank(message = "分馆名称不能为空")
    @ApiModelProperty(value = "分馆名称", notes = "不能为空")
    String subName;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    String concatName;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    String telephone;
    /**
     * 绘本馆地址
     */
    @ApiModelProperty(value = "绘本馆地址")
    String address;
    /**
     * 宣传语
     */
    @ApiModelProperty(value = "宣传语")
    String memo;
}
