package com.microtf.framework.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传返回信息
 * @author glzaboy
 */
@Data
@Builder
public class EditorUpload implements Serializable {
    @JsonProperty("url")
    private String url;
    @JsonProperty("alt")
    private String alt;
    @JsonProperty("href")
    private String href;
}
