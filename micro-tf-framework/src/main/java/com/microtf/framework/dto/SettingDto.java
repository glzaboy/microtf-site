package com.microtf.framework.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 设置数据基础类
 *
 * @author guliuzhong
 */
@SuppressWarnings("unused")
public class SettingDto implements Serializable {
    @Getter
    @Setter
    Boolean read;
}
