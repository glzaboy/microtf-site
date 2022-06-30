package com.microtf.framework.services.storage;

import lombok.Data;

@Data
public class Config {
    private String accessKeyId;
    private String secretAccessKey;
    private String bucket;
    private String rootPath;
    private String endPoint;
    private String region;
}
