package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FsUserLoginResponse {


    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private Integer expiresIn;
        @JsonProperty("name")
        private String name;
        @JsonProperty("en_name")
        private String enName;
        @JsonProperty("avatar_url")
        private String avatarUrl;
        @JsonProperty("avatar_thumb")
        private String avatarThumb;
        @JsonProperty("avatar_middle")
        private String avatarMiddle;
        @JsonProperty("avatar_big")
        private String avatarBig;
        @JsonProperty("open_id")
        private String openId;
        @JsonProperty("union_id")
        private String unionId;
        @JsonProperty("email")
        private String email;
        @JsonProperty("enterprise_email")
        private String enterpriseEmail;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("mobile")
        private String mobile;
        @JsonProperty("tenant_key")
        private String tenantKey;
        @JsonProperty("refresh_expires_in")
        private Integer refreshExpiresIn;
        @JsonProperty("refresh_token")
        private String refreshToken;
    }
}

