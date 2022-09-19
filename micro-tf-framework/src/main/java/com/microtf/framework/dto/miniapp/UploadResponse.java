package com.microtf.framework.dto.miniapp;

import lombok.Getter;
import lombok.Setter;

/**
 * 小程序上传返回结果
 *
 * @author glzaboy
 */
@Getter
@Setter
public class UploadResponse extends MiniAppResponse {
    private String url;
    private String id;
}
