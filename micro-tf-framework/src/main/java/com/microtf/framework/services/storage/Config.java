package com.microtf.framework.services.storage;

import lombok.Data;

@Data
public class Config {
    /**
     * accessKeyId
     */
    private String accessKeyId;
    /**
     * secretAccessKey
     */
    private String secretAccessKey;
    /**
     * 存储桶
     */
    private String bucket;
    /**
     * 文件存储点
     * 文件存储位置
     */
    private String rootPath;
    /**
     * api域名
     * 如 http://s3-cn-east-1.qiniucs.com/
     */
    private String endPoint;
    /**
     * 存储区域
     * cn-east-1
     */
    private String region;
    /**
     * 私有存储，获取此桶文件需要签名才能访问
     */
    private Boolean isPrivate;
    /**
     * 私有存储，获取此桶文件需要签名才能访问
     */
    private int expiry;
    /**
     * 用户访问桶域名地址
     */
    private String urlHost;
}
