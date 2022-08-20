package com.microtf.framework.services.miniapp.baiduAi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PlantResult  {

    @JsonProperty("result")
    private List<ResultDTO> result;
    @JsonProperty("log_id")
    private Long logId;
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("error_msg")
    private String errorMsg;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("score")
        private Double score;
        @JsonProperty("name")
        private String name;
        @JsonProperty("baike_info")
        private BaikeInfoDTO baikeInfo;

        @NoArgsConstructor
        @Data
        public static class BaikeInfoDTO {
            @JsonProperty("baike_url")
            private String baikeUrl;
            @JsonProperty("image_url")
            private String imageUrl;
            @JsonProperty("description")
            private String description;
        }
    }
}
