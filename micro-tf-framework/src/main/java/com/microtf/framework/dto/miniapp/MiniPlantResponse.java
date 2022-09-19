package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microtf.framework.services.miniapp.baiduAi.PlantResult;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 植物识别返回给小程序结果
 *
 * @author glzaboy
 */
@Getter
@Setter
public class MiniPlantResponse extends MiniAppResponse {

    @JsonProperty("result")
    private List<PlantResult.ResultDTO> result;
    @JsonProperty("log_id")
    private Long logId;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("score")
        private Double score;
        @JsonProperty("name")
        private String name;
        @JsonProperty("baike_info")
        private PlantResult.ResultDTO.BaikeInfoDTO baikeInfo;

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
