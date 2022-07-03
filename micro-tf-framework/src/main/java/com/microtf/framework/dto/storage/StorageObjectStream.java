package com.microtf.framework.dto.storage;

import lombok.Data;
import lombok.ToString;

import java.io.BufferedInputStream;
import java.util.Map;

/**
 * 存储数据对象
 */
@Data
@ToString
public class StorageObjectStream {
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 元数据
     */
    private Map<String,String> metaData;
    /**
     * 下载文件时获取的Input对象
     */
    private BufferedInputStream bufferedInputStream;
}
