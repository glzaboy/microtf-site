package com.microtf.framework.services.storage.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书小程序上传结果
 * @author  glzaboy
 */
@NoArgsConstructor
@Data
public class UploadInfo {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("file_token")
        private String fileToken;
    }
}
