package com.microtf.framework.dto.storage;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * 存储数据对象
 */
@Data
@ToString
public class StorageObject {
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 对象地址
     */
    private String url;
    /**
     * 元数据
     */
    private Map<String,String> metaData;
}
