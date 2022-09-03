package com.microtf.framework.services.miniapp.baiduAi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 百度ai ocr 接口识别结果
 * @author glzaboy
 */
@NoArgsConstructor
@Data
public class BodySegResult {



    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("error_msg")
    private String errorMsg;
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
    private List<PersonInfoDTO> personInfo;

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
