package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microtf.framework.services.miniapp.baiduAi.BodySegResult;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 小程序OCR接口返回
 *
 * @author glzaboy
 */
@Getter
@Setter
public class MiniBodySegResponse extends MiniAppResponse {
    @JsonProperty("log_id")
    private Long logId;
    @JsonProperty("labelmap")
    private String labelmap;
    @JsonProperty("scoremap")
    private String scoremap;
    @JsonProperty("foreground")
    private String foreground;
    @JsonProperty("person_num")
    private Integer personNum;
    @JsonProperty("person_info")
    private List<BodySegResult.PersonInfoDTO> personInfo;

    @NoArgsConstructor
    @Data
    public static class PersonInfoDTO {
        @JsonProperty("height")
        private Double height;
        @JsonProperty("width")
        private Double width;
        @JsonProperty("top")
        private Double top;
        @JsonProperty("score")
        private Double score;
        @JsonProperty("left")
        private Double left;
    }
}
