package com.microtf.framework.services.storage.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书云文档根目录
 *
 * @author guliuzhong
 */
@Data
public class RootDirInfo {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("token")
        private String token;
        @JsonProperty("id")
        private String id;
        @JsonProperty("user_id")
        private String userId;
    }
}
