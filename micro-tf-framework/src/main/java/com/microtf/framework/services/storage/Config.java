package com.microtf.framework.services.storage;

import com.microtf.framework.dto.SettingDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config extends SettingDto {
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
    /**
     * 引擎处理类
     */
    private String canonicalName;
    /**
     * 用户ID，有个别存储除ak,sk外可能以用户身份来执行
     */
    private String openUserId;
}
